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
package org.eclipse.che.api.project.server;

import org.eclipse.che.api.core.ServerException;
import org.eclipse.che.api.core.model.project.ProjectConfig;
import org.eclipse.che.api.core.model.project.GZProjectConfig;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**存储在工作区配置中的项目配置与代理状态同步器
 * Synchronizer for Project Configurations stored in Workspace Configuration with Agent's state
 *
 * @author gazarenkov
 */
public abstract class WorkspaceProjectsSyncer {
	private static final Logger LOG = LoggerFactory.getLogger(WorkspaceProjectsSyncer.class);
	
	public final void sync(ProjectRegistry projectRegistry) throws ServerException {
		sync(projectRegistry,"blank");
	}
    /**在代理和主程序上同步项目配置状态
     * Synchronizes Project Config state on Agent and Master
     * @param projectRegistry project registry
     * @throws ServerException
     */
    public final void sync(ProjectRegistry projectRegistry,String type) throws ServerException {
    	if(!"gzproject".equals(type)){
    		LOG.info("*********************************sync:普通*********************************"+type);
    		List<? extends ProjectConfig> remote = getProjects();
            // check on removed
            List <ProjectConfig> removed = new ArrayList<>();
            for(ProjectConfig r  : remote) {
                if(projectRegistry.getProject(r.getPath()) == null)
                    removed.add(r);
            }
            for(ProjectConfig r : removed){
                removeProject(r);
            }
            // update or add
            for(RegisteredProject project : projectRegistry.getProjects()) {
                if(!project.isSynced() && !project.isDetected()) {               
                	    final ProjectConfig config = new NewProjectConfigImpl(project.getPath(),
                                project.getType(),
                                project.getMixins(),
                                project.getName(),
                                project.getDescription(),
                                project.getPersistableAttributes(),
                                null,
                                project.getSource());
    					LOG.info("project=="+config.toString());
    					boolean found = false;
    					for(ProjectConfig r  : remote) {
    						LOG.info("3333333333333333333333333333333");
    						LOG.info(r.getPath()+"******************"+project.getPath());
    						if(r.getPath().equals(project.getPath())) {
    							updateProject(config);
    							found = true;
    						}
    					}
    					if(!found){
    						LOG.info("55555555555555555555555555555555");
    						addProject(config);
    					}
    					project.setSync();
            	}            
            }
    	}else{
    		LOG.info("*********************************sync:构造*********************************"+type);
    		 List<? extends GZProjectConfig> gzremote = getGZProjects();
    	        // check on removed
    	        List <GZProjectConfig> gzremoved = new ArrayList<>();
    	        for(GZProjectConfig r  : gzremote) {
    	            if(projectRegistry.getProject(r.getPath()) == null)
    	                gzremoved.add(r);
    	        }
    	        for(GZProjectConfig r : gzremoved){
    	            removeGZProject(r);
    	        }
    	        // update or add
    	        for(RegisteredProject project : projectRegistry.getProjects()) {
    	            if(!project.isSynced() && !project.isDetected()) {
    	            		final GZProjectConfig gzconfig = new NewGZProjectConfigImpl(project.getPath(),
    	                            project.getType(),
    	                            project.getMixins(),
    	                            project.getName(),
    	                            project.getDescription(),
    	                            project.getPersistableAttributes(),
    	                            null,
    	                            project.getSource());
    	                    LOG.info("gzproject=="+gzconfig.toString());
    	                    boolean found = false;
    	                    for(GZProjectConfig r  : gzremote) {
    	                    	LOG.info(r.getPath()+"************************"+project.getPath());
    	                        if(r.getPath().equals(project.getPath())) {
    	                            updateGZProject(gzconfig);
    	                            found = true;
    	                        }
    	                    }
    	                    if(!found){
    	                    	LOG.info("444444444444444444444444444444444444");
    	                        addGZProject(gzconfig);
    	                    }
    	                    project.setSync();
    	        	}            
    	        }
    	}
    }

    /**
     * @return projects from Workspace Config
     * @throws ServerException
     */
    public abstract List<? extends ProjectConfig> getProjects() throws ServerException;

    /**
     * @return workspace ID
     */
    public abstract String getWorkspaceId();

    /**
     * Adds project to Workspace Config
     * @param project the project config
     * @throws ServerException
     */
    protected abstract void addProject(ProjectConfig project) throws ServerException;

    /**
     * Updates particular project in Workspace Config
     * @param project the project config
     * @throws ServerException
     */
    protected abstract void updateProject(ProjectConfig project) throws ServerException;

    /**
     * Removes particular project in Workspace Config
     * @param project the project config
     * @throws ServerException
     */
    protected abstract void removeProject(ProjectConfig project) throws ServerException;
    
  //==========================20180205==========================//
    /**
     * @return gzprojects from Workspace Config
     * @throws ServerException
     */
    public abstract List<? extends GZProjectConfig> getGZProjects() throws ServerException;
    
    /**
     * Adds gzproject to Workspace Config
     * @param gzproject the project config
     * @throws ServerException
     */
    protected abstract void addGZProject(GZProjectConfig project) throws ServerException;

    /**
     * Updates particular gzproject in Workspace Config
     * @param gzproject the project config
     * @throws ServerException
     */
    protected abstract void updateGZProject(GZProjectConfig project) throws ServerException;

    /**
     * Removes particular gzproject in Workspace Config
     * @param gzproject the project config
     * @throws ServerException
     */
    protected abstract void removeGZProject(GZProjectConfig project) throws ServerException;

}
