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

import cd.go.contrib.elasticagents.marathon.marathon.MarathonApp;
import cd.go.contrib.elasticagents.marathon.requests.CreateAgentRequest;
import cd.go.contrib.elasticagents.marathon.utils.Size;
import com.google.common.collect.Iterables;
import mesosphere.marathon.client.model.v2.Container;
import mesosphere.marathon.client.model.v2.Docker;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.joda.time.DateTime;

import java.util.*;

import static cd.go.contrib.elasticagents.marathon.MarathonPlugin.LOG;

public class MarathonInstance {
    private String name;
    private final DateTime createdAt;
    private final String environment;

    private final String goServerUrl;
    private final String marathonPrefix;
    private final String image;
    private final Double memory;
    private final Double cpus;
    private final String command;
    private final String user;
    private final Map<String,String> autoRegisterProperties;
    private final MarathonApp app;

    MarathonInstance(String name, DateTime createdAt, String environment, String goServerUrl, String marathonPrefix, String image, Double memory, Double cpus, String command, String user, Map<String, String> autoRegisterProperties) {
        this.name = name;
        this.createdAt = createdAt;
        this.environment = environment;
        this.goServerUrl = goServerUrl;
        this.marathonPrefix = marathonPrefix;
        this.image = image;
        this.memory = memory;
        this.cpus = cpus;
        this.command = command;
        this.user = user;
        this.autoRegisterProperties = autoRegisterProperties;
        this.app = buildApp();
    }

    MarathonInstance(String name, DateTime createdAt, String environment, String goServerUrl, String marathonPrefix, String image, Double memory, Double cpus, String command, String user, Map<String, String> autoRegisterProperties, MarathonApp app) {
        this.name = name;
        this.createdAt = createdAt;
        this.environment = environment;
        this.goServerUrl = goServerUrl;
        this.marathonPrefix = marathonPrefix;
        this.image = image;
        this.memory = memory;
        this.cpus = cpus;
        this.command = command;
        this.user = user;
        this.autoRegisterProperties = autoRegisterProperties;
        this.app = app;
    }

    private MarathonApp buildApp() {
        Docker docker = new Docker();
        docker.setImage(getImage());
        docker.setNetwork("HOST");

        Container container = new Container();
        container.setType("DOCKER");
        container.setDocker(docker);

        MarathonApp app = new MarathonApp();
        app.setMem(getMemory());
        app.setCpus(getCpus());
        app.setId(getMarathonPrefix() + getName());
        app.setInstances(1);
        app.setContainer(container);
        app.setCmd(getCommand());

        Map<String, String> envVars = new HashMap<>();

        envVars.put("GO_EA_SERVER_URL", getGoServerUrl());
        envVars.put("GO_EA_AUTO_REGISTER_ENVIRONMENT", getEnvironment());
        envVars.put("GO_EA_GUID", "marathon." + getName());
        envVars.putAll(getAutoRegisterProperties());

        app.setEnv(envVars);

        if (getUser() != null) {
            app.setUser(getUser());
        }

        return app;
    }

    public static MarathonInstance instanceFromApp(MarathonApp app, PluginSettings settings) {
        Map<String, String> autoRegisterProperties = new HashMap<>();
        autoRegisterProperties.put("GO_EA_AUTO_REGISTER_KEY", app.getEnv().get("GO_EA_AUTO_REGISTER_KEY"));
        autoRegisterProperties.put("GO_EA_AUTO_REGISTER_ENVIRONMENT", app.getEnv().get("GO_EA_AUTO_REGISTER_ENVIRONMENT"));
        autoRegisterProperties.put("GO_EA_AUTO_REGISTER_ELASTIC_AGENT_ID", app.getEnv().get("GO_EA_AUTO_REGISTER_ELASTIC_AGENT_ID"));
        autoRegisterProperties.put("GO_EA_AUTO_REGISTER_ELASTIC_PLUGIN_ID", app.getEnv().get("GO_EA_AUTO_REGISTER_ELASTIC_PLUGIN_ID"));

        DateTime createdTime = new DateTime();

        if (app.getTasks() != null) {
            if (app.getTasks().size() > 0) {
                createdTime = new DateTime(Iterables.get(app.getTasks(), 0).getStagedAt());
            }
        }

        return new MarathonInstance(
                app.getId().substring(settings.getMarathonPrefix().length()),
                createdTime,
                app.getEnv().get("GO_EA_AUTO_REGISTER_ENVIRONMENT"),
                settings.getGoServerUrl(),
                settings.getMarathonPrefix(),
                app.getContainer().getDocker().getImage(),
                app.getMem(),
                app.getCpus(),
                app.getCmd(),
                app.getUser(),
                autoRegisterProperties,
                app
        );
    }

    void terminate(marathonClient marathon) {
        LOG.info("Terminating instance " + this.name());
        marathon.terminate(app.getId());
    }

    public static MarathonInstance create(CreateAgentRequest request, PluginSettings settings, marathonClient marathon) {
        String name = UUID.randomUUID().toString();
        MarathonInstance marathonInstance = new MarathonInstance(
                name,
                new DateTime(),
                request.environment(),
                settings.getGoServerUrl(),
                settings.getMarathonPrefix(),
                request.properties().get("Image"),
                Double.valueOf(Size.parse(request.properties().get("Memory")).toMegabytes()),
                Double.valueOf(request.properties().get("CPUs")),
                request.properties().get("Command"),
                request.properties().get("User"),
                request.autoregisterPropertiesAsEnvironmentVars(name)
        );

        LOG.info("Creating instance " + marathonInstance.app.getId());
        marathon.requestGoAgent(marathonInstance);
        return marathonInstance;
    }

    @Override
    public String toString() {
        return "MarathonInstance{" +
                "name='" + name + '\'' +
                ", createdAt=" + createdAt +
                ", environment='" + environment + '\'' +
                ", goServerUrl='" + goServerUrl + '\'' +
                ", marathonPrefix='" + marathonPrefix + '\'' +
                ", image='" + image + '\'' +
                ", memory=" + memory +
                ", cpus=" + cpus +
                ", command='" + command + '\'' +
                ", user='" + user + '\'' +
                ", autoRegisterProperties=" + autoRegisterProperties +
                ", app=" + app +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof MarathonInstance)) return false;

        MarathonInstance that = (MarathonInstance) o;

        return new EqualsBuilder()
                .append(getName(), that.getName())
                .append(getCreatedAt(), that.getCreatedAt())
                .append(getEnvironment(), that.getEnvironment())
                .append(getGoServerUrl(), that.getGoServerUrl())
                .append(getMarathonPrefix(), that.getMarathonPrefix())
                .append(getImage(), that.getImage())
                .append(getMemory(), that.getMemory())
                .append(getCpus(), that.getCpus())
                .append(getCommand(), that.getCommand())
                .append(getUser(), that.getUser())
                .append(getAutoRegisterProperties(), that.getAutoRegisterProperties())
                .append(getApp(), that.getApp())
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(getName())
                .append(getCreatedAt())
                .append(getEnvironment())
                .append(getGoServerUrl())
                .append(getMarathonPrefix())
                .append(getImage())
                .append(getMemory())
                .append(getCpus())
                .append(getCommand())
                .append(getUser())
                .append(getAutoRegisterProperties())
                .append(getApp())
                .toHashCode();
    }

    public Map<String, String> properties() {
        Map<String, String> props = new HashMap<>();

        props.put("CPUs", String.valueOf(getCpus()));
        props.put("Memory", String.valueOf(getMemory()));
        props.put("Image", getImage());

        return props;
    }

    public String name() {
        return name;
    }

    public DateTime createdAt() {
        return createdAt;
    }

    public String environment() {
        return environment;
    }

    public String getName() {
        return name;
    }

    public DateTime getCreatedAt() {
        return createdAt;
    }

    public String getEnvironment() {
        return environment;
    }

    public String getGoServerUrl() {
        return goServerUrl;
    }

    public String getMarathonPrefix() {
        return marathonPrefix;
    }

    public String getImage() {
        return image;
    }

    public Double getMemory() {
        return memory;
    }

    public Double getCpus() {
        return cpus;
    }

    public String getCommand() {
        return command;
    }

    public String getUser() {
        return user;
    }

    public Map<String, String> getAutoRegisterProperties() {
        return autoRegisterProperties;
    }

    public MarathonApp getApp() {
        return app;
    }
}
