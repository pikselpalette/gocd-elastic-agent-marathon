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

import cd.go.contrib.elasticagents.marathon.RequestExecutor;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.thoughtworks.go.plugin.api.response.DefaultGoPluginApiResponse;
import com.thoughtworks.go.plugin.api.response.GoPluginApiResponse;

import java.util.LinkedHashMap;
import java.util.Map;

/*
 * TODO: add any additional configuration fields here.
 */
public class GetPluginConfigurationExecutor implements RequestExecutor {

    private static final Gson GSON = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();

    private static final Field GO_SERVER_URL = new NonBlankField("go_server_url", "Go Server URL", null, true, false, "0");
    private static final Field AUTOREGISTER_TIMEOUT = new PositiveNumberField("auto_register_timeout", "Agent auto-register Timeout (in minutes)", "10", true, false, "1");
    private static final Field MARATHON_URL = new NonBlankField("marathon_url", "Marathon URL", null, true, false, "2");
    private static final Field MARATHON_USERNAME = new NonBlankField("marathon_username", "Marathon Username", null, false, false, "3");
    private static final Field MARATHON_PASSWORD = new NonBlankField("marathon_password", "Marathon Password", null, false, true, "4");
    private static final Field MARATHON_PREFIX = new NonBlankField("marathon_prefix", "Marathon Prefix", "", true, false, "5");
    private static final Field MAX_INSTANCES = new PositiveNumberField("max_instances", "Maximum Number of Tasks", "10", true, false, "6");

    static final Map<String, Field> FIELDS = new LinkedHashMap<>();

    static {
        FIELDS.put(GO_SERVER_URL.key(), GO_SERVER_URL);
        FIELDS.put(AUTOREGISTER_TIMEOUT.key(), AUTOREGISTER_TIMEOUT);

        FIELDS.put(MARATHON_URL.key(), MARATHON_URL);
        FIELDS.put(MARATHON_USERNAME.key(), MARATHON_USERNAME);
        FIELDS.put(MARATHON_PASSWORD.key(), MARATHON_PASSWORD);
        FIELDS.put(MARATHON_PREFIX.key(), MARATHON_PREFIX);
        FIELDS.put(MAX_INSTANCES.key(), MAX_INSTANCES);
    }

    public GoPluginApiResponse execute() {
        return new DefaultGoPluginApiResponse(200, GSON.toJson(FIELDS));
    }

}
