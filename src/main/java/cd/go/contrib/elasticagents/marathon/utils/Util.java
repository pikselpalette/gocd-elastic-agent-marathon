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

package cd.go.contrib.elasticagents.marathon.utils;

import cd.go.contrib.elasticagents.marathon.executors.GetViewRequestExecutor;
import com.google.common.io.ByteStreams;
import com.google.common.io.CharStreams;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class Util {

    public static String stringFromMap(Map<String, String> input) {
        String ret = "";
        boolean first = true;
        for (Map.Entry<String, String> entry : input.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            if (first) {
                ret +=key + "=" + value;
                first = false;
            } else {
                ret += "\n" + key + "=" + value + "";
            }
        }
        return ret;
    }

    public static Map<String, String> mapFromString(String input) {
        Map<String, String> myMap = new HashMap<>();
        String[] pairs = input.split("\n");
        for (String pair: pairs) {
            String[] keyValue = pair.split("=");
            myMap.put(keyValue[0], keyValue[1]);
        }
        return myMap;
    }

    public static String readResource(String resourceFile) {
        try (InputStreamReader reader = new InputStreamReader(GetViewRequestExecutor.class.getResourceAsStream(resourceFile), StandardCharsets.UTF_8)) {
            return CharStreams.toString(reader);
        } catch (IOException e) {
            throw new RuntimeException("Could not find resource " + resourceFile, e);
        }
    }

    public static byte[] readResourceBytes(String resourceFile) {
        try (InputStream in = GetViewRequestExecutor.class.getResourceAsStream(resourceFile)) {
            return ByteStreams.toByteArray(in);
        } catch (IOException e) {
            throw new RuntimeException("Could not find resource " + resourceFile, e);
        }
    }

    public static String pluginId() {
        String s = readResource("/plugin.properties");
        try {
            Properties properties = new Properties();
            properties.load(new StringReader(s));
            return (String) properties.get("pluginId");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
