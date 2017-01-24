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

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class PluginSettingsTest {
    @Test
    public void shouldDeserializeFromJSON() throws Exception {
        PluginSettings pluginSettings = PluginSettings.fromJSON("{" +
                "\"marathon_username\": \"bob\", " +
                "\"marathon_password\": \"p@ssw0rd\", " +
                "\"marathon_url\": \"https://cloud.example.com/api/v1\" " +
                "}");

        assertThat(pluginSettings.getMarathonUsername(), is("bob"));
        assertThat(pluginSettings.getMarathonPassword(), is("p@ssw0rd"));
        assertThat(pluginSettings.getMarathonUrl(), is("https://cloud.example.com/api/v1"));
    }
}
