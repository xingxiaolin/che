/*******************************************************************************
 * Copyright (c) 2012-2017 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package org.eclipse.che.api.agent.server.impl;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import org.eclipse.che.api.agent.server.AgentRegistry;
import org.eclipse.che.api.agent.server.exception.AgentException;
import org.eclipse.che.api.agent.server.exception.AgentNotFoundException;
import org.eclipse.che.api.agent.shared.model.Agent;
import org.eclipse.che.api.agent.shared.model.AgentKey;
import org.eclipse.che.api.agent.shared.model.impl.AgentKeyImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static java.lang.String.format;
import static java.util.Collections.unmodifiableCollection;

/**
 * Local implementation of the {@link AgentRegistry}.
 * The name of the agent might represent a url.
 *
 * @author Anatoliy Bazko
 */
@Singleton
public class AgentRegistryImpl implements AgentRegistry {

    protected static final Logger LOG = LoggerFactory.getLogger(AgentRegistryImpl.class);

    private final Map<AgentKey, Agent> agents;

    /**
     * AgentRegistryImpl constructor.
     *
     * @param agents
     *      list of agents to register
     * @throws IllegalArgumentException
     *      if there are several agents with same id and version
     */
    @Inject
    public AgentRegistryImpl(Set<Agent> agents) throws IllegalArgumentException {
        this.agents = new HashMap<>(agents.size());
        for (Agent agent : agents) {
        	LOG.info("********************"+agent.getId() +"//"+agent.getName()+"//"+agent.getVersion());
            AgentKeyImpl key = new AgentKeyImpl(agent);
            Agent registeredAgent = this.agents.put(key, agent);
            LOG.info("111111111111111");
            if (registeredAgent != null) {
            	LOG.info("2222222222222");
                throw new IllegalArgumentException(format("Agent with key %s has been registered already.", key));
            }
        }
    }

    @Override
    public Agent getAgent(AgentKey agentKey) throws AgentException {
    	LOG.info("55555555555555555");
        return doGetAgent(agentKey);
    }

    @Override
    public List<String> getVersions(String id) throws AgentException {
        return agents.entrySet().stream()
                     .filter(e -> e.getKey().getId().equals(id))
                     .map(e -> e.getKey().getVersion())
                     .collect(Collectors.toList());
    }

    @Override
    public Collection<Agent> getAgents() throws AgentException {
        return unmodifiableCollection(agents.values());
    }

    private Agent doGetAgent(AgentKey key) throws AgentException {
    	LOG.info("666666666666");
        Optional<Agent> agent = Optional.ofNullable(agents.get(key));
        LOG.info("7777777777777777");
        return agent.orElseThrow(() -> new AgentNotFoundException(format("Agent %s not found", key.getId())));
    }
}
