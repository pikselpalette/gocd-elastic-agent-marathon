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

package cd.go.contrib.elasticagents.marathon.utils;

import cd.go.contrib.elasticagents.marathon.PluginSettings;
import cd.go.contrib.elasticagents.marathon.requests.CreateAgentRequest;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.joda.time.DateTime;
import org.joda.time.Period;

import java.util.Map;

/*
Data storage for translation between Marathon App class and the internal MarathonInstance
class.
 */

public class InstanceProperties {
    private String name;
    private String goServerUrl;
    private Integer autoRegisterTimeout;
    private String marathonUrl;
    private String marathonUsername;
    private String marathonPassword;
    private String marathonPrefix;
    private Integer maxInstances;
    private Period autoRegisterPeriod;
    private String image;
    private Double memory;
    private Double cpus;
    private String command;
    private String environment;
    private Map<String,String> autoRegisterProperties;
    private DateTime createdAt;

    public InstanceProperties(
            String name,
            String goServerUrl,
            Integer autoRegisterTimeout,
            String marathonUrl,
            String marathonUsername,
            String marathonPassword,
            String marathonPrefix,
            Integer maxInstances,
            Period autoRegisterPeriod,
            String image,
            Double memory,
            Double cpus,
            String command,
            String environment,
            Map<String, String> autoRegisterProperties,
            DateTime createdAt) {
        this.name = name;
        this.goServerUrl = goServerUrl;
        this.autoRegisterTimeout = autoRegisterTimeout;
        this.marathonUrl = marathonUrl;
        this.marathonUsername = marathonUsername;
        this.marathonPassword = marathonPassword;
        this.marathonPrefix = marathonPrefix;
        this.maxInstances = maxInstances;
        this.autoRegisterPeriod = autoRegisterPeriod;
        this.image = image;
        this.memory = memory;
        this.cpus = cpus;
        this.command = command;
        this.environment = environment;
        this.autoRegisterProperties = autoRegisterProperties;
        this.createdAt = createdAt;
    }

    public InstanceProperties (
            String name,
            PluginSettings settings,
            CreateAgentRequest request,
            Map<String, String> autoRegisterProperties,
            DateTime createdAt) {
        setName(name);
        setGoServerUrl(settings.getGoServerUrl());
        setAutoRegisterTimeout(Integer.valueOf(settings.getAutoRegisterTimeout()));
        setMarathonUrl(settings.getMarathonUrl());
        setMarathonUsername(settings.getMarathonUsername());
        setMarathonPassword(settings.getMarathonPassword());
        setMarathonPrefix(settings.getMarathonPrefix());
        setMaxInstances(settings.getMaxInstances());
        setAutoRegisterPeriod(settings.getAutoRegisterPeriod());
        setImage(request.properties().get("Image"));

        String memory;
        try {
            memory = String.valueOf(Size.parse(request.properties().get("Memory")).toMegabytes());
        } catch (Exception e) {
            memory = request.properties().get("Memory");
        }

        setMemory(Double.valueOf(memory));
        setCpus(Double.valueOf(request.properties().get("CPUs")));
        setCommand(request.properties().get("Command"));
        setEnvironment(request.environment());
        setAutoRegisterProperties(autoRegisterProperties);
        setCreatedAt(createdAt);
    }

    @Override
    public String toString() {
        return "InstanceProperties{" +
                "name='" + name + '\'' +
                ", goServerUrl='" + goServerUrl + '\'' +
                ", autoRegisterTimeout=" + autoRegisterTimeout +
                ", marathonUrl='" + marathonUrl + '\'' +
                ", marathonUsername='" + marathonUsername + '\'' +
                ", marathonPassword='" + marathonPassword + '\'' +
                ", marathonPrefix='" + marathonPrefix + '\'' +
                ", maxInstances=" + maxInstances +
                ", autoRegisterPeriod=" + autoRegisterPeriod +
                ", image='" + image + '\'' +
                ", memory=" + memory +
                ", cpus=" + cpus +
                ", command='" + command + '\'' +
                ", environment='" + environment + '\'' +
                ", autoRegisterProperties=" + autoRegisterProperties +
                ", createdAt=" + createdAt +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof InstanceProperties)) return false;

        InstanceProperties that = (InstanceProperties) o;

        return new EqualsBuilder()
                .append(getName(), that.getName())
                .append(getGoServerUrl(), that.getGoServerUrl())
                .append(getAutoRegisterTimeout(), that.getAutoRegisterTimeout())
                .append(getMarathonUrl(), that.getMarathonUrl())
                .append(getMarathonUsername(), that.getMarathonUsername())
                .append(getMarathonPassword(), that.getMarathonPassword())
                .append(getMarathonPrefix(), that.getMarathonPrefix())
                .append(getMaxInstances(), that.getMaxInstances())
                .append(getAutoRegisterPeriod(), that.getAutoRegisterPeriod())
                .append(getImage(), that.getImage())
                .append(getMemory(), that.getMemory())
                .append(getCpus(), that.getCpus())
                .append(getCommand(), that.getCommand())
                .append(getEnvironment(), that.getEnvironment())
                .append(getAutoRegisterProperties(), that.getAutoRegisterProperties())
                .append(getCreatedAt(), that.getCreatedAt())
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(getName())
                .append(getGoServerUrl())
                .append(getAutoRegisterTimeout())
                .append(getMarathonUrl())
                .append(getMarathonUsername())
                .append(getMarathonPassword())
                .append(getMarathonPrefix())
                .append(getMaxInstances())
                .append(getAutoRegisterPeriod())
                .append(getImage())
                .append(getMemory())
                .append(getCpus())
                .append(getCommand())
                .append(getEnvironment())
                .append(getAutoRegisterProperties())
                .append(getCreatedAt())
                .toHashCode();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGoServerUrl() {
        return goServerUrl;
    }

    private void setGoServerUrl(String goServerUrl) {
        this.goServerUrl = goServerUrl;
    }

    public Integer getAutoRegisterTimeout() {
        return autoRegisterTimeout;
    }

    private void setAutoRegisterTimeout(Integer autoRegisterTimeout) {
        this.autoRegisterTimeout = autoRegisterTimeout;
    }

    public String getMarathonUrl() {
        return marathonUrl;
    }

    private void setMarathonUrl(String marathonUrl) {
        this.marathonUrl = marathonUrl;
    }

    public String getMarathonUsername() {
        return marathonUsername;
    }

    private void setMarathonUsername(String marathonUsername) {
        this.marathonUsername = marathonUsername;
    }

    public String getMarathonPassword() {
        return marathonPassword;
    }

    private void setMarathonPassword(String marathonPassword) {
        this.marathonPassword = marathonPassword;
    }

    public String getMarathonPrefix() {
        return marathonPrefix;
    }

    private void setMarathonPrefix(String marathonPrefix) {
        this.marathonPrefix = marathonPrefix;
    }

    public Integer getMaxInstances() {
        return maxInstances;
    }

    private void setMaxInstances(Integer maxInstances) {
        this.maxInstances = maxInstances;
    }

    public Period getAutoRegisterPeriod() {
        return autoRegisterPeriod;
    }

    private void setAutoRegisterPeriod(Period autoRegisterPeriod) {
        this.autoRegisterPeriod = autoRegisterPeriod;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Double getMemory() {
        return memory;
    }

    private void setMemory(Double memory) {
        this.memory = memory;
    }

    public Double getCpus() {
        return cpus;
    }

    private void setCpus(Double cpus) {
        this.cpus = cpus;
    }

    public String getCommand() {
        return command;
    }

    private void setCommand(String command) {
        this.command = command;
    }

    public String getEnvironment() {
        return environment;
    }

    public void setEnvironment(String environment) {
        this.environment = environment;
    }

    public Map<String, String> getAutoRegisterProperties() {
        return autoRegisterProperties;
    }

    private void setAutoRegisterProperties(Map<String, String> autoRegisterProperties) {
        this.autoRegisterProperties = autoRegisterProperties;
    }

    public DateTime getCreatedAt() {
        return createdAt;
    }

    private void setCreatedAt(DateTime createdAt) {
        this.createdAt = createdAt;
    }
}
