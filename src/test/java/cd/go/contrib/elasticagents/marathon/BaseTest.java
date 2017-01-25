/*
 * Copyright 2016 ThoughtWorks, Inc.
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

import org.junit.BeforeClass;

import java.io.IOException;
import java.util.*;

import static org.mockito.Mockito.mock;

public abstract class BaseTest {

    protected static marathonClient mockMarathonClient;
    private static PluginSettings settings = new PluginSettings();

    //PluginSettings { AutoRegisterTimeout: 10, MaxInstances: 10, AutoRegisterPeriod: PT10M}
    @BeforeClass
    public static void beforeClass() throws Exception {
        settings.setMarathonPrefix("/");
        settings.setMarathonUsername("bob");
        settings.setMarathonPassword("password");
        settings.setMarathonUrl("http://localhost:8080");
        settings.setGoServerUrl("https://localhost:8153/go");
        settings.setMaxInstances(10);
        settings.setAutoRegisterTimeout("10");
        mockMarathonClient = new MockMarathonClient(settings);
    }

    protected PluginSettings createSettings() throws IOException {
        return settings;
    }

    protected marathonClient createClient() throws Exception {
        return mockMarathonClient;
    }

    public static class MockMarathonClient extends marathonClient {
        private Map<String, MarathonInstance> instances = new HashMap<>();

        public MockMarathonClient (PluginSettings settings) {}

        @Override
        public MarathonInstance requestGoAgent(MarathonInstance instance) {
            instances.put(instance.name(), instance);
            return instance;
        }

        @Override
        public List<MarathonInstance> getGoAgents(PluginSettings settings) {
            List<MarathonInstance> appList = new ArrayList<>();
            appList.addAll(instances.values());
            return appList;
        }

        @Override
        public void terminate(String name) {
            instances.remove(name);
        }
    }
}
