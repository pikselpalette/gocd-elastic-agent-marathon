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
import org.joda.time.DateTime;
import org.joda.time.Period;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;

import static cd.go.contrib.elasticagents.marathon.MarathonPlugin.LOG;

public class MarathonAgentInstances implements AgentInstances<MarathonInstance> {

    private final ConcurrentHashMap<String, MarathonInstance> instances = new ConcurrentHashMap<>();
    private marathonClientFactory marathonClientFactory;
    private boolean refreshed;
    private DateTime refreshedTime;
    public Clock clock = Clock.DEFAULT;
    final Semaphore semaphore = new Semaphore(0, true);

    public MarathonAgentInstances() {
        this.marathonClientFactory = new marathonClientFactory();
    }

    public MarathonAgentInstances(marathonClientFactory marathonClientFactory) {
        this.marathonClientFactory = marathonClientFactory;
    }

    private void doWithLockOnSemaphore(Runnable runnable) {
        synchronized (semaphore) {
            runnable.run();
        }
    }

    public marathonClient marathon(PluginSettings settings) throws Exception {
        return marathonClientFactory.marathon(settings);
    }

    @Override
    public MarathonInstance create(CreateAgentRequest request, PluginSettings settings) throws Exception {
        final Integer maxInstances = settings.getMaxInstances();
        synchronized (instances) {
            doWithLockOnSemaphore(new SetupSemaphore(maxInstances, instances, semaphore));
            if (semaphore.tryAcquire()) {
                MarathonInstance instance = MarathonInstance.create(request, settings, marathon(settings));
                register(instance);
                return instance;
            } else {
                LOG.info("The number of containers currently running is currently at the maximum permissible limit (" + instances.size() + "). Not creating any more containers.");
                return null;
            }
        }
    }

    @Override
    public void terminate(String agentId, PluginSettings settings) throws Exception {
        MarathonInstance instance = instances.get(agentId);
        if (instance != null) {
            instance.terminate(marathon(settings), settings.getMarathonPrefix());
        } else {
            LOG.warn("Requested to terminate an instance that does not exist " + agentId);
            marathon(settings).terminate(agentId);
        }

        doWithLockOnSemaphore(new Runnable() {
            @Override
            public void run() {
                semaphore.release();
            }
        });

        synchronized (instances) {
            instances.remove(agentId);
        }
    }

    @Override
    public void terminateUnregisteredInstances(PluginSettings settings, Agents agents) throws Exception {

        MarathonAgentInstances toTerminate = unregisteredAfterTimeout(settings, agents);

        if (toTerminate.instances.isEmpty()) {
            return;
        }
        for (MarathonInstance instance: toTerminate.instances.values()) {
            terminate(instance.name(), settings);
        }
    }

    @Override
    // TODO: Implement me!
    public Agents instancesCreatedAfterTimeout(PluginSettings settings, Agents agents) {
        ArrayList<Agent> oldAgents = new ArrayList<>();
        for (Agent agent : agents.agents()) {
            MarathonInstance instance = instances.get(agent.elasticAgentId());
            if (instance == null) {
                continue;
            }

            if (clock.now().isAfter(instance.createdAt().plus(settings.getAutoRegisterPeriod()))) {
                oldAgents.add(agent);
            }
        }
        return new Agents(oldAgents);
    }

    @Override
    public void refreshAll(PluginRequest pluginRequest) throws Exception {
        if (refreshed) {
            if (refreshedTime == null) {
                refreshed = false;
            } else {
                if (refreshedTime.isBefore(new DateTime().minus(new Period("PT10M")))) {
                    refreshedTime = new DateTime();
                    refreshed = false;
                }
            }
        }
        if (!refreshed) {
            List<MarathonInstance> marathonInstanceList = marathon(pluginRequest.getPluginSettings()).getGoAgents();
            for (MarathonInstance instance: marathonInstanceList) {
                 register(instance);
            }
            refreshed = true;
        }
    }

    @Override
    public MarathonInstance find(String agentId) {
        return instances.get(agentId);
    }

    // used by tests
    public boolean hasInstance(String agentId) {
        return instances.containsKey(agentId);
    }

    private void register(MarathonInstance instance) {
        instances.put(instance.name(), instance);
    }

    private MarathonAgentInstances unregisteredAfterTimeout(PluginSettings settings, Agents knownAgents) throws Exception {
        MarathonAgentInstances unregisteredContainers = new MarathonAgentInstances();
        if (settings == null) {
            return unregisteredContainers;
        }
        Period period = settings.getAutoRegisterPeriod();

        for (String instanceName : instances.keySet()) {
            if (knownAgents.containsAgentWithId(instanceName)) {
                continue;
            }

            MarathonInstance instance = marathon(settings).findGoAgent(instanceName);
            if (instance == null) {
                continue;
            }

            DateTime dateTimeCreated = new DateTime(instance.createdAt());

            if (clock.now().isAfter(dateTimeCreated.plus(period))) {
                unregisteredContainers.register(instance);
            }
        }
        return unregisteredContainers;
    }
}
