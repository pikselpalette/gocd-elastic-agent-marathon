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

import java.util.ArrayList;
import java.util.List;

public class GetProfileMetadataExecutor implements RequestExecutor {
    private static final Gson GSON = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();

    private static final Metadata IMAGE = new Metadata("Image", true, false);
    private static final Metadata MAX_MEMORY = new MemoryMetadata("Memory", true);
    private static final Metadata CPUS = new Metadata("CPUs", true, false);
    private static final Metadata COMMAND = new Metadata("Command", false, false);
    private static final Metadata ENVIRONMENT = new Metadata("Environment", false, false);
    private static final Metadata USER = new Metadata("User", false, false);
    private static final Metadata CONSTRAINTS = new Metadata("Constraints", false, false);

    static final List<Metadata> FIELDS = new ArrayList<>();

    static {
        FIELDS.add(IMAGE);
        FIELDS.add(MAX_MEMORY);
        FIELDS.add(CPUS);
        FIELDS.add(COMMAND);
        FIELDS.add(ENVIRONMENT);
        FIELDS.add(USER);
        FIELDS.add(CONSTRAINTS);
    }

    @Override

    public GoPluginApiResponse execute() throws Exception {
        return new DefaultGoPluginApiResponse(200, GSON.toJson(FIELDS));
    }
}
