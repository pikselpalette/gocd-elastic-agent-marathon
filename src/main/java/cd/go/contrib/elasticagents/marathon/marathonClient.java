/*
 * Copyright 2017 Piksel, Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cd.go.contrib.elasticagents.marathon;

import cd.go.contrib.elasticagents.marathon.marathon.MarathonApp;
import cd.go.contrib.elasticagents.marathon.marathon.MarathonClient;
import mesosphere.marathon.client.Marathon;
import mesosphere.marathon.client.model.v2.App;

import java.util.*;

import static cd.go.contrib.elasticagents.marathon.MarathonPlugin.LOG;

public class marathonClient {

    private static Marathon marathon;
    private String prefix;
    private String marathonUrl;

    public marathonClient () {}

    public marathonClient (PluginSettings settings) {
        setMarathonUrl(settings.getMarathonUrl());
        setPrefix(settings.getMarathonPrefix());

        if (marathon == null) {
            if (settings.getMarathonUsername() != null && settings.getMarathonPassword() != null) {
                marathon = MarathonClient.getInstanceWithBasicAuth(
                        getMarathonUrl(),
                        settings.getMarathonUsername(),
                        settings.getMarathonPassword()
                );
            } else {
                marathon = MarathonClient.getInstance(getMarathonUrl());
            }
        }
    }

    @SuppressWarnings("unused")
    public String getPrefix() {
        return prefix;
    }

    @SuppressWarnings("unused")
    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    @SuppressWarnings("unused")
    public String getMarathonUrl() {
        return marathonUrl;
    }

    @SuppressWarnings("unused")
    public void setMarathonUrl(String marathonUrl) {
        this.marathonUrl = marathonUrl;
    }

    public void terminate(String name) {
        try {
            marathon.deleteApp(name);
        } catch (Exception e) {
            LOG.warn("Couldn't terminate app " + name + ": " + e.getMessage(), e);
        }
    }

    public List<MarathonInstance> getGoAgents(PluginSettings settings) {
        List<MarathonInstance> appList = new ArrayList<>();
        try {
            for (App app: marathon.getGroup(getPrefix()).getApps()) {
                app.setTasks(marathon.getAppTasks(app.getId()).getTasks());
                try {
                    appList.add(MarathonInstance.instanceFromApp((MarathonApp)app, settings));
                } catch (Exception f) {
                    LOG.error("instanceFromApp failed: " + f.toString(), f);
                }
            }
        } catch (Exception e) {
            LOG.error("GetGroup Failed: " + e.toString(), e);
        }
        return appList;
    }

    public MarathonInstance requestGoAgent(MarathonInstance instance) {

        MarathonApp app = instance.getApp();

        MarathonApp existing = null;
        try {
            existing = (MarathonApp)marathon.getApp(app.getId()).getApp();
        } catch (Exception first) {
            LOG.debug("No app exists for " + app.getId() + ", creating");
        }

        if (existing != null) {
            if (app.equals(existing)) {
                LOG.info("App already exists: " + existing);
                app.setTasks(marathon.getAppTasks(app.getId()).getTasks());
                return instance;
            }

            try {
                marathon.updateApp(app.getId(), app);
                return instance;
            } catch (Exception second) {
                LOG.error("Update failed: " + second.getMessage());
                return instance;
            }
        }

        try {
            marathon.createApp(app);
            return instance;
        } catch (Exception third) {
            LOG.error("Create failed: " + third.getMessage());
            return null;
        }
    }
}
