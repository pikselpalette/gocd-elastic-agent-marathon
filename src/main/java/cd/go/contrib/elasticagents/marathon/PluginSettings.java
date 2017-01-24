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

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.joda.time.Period;
import static cd.go.contrib.elasticagents.marathon.MarathonPlugin.LOG;

// TODO: Implement any settings that your plugin needs
public class PluginSettings {
    public static final Gson GSON = new GsonBuilder()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .excludeFieldsWithoutExposeAnnotation()
            .create();

    @Expose
    @SerializedName("go_server_url")
    private String goServerUrl;

    @Expose
    @SerializedName("auto_register_timeout")
    private String autoRegisterTimeout;

    @Expose
    @SerializedName("marathon_url")
    private String marathonUrl;

    @Expose
    @SerializedName("marathon_username")
    private String marathonUsername;

    @Expose
    @SerializedName("marathon_password")
    private String marathonPassword;

    @Expose
    @SerializedName("marathon_prefix")
    private String marathonPrefix;

    @Expose
    @SerializedName("max_instances")
    private String maxInstances;

    private Period autoRegisterPeriod;

    @SuppressWarnings("unused")
    public String getGoServerUrl() {
        return goServerUrl;
    }

    @SuppressWarnings("unused")
    public void setGoServerUrl(String goServerUrl) {
        this.goServerUrl = goServerUrl;
    }

    @SuppressWarnings("unused")
    public void setAutoRegisterTimeout(String autoRegisterTimeout) {
        this.autoRegisterTimeout = autoRegisterTimeout;
    }

    @SuppressWarnings("unused")
    public String getMarathonUrl() {
        return marathonUrl;
    }

    @SuppressWarnings("unused")
    public void setMarathonUrl(String marathonUrl) {
        this.marathonUrl = marathonUrl;
    }

    @SuppressWarnings("unused")
    public String getMarathonUsername() {
        return marathonUsername;
    }

    @SuppressWarnings("unused")
    public void setMarathonUsername(String marathonUsername) {
        this.marathonUsername = marathonUsername;
    }

    @SuppressWarnings("unused")
    public String getMarathonPassword() {
        return marathonPassword;
    }

    @SuppressWarnings("unused")
    public void setMarathonPassword(String marathonPassword) {
        this.marathonPassword = marathonPassword;
    }

    @SuppressWarnings("unused")
    public String getMarathonPrefix() {
        if (marathonPrefix == null) {
            setMarathonPrefix("");
        }
        return marathonPrefix;
    }

    @SuppressWarnings("unused")
    public void setMarathonPrefix(String marathonPrefix) {
        this.marathonPrefix = marathonPrefix;
    }

    @SuppressWarnings("unused")
    public Integer getMaxInstances() {
        try {
            return Integer.valueOf(maxInstances);
        } catch (NumberFormatException e) {
            LOG.warn("getMaxInstances failed: " + e.getMessage(), e);
            return 10;
        }

    }

    @SuppressWarnings("unused")
    public void setMaxInstances(String maxInstances) {
        this.maxInstances = maxInstances;
    }

    @SuppressWarnings("unused")
    public void setMaxInstances(Integer maxInstances) {
        this.maxInstances = String.valueOf(maxInstances);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;

        if (!(o instanceof PluginSettings)) return false;

        PluginSettings that = (PluginSettings) o;

        return new EqualsBuilder()
                .append(getGoServerUrl(), that.getGoServerUrl())
                .append(getAutoRegisterTimeout(), that.getAutoRegisterTimeout())
                .append(getMarathonUrl(), that.getMarathonUrl())
                .append(getMarathonUsername(), that.getMarathonUsername())
                .append(getMarathonPassword(), that.getMarathonPassword())
                .append(getMarathonPrefix(), that.getMarathonPrefix())
                .append(getMaxInstances(), that.getMaxInstances())
                .append(getAutoRegisterPeriod(), that.getAutoRegisterPeriod())
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(getGoServerUrl())
                .append(getAutoRegisterTimeout())
                .append(getMarathonUrl())
                .append(getMarathonUsername())
                .append(getMarathonPassword())
                .append(getMarathonPrefix())
                .append(getMaxInstances())
                .append(getAutoRegisterPeriod())
                .toHashCode();
    }

    @Override
    public String toString() {
        return "PluginSettings { GoServerURL: " + getGoServerUrl() +
                ", AutoRegisterTimeout: " + getAutoRegisterTimeout() +
                ", MarathonURL: " + getMarathonUrl() +
                ", MarathonUsername: " + getMarathonUsername() +
                ", MarathonPassword: " + getMarathonPassword() +
                ", MarathonPrefix: " + getMarathonPrefix() +
                ", MaxInstances: " + getMaxInstances() +
                ", AutoRegisterPeriod: " + getAutoRegisterPeriod() +
                "}";
    }

    public static PluginSettings fromJSON(String json) {
        return GSON.fromJson(json, PluginSettings.class);
    }

    public Period getAutoRegisterPeriod() {
        if (this.autoRegisterPeriod == null) {
            this.autoRegisterPeriod = new Period().withMinutes(Integer.parseInt(getAutoRegisterTimeout()));
        }
        return this.autoRegisterPeriod;
    }

    public String getAutoRegisterTimeout() {
        if (autoRegisterTimeout == null) {
            autoRegisterTimeout = "10";
        }
        return autoRegisterTimeout;
    }
}
