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

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

public class Field {
    protected final String key;

    @Expose
    @SerializedName("display-name")
    String displayName;

    @Expose
    @SerializedName("default-value")
    private String defaultValue;

    @Expose
    @SerializedName("required")
    Boolean required;

    @Expose
    @SerializedName("secure")
    private Boolean secure;

    @Expose
    @SerializedName("display-order")
    private String displayOrder;

    Field(String key, String displayName, String defaultValue, Boolean required, Boolean secure, String displayOrder) {
        this.key = key;
        this.displayName = displayName;
        this.defaultValue = defaultValue;
        this.required = required;
        this.secure = secure;
        this.displayOrder = displayOrder;
    }

    Map<String, String> validate(String input) {
        HashMap<String, String> result = new HashMap<>();
        String validationError = doValidate(input);
        if (StringUtils.isNotBlank(validationError)) {
            result.put("key", key);
            result.put("message", validationError);
        }
        return result;
    }

    protected String doValidate(String input) {
        return null;
    }

    public String key() {
        return key;
    }
}
