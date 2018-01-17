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
package org.eclipse.che.wsagent.server;

import org.eclipse.che.api.core.ApiException;
import org.eclipse.che.api.core.rest.HttpJsonRequestFactory;
import org.eclipse.che.api.machine.shared.dto.ServerDto;
import org.eclipse.che.api.workspace.shared.dto.WorkspaceDto;
import org.eclipse.che.api.machine.shared.dto.MachineDto;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import java.io.IOException;
import java.util.Collection;

import static com.google.common.base.Strings.isNullOrEmpty;
import static org.eclipse.che.api.machine.shared.Constants.WSAGENT_REFERENCE;

/**
 * Provides URL to workspace agent inside container.
 *
 * @author Anton Korneta
 */
public class WsAgentURLProvider implements Provider<String> {
    private static final Logger LOG = LoggerFactory.getLogger(WsAgentURLProvider.class);

    private final String                 wsId;
    private final String                 workspaceApiEndpoint;
    private final HttpJsonRequestFactory requestFactory;

    private String cachedAgentUrl;

    @Inject
    public WsAgentURLProvider(@Named("che.api") String apiEndpoint,
                              @Named("env.CHE_WORKSPACE_ID") String wsId,
                              HttpJsonRequestFactory requestFactory) {
        this.wsId = wsId;
        this.workspaceApiEndpoint = apiEndpoint + "/workspace/";
        this.requestFactory = requestFactory;
    }

    @Override
    public String get() {
	LOG.info("inject in WsAgentURLProvider");
        if (isNullOrEmpty(cachedAgentUrl)) {
            try {
                final WorkspaceDto workspace = requestFactory.fromUrl(workspaceApiEndpoint + wsId)
                                                             .useGetMethod()
                                                             .request()
                                                             .asDto(WorkspaceDto.class);
                if (workspace.getRuntime() != null) {
                   /* final Collection<ServerDto> servers = workspace.getRuntime()
                                                                   .getDevMachine()
                                                                   .getRuntime()
                                                                   .getServers()
                                                                   .values();*/
		    final Collection<ServerDto> servers = findServers(workspace);
                    for (ServerDto server : servers) {
                        if (WSAGENT_REFERENCE.equals(server.getRef())) {
                            cachedAgentUrl = server.getUrl();
                            return cachedAgentUrl;
                        }
                    }
                }
            } catch (ApiException | IOException ex) {
                LOG.warn(ex.getLocalizedMessage());
                throw new RuntimeException("Failed to configure wsagent endpoint");
            }
        }
        return cachedAgentUrl;
    }
    private Collection<ServerDto> findServers(final WorkspaceDto workspace){
	MachineDto machine = workspace.getRuntime().getDevMachine();
	LOG.info("DevMachine: "+machine);
	if(machine != null){
		return machine.getRuntime().getServers().values();
	}else{
         System.out.println("There is no devMachine");
         final Collection<MachineDto> machines=workspace.getRuntime().getMachines();//.values();
         for(MachineDto m : machines){
        	 LOG.info(m.getEnvName() + "////" +m.getId());
			 Collection<ServerDto> servers = m.getRuntime().getServers().values();
			 for (ServerDto server : servers) {
                    if (WSAGENT_REFERENCE.equals(server.getRef())) {
                       // cachedAgentUrl = server.getUrl();
                        LOG.info("Find a machine but not dev machine with wsagent");
                        return servers;
                    }
                }
	     }
	     LOG.info("cannot find machine with wsagent ");

	}
	return null;

    }
}
