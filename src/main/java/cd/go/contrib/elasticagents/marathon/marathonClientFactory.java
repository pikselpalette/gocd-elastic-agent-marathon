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

public class marathonClientFactory {
    private static marathonClient client = null;
    private static PluginSettings settings = null;

    private synchronized static marathonClient getMarathon(PluginSettings settings) throws Exception {
        if (settings.equals(marathonClientFactory.settings) && marathonClientFactory.client != null) {
            return marathonClientFactory.client;
        }

        marathonClientFactory.settings = settings;
        marathonClientFactory.client = createClient(settings);
        return marathonClientFactory.client;
    }

    public synchronized marathonClient marathon(PluginSettings settings) throws Exception {
        return marathonClientFactory.getMarathon(settings);
    }

    private static marathonClient createClient(PluginSettings settings) {
        return new marathonClient(settings);
    }

}
