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

import cd.go.contrib.elasticagents.marathon.requests.ValidatePluginSettings;
import com.thoughtworks.go.plugin.api.response.GoPluginApiResponse;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class ValidateConfigurationExecutorTest {
    @Test
    public void shouldValidateABadConfiguration() throws Exception {
        ValidatePluginSettings settings = new ValidatePluginSettings();
        GoPluginApiResponse response = new ValidateConfigurationExecutor(settings).execute();

        assertThat(response.responseCode(), is(200));
        JSONAssert.assertEquals("[\n" +
                "  {\n" +
                "    \"message\": \"Go Server URL must not be blank.\",\n" +
                "    \"key\": \"go_server_url\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"message\": \"Agent auto-register Timeout (in minutes) must be a positive integer.\",\n" +
                "    \"key\": \"auto_register_timeout\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"message\": \"Marathon URL must not be blank.\",\n" +
                "    \"key\": \"marathon_url\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"message\": \"Marathon Username must not be blank.\",\n" +
                "    \"key\": \"marathon_username\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"message\": \"Marathon Password must not be blank.\",\n" +
                "    \"key\": \"marathon_password\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"message\": \"Marathon Prefix must not be blank.\",\n" +
                "    \"key\": \"marathon_prefix\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"message\": \"Maximum Number of Tasks must be a positive integer.\",\n" +
                "    \"key\": \"max_instances\"\n" +
                "  },\n" +
                "]", response.responseBody(), true);
    }

    @Test
    public void shouldValidateAGoodConfiguration() throws Exception {
        ValidatePluginSettings settings = new ValidatePluginSettings();
        settings.put("marathon_url", "https://api.example.com");
        settings.put("marathon_username", "bob");
        settings.put("marathon_password", "p@ssw0rd");
        settings.put("go_server_url", "https://ci.example.com");
        settings.put("auto_register_timeout", "10");
        settings.put("marathon_prefix", "/");
        settings.put("max_instances", "5");
        GoPluginApiResponse response = new ValidateConfigurationExecutor(settings).execute();

        assertThat(response.responseCode(), is(200));
        JSONAssert.assertEquals("[]", response.responseBody(), true);
    }
}
