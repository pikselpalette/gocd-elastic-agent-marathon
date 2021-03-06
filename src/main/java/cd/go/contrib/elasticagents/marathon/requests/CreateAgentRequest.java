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

package cd.go.contrib.elasticagents.marathon.requests;

import cd.go.contrib.elasticagents.marathon.PluginRequest;
import cd.go.contrib.elasticagents.marathon.executors.CreateAgentRequestExecutor;
import cd.go.contrib.elasticagents.marathon.AgentInstances;
import cd.go.contrib.elasticagents.marathon.Constants;
import cd.go.contrib.elasticagents.marathon.RequestExecutor;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.*;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

public class CreateAgentRequest {
    private static final Gson GSON = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();
    private String autoRegisterKey;
    private Map<String, String> properties;
    private String environment;


    public CreateAgentRequest() {
    }

    public CreateAgentRequest(String autoRegisterKey, Map<String, String> properties, String environment) {
        this.autoRegisterKey = autoRegisterKey;
        this.properties = properties;
        this.environment = environment;
    }

    String autoRegisterKey() {
        return autoRegisterKey;
    }

    public Map<String, String> properties() {
        return properties;
    }

    public String environment() {
        return environment;
    }

    public static CreateAgentRequest fromJSON(String json) {
        return GSON.fromJson(json, CreateAgentRequest.class);
    }

    public RequestExecutor executor(AgentInstances agentInstances, PluginRequest pluginRequest) {
        return new CreateAgentRequestExecutor(this, agentInstances, pluginRequest);
    }

    public Map<String, String> autoregisterPropertiesAsEnvironmentVars(String elasticAgentId) {
        Map<String, String> vars = new HashMap<>();
        if (isNotBlank(autoRegisterKey)) {
            vars.put("GO_EA_AUTO_REGISTER_KEY", autoRegisterKey);
        }
        if (isNotBlank(environment)) {
            vars.put("GO_EA_AUTO_REGISTER_ENVIRONMENT", environment);
        }
        vars.put("GO_EA_AUTO_REGISTER_ELASTIC_AGENT_ID", elasticAgentId);
        vars.put("GO_EA_AUTO_REGISTER_ELASTIC_PLUGIN_ID", Constants.PLUGIN_ID);
        return vars;
    }

    @Override
    public String toString() {
        return "CreateAgentRequest {" +
                "autoRegisterKey: " + autoRegisterKey +
                ", properties: " + properties.toString() +
                ", environment: " + environment +
                "}";
    }
}
