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

import cd.go.contrib.elasticagents.marathon.requests.CreateAgentRequest;
import cd.go.contrib.elasticagents.marathon.utils.InstanceProperties;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.joda.time.DateTime;

import java.util.*;

import static cd.go.contrib.elasticagents.marathon.MarathonPlugin.LOG;

public class MarathonInstance {
    private final DateTime createdAt;
    private final Map<String, String> properties;
    private final Map<String, String> extraProperties;
    private final String environment;
    private String name;

    public MarathonInstance(String name, DateTime createdAt, Map<String, String> properties, Map<String, String> extraProperties, String environment) {
        this.name = name;
        this.createdAt = new DateTime(createdAt);
        this.properties = properties;
        this.extraProperties = extraProperties;
        this.environment = environment;
    }

    public String name() {
        return name;
    }

    public DateTime createdAt() {
        return createdAt;
    }

    public Map<String, String> properties() {
        return properties;
    }
    public Map<String, String> extraProperties() {
        return extraProperties;
    }

    public String environment() {
        return environment;
    }

    public void terminate(marathonClient marathon, String prefix) {
        LOG.debug("Terminating instance " + this.name());
        marathon.terminate(prefix + name);
    }

    public static MarathonInstance create(CreateAgentRequest request, PluginSettings settings, marathonClient marathon) {
        String name = UUID.randomUUID().toString();
        MarathonInstance marathonInstance = new InstanceBuilder(
            new InstanceProperties(
                name,
                settings,
                request,
                request.autoregisterPropertiesAsEnvironmentVars(name),
                new DateTime()
        )).getInstance();

        LOG.debug("Creating instance " + settings.getMarathonPrefix() + name);
        marathon.requestGoAgent(marathonInstance);
        return marathonInstance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof MarathonInstance)) return false;

        MarathonInstance that = (MarathonInstance) o;

        return new EqualsBuilder()
                .append(createdAt, that.createdAt)
                .append(properties, that.properties)
                .append(extraProperties, that.extraProperties)
                .append(environment, that.environment)
                .append(name, that.name)
                .isEquals();
    }

    @Override
    public String toString() {
        return "MarathonInstance{" +
                "createdAt=" + createdAt +
                ", properties=" + properties +
                ", extraProperties=" + extraProperties +
                ", environment='" + environment + '\'' +
                ", name='" + name + '\'' +
                '}';
    }

    @Override
    public int hashCode() {
        return name != null ? name.hashCode() : 0;
    }
}
