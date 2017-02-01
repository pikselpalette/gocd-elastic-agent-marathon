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

import cd.go.contrib.elasticagents.marathon.*;
import cd.go.contrib.elasticagents.marathon.requests.CreateAgentRequest;
import com.thoughtworks.go.plugin.api.response.DefaultGoPluginApiResponse;
import com.thoughtworks.go.plugin.api.response.GoPluginApiResponse;
import org.joda.time.DateTime;
import org.joda.time.Period;

import static cd.go.contrib.elasticagents.marathon.utils.Util.propertiesMatch;

public class CreateAgentRequestExecutor implements RequestExecutor {
    private final AgentInstances agentInstances;
    private final PluginRequest pluginRequest;
    private final CreateAgentRequest request;

    public CreateAgentRequestExecutor(CreateAgentRequest request, AgentInstances agentInstances, PluginRequest pluginRequest) {
        this.request = request;
        this.agentInstances = agentInstances;
        this.pluginRequest = pluginRequest;
    }

    private boolean agentIdle(Agent agent) {
       return (agent.configState().equals(Agent.ConfigState.Enabled) &&
               agent.agentState().equals(Agent.AgentState.Idle) &&
               agent.buildState().equals(Agent.BuildState.Idle)
       );
    }

    @Override
    public GoPluginApiResponse execute() throws Exception {
        /*
        The logic here is that the go server has a list of agents, but
        not a definitive view of which of those agents is actually
        running.  This means that this plugin is sent a createAgentRequest
        for every matching job being scheduled.

        The housekeeping we do to prevent over-creating new instances is to
        get a list of agents from the go server, find idle ones in the list,
        and match to the corresponding instance running on marathon.  If the
        instance running on marathon would satisfy the request, we do not create
        a new instance.

        In the case where we might get multiple createAgentRequests in a short
        period of time, we mark the instance as recently matched so it is not
        eligible to match for subsequent requests.

        This isn't perfect - we might mark one and then the job actually gets
        scheduled to another agent.  In the long term, the go agents will use
        web sockets, the go server will know which ones are still running, and
        we can drop this logic here.
         */

        boolean agentMatch = false;
        for (Agent agent: pluginRequest.listAgents().agents()) {
            if (!agentIdle(agent)) {
                continue;
            }
            MarathonInstance instance = (MarathonInstance)agentInstances.find(agent.elasticAgentId());
            if (instance == null) {
                continue;
            }
            if (!propertiesMatch(instance.properties(), request.properties())) {
                continue;
            }
            // Recently matched to an outstanding task
            if (instance.getLastMatched().isAfter(new DateTime().minus(new Period("PT30S")))) {
                continue;
            }
            agentMatch = true;
            instance.setLastMatched(new DateTime());
            break;
        }
        if (!agentMatch) {
            agentInstances.create(request, pluginRequest.getPluginSettings());
        }
        return new DefaultGoPluginApiResponse(200);
    }
}
