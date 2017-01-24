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

import cd.go.contrib.elasticagents.marathon.AgentInstances;
import cd.go.contrib.elasticagents.marathon.MarathonInstance;
import cd.go.contrib.elasticagents.marathon.PluginRequest;
import cd.go.contrib.elasticagents.marathon.RequestExecutor;
import cd.go.contrib.elasticagents.marathon.requests.ShouldAssignWorkRequest;
import cd.go.contrib.elasticagents.marathon.utils.Size;
import com.thoughtworks.go.plugin.api.response.DefaultGoPluginApiResponse;
import com.thoughtworks.go.plugin.api.response.GoPluginApiResponse;

import java.util.HashMap;
import java.util.Map;

import static org.apache.commons.lang3.StringUtils.stripToEmpty;
import static cd.go.contrib.elasticagents.marathon.MarathonPlugin.LOG;

public class ShouldAssignWorkRequestExecutor implements RequestExecutor {
    private final AgentInstances<MarathonInstance> agentInstances;
    private final PluginRequest pluginRequest;
    private final ShouldAssignWorkRequest request;

    public ShouldAssignWorkRequestExecutor(ShouldAssignWorkRequest request, AgentInstances<MarathonInstance> agentInstances, PluginRequest pluginRequest) {
        this.request = request;
        this.agentInstances = agentInstances;
        this.pluginRequest = pluginRequest;
    }

    private String castToSize(String mem) {
        String ret;
        try {
            ret = String.valueOf(Size.parse(mem).toMegabytes());
        } catch (Exception e) {
            ret = mem;
        }
        return ret;
    }

    private boolean propertiesMatch(Map<String, String> request, Map<String, String> instance) {
        return (
                (Double.valueOf(request.getOrDefault("CPUs", "0")).equals(Double.valueOf(instance.getOrDefault("CPUs", "0")))) &&
                (Double.valueOf(castToSize(request.getOrDefault("Memory", "0"))).equals(Double.valueOf(castToSize(instance.getOrDefault("Memory", "0"))))) &&
                (request.getOrDefault("Image", "").equals(instance.getOrDefault("Image", "")))
        );

    }

    @Override
    public GoPluginApiResponse execute() {
        MarathonInstance instance = agentInstances.find(request.agent().elasticAgentId());

        if (instance == null) {
            return DefaultGoPluginApiResponse.success("false");
        }

        boolean environmentMatches = stripToEmpty(request.environment()).equalsIgnoreCase(stripToEmpty(instance.environment()));

        Map<String, String> containerProperties = instance.properties() == null ? new HashMap<>() : instance.properties();
        Map<String, String> requestProperties = request.properties() == null ? new HashMap<>() : request.properties();

        boolean propertiesMatch = propertiesMatch(requestProperties, containerProperties);
        LOG.debug("ShouldAssignWorkRequestExecutor: propertiesMatch: " + String.valueOf(propertiesMatch) + ", environmentMatches: " + String.valueOf(environmentMatches));

        return DefaultGoPluginApiResponse.success(String.valueOf(environmentMatches && propertiesMatch));
    }
}
