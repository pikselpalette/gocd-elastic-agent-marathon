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

import cd.go.contrib.elasticagents.marathon.utils.InstanceProperties;
import com.google.common.collect.Iterables;
import mesosphere.marathon.client.model.v2.App;
import mesosphere.marathon.client.model.v2.Container;
import mesosphere.marathon.client.model.v2.Docker;
import mesosphere.marathon.client.model.v2.Task;
import org.joda.time.DateTime;
import org.joda.time.Period;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static cd.go.contrib.elasticagents.marathon.utils.Util.mapFromString;
import static cd.go.contrib.elasticagents.marathon.utils.Util.stringFromMap;
import static cd.go.contrib.elasticagents.marathon.MarathonPlugin.LOG;

/*
Translation layer for MarathonApp to MarathonInstance
 */

public class InstanceBuilder {
    private MarathonInstance instance;
    private App app;

    public InstanceBuilder(InstanceProperties instanceProperties) {
        buildApp(instanceProperties);
        buildInstance(instanceProperties);
        buildApp(instanceProperties);
    }

    public void buildApp(InstanceProperties instanceProperties) {
        Docker docker = new Docker();
        docker.setImage(instanceProperties.getImage());
        docker.setNetwork("HOST");

        Container container = new Container();
        container.setType("DOCKER");
        container.setDocker(docker);

        App app = new App();
        app.setMem(instanceProperties.getMemory());
        app.setCpus(instanceProperties.getCpus());
        app.setId(instanceProperties.getMarathonPrefix() + instanceProperties.getName());
        app.setInstances(1);
        app.setContainer(container);
        app.setCmd(instanceProperties.getCommand());

        Map<String, String> envVars = new HashMap<>();
        envVars.put("marathonUrl", instanceProperties.getMarathonUrl());
        envVars.put("marathonUsername", instanceProperties.getMarathonUsername());
        envVars.put("marathonPassword", instanceProperties.getMarathonPassword());
        envVars.put("marathonPrefix", instanceProperties.getMarathonPrefix());
        envVars.put("maxInstances", instanceProperties.getMaxInstances().toString());
        envVars.put("autoRegisterPeriod", instanceProperties.getAutoRegisterPeriod().toString());
        envVars.put("autoRegisterTimeout", instanceProperties.getAutoRegisterTimeout().toString());


        envVars.put("GO_EA_SERVER_URL", instanceProperties.getGoServerUrl());
        envVars.put("GO_EA_AUTO_REGISTER_ENVIRONMENT", instanceProperties.getEnvironment());
        envVars.put("GO_EA_GUID", "marathon." + instanceProperties.getName());
        envVars.putAll(instanceProperties.getAutoRegisterProperties());

        app.setEnv(envVars);

        setApp(app);
    }

    public void buildInstance(InstanceProperties instanceProperties) {

        Map<String, String> extraProperties = new HashMap<>();
        extraProperties.put("goServerUrl", instanceProperties.getGoServerUrl());
        extraProperties.put("autoRegisterTimeout", instanceProperties.getAutoRegisterTimeout().toString());
        extraProperties.put("marathonUrl", instanceProperties.getMarathonUrl());
        extraProperties.put("marathonUsername", instanceProperties.getMarathonUsername());
        extraProperties.put("marathonPassword", instanceProperties.getMarathonPassword());
        extraProperties.put("marathonPrefix", instanceProperties.getMarathonPrefix());
        extraProperties.put("maxInstances", instanceProperties.getMaxInstances().toString());
        extraProperties.put("autoRegisterPeriod", instanceProperties.getAutoRegisterPeriod().toString());
        extraProperties.put("autoRegisterProperties", stringFromMap(instanceProperties.getAutoRegisterProperties()));

        Map<String, String> properties = new HashMap<>();
        properties.put("Image", instanceProperties.getImage());
        properties.put("Memory", String.valueOf(instanceProperties.getMemory()));
        properties.put("CPUs", instanceProperties.getCpus().toString());
        properties.put("Command", instanceProperties.getCommand());
        properties.put("Environment", instanceProperties.getEnvironment());

        MarathonInstance marathonInstance = new MarathonInstance(
                instanceProperties.getName(),
                instanceProperties.getCreatedAt(),
                properties,
                extraProperties,
                instanceProperties.getEnvironment()
        );
        setInstance(marathonInstance);
    }

    public static App appFromInstance(MarathonInstance instance) {

        Map<String, String> myMap = mapFromString(instance.extraProperties().get("autoRegisterProperties"));
        Period period = new Period(instance.extraProperties().get("autoRegisterPeriod"));

        InstanceProperties instanceProperties = new InstanceProperties(
                instance.name(),
                instance.extraProperties().get("goServerUrl"),
                Integer.valueOf(instance.extraProperties().get("autoRegisterTimeout")),
                instance.extraProperties().get("marathonUrl"),
                instance.extraProperties().get("marathonUsername"),
                instance.extraProperties().get("marathonPassword"),
                instance.extraProperties().get("marathonPrefix"),
                Integer.valueOf(instance.extraProperties().get("maxInstances")),
                period,
                instance.properties().get("Image"),
                Double.valueOf(instance.properties().get("Memory")),
                Double.valueOf(instance.properties().get("CPUs")),
                instance.properties().get("Command"),
                instance.environment(),
                myMap,
                instance.createdAt());

        InstanceBuilder builder = new InstanceBuilder(instanceProperties);
        return builder.getApp();
    }

    public static MarathonInstance instanceFromApp(App app) {
        Map<String, String> envVars = app.getEnv();
        Period period = new Period(envVars.remove("autoRegisterPeriod"));
        Collection<Task> tasks = app.getTasks();
        String created = "0";
        if (tasks == null) {
            LOG.warn("getTasks returned null?");
        } else {
            if (tasks.size() > 0) {
                created = Iterables.get(tasks, 0).getStagedAt();
            }
        }
        DateTime createdAt = new DateTime(created);

        InstanceProperties instanceProperties = new InstanceProperties(
                app.getId().substring(app.getEnv().get("marathonPrefix").length()),
                envVars.remove("GO_EA_SERVER_URL"),
                Integer.valueOf(envVars.remove("autoRegisterTimeout")),
                envVars.remove("marathonUrl"),
                envVars.remove("marathonUsername"),
                envVars.remove("marathonPassword"),
                envVars.remove("marathonPrefix"),
                Integer.valueOf(envVars.remove("maxInstances")),
                period,
                app.getContainer().getDocker().getImage(),
                app.getMem(),
                app.getCpus(),
                app.getCmd(),
                app.getEnv().get("GO_EA_AUTO_REGISTER_ENVIRONMENT"),
                envVars,
                createdAt
        );

        InstanceBuilder builder = new InstanceBuilder(instanceProperties);
        return builder.getInstance();
    }

    public MarathonInstance getInstance() {
        return instance;
    }

    public void setInstance(MarathonInstance instance) {
        this.instance = instance;
    }

    public App getApp() {
        return app;
    }

    public void setApp(App app) {
        this.app = app;
    }
}
