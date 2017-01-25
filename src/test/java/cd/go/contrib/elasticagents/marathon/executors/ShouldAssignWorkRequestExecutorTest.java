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

package cd.go.contrib.elasticagents.marathon.executors;

import cd.go.contrib.elasticagents.marathon.*;
import cd.go.contrib.elasticagents.marathon.requests.CreateAgentRequest;
import cd.go.contrib.elasticagents.marathon.requests.ShouldAssignWorkRequest;
import com.thoughtworks.go.plugin.api.response.GoPluginApiResponse;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ShouldAssignWorkRequestExecutorTest extends BaseTest {

    private AgentInstances<MarathonInstance> agentInstances;
    private MarathonInstance instance;
    private final String environment = "production";
    private Map<String, String> properties = new HashMap<>();

    @Before
    public void setUp() throws Exception {
        marathonClientFactory mf = mock(marathonClientFactory.class);
        when(mf.marathon(any(PluginSettings.class))).thenReturn(createClient());
        agentInstances = new MarathonAgentInstances(mf);
        properties.put("Memory", "2048MB");
        properties.put("CPUs", "2");
        properties.put("Command", "");
        properties.put("Environment", "production");
        properties.put("Image", "gocdcontrib/ubuntu-docker-elastic-agent");
        instance = agentInstances.create(new CreateAgentRequest(UUID.randomUUID().toString(), properties, environment), createSettings());
    }

    @Test
    public void shouldAssignWorkToContainerWithMatchingEnvironmentNameAndProperties() throws Exception {
        ShouldAssignWorkRequest request = new ShouldAssignWorkRequest(new Agent(instance.name(), null, null, null), environment, properties);
        GoPluginApiResponse response = new ShouldAssignWorkRequestExecutor(request, agentInstances, null).execute();
        assertThat(response.responseCode(), is(200));
        assertThat(response.responseBody(), is("true"));
    }

    @Test
    public void shouldNotAssignWorkToContainerWithDifferentEnvironmentName() throws Exception {
        ShouldAssignWorkRequest request = new ShouldAssignWorkRequest(new Agent(instance.name(), null, null, null), "FooEnv", properties);
        GoPluginApiResponse response = new ShouldAssignWorkRequestExecutor(request, agentInstances, null).execute();
        assertThat(response.responseCode(), is(200));
        assertThat(response.responseBody(), is("false"));
    }

    @Test
    public void shouldNotAssignWorkToContainerWithDifferentProperties() throws Exception {
        ShouldAssignWorkRequest request = new ShouldAssignWorkRequest(new Agent(instance.name(), null, null, null), environment, null);
        GoPluginApiResponse response = new ShouldAssignWorkRequestExecutor(request, agentInstances, null).execute();
        assertThat(response.responseCode(), is(200));
        assertThat(response.responseBody(), is("false"));
    }
}
