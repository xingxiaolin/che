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
package org.eclipse.che.ide.api.app;

import com.google.common.annotations.Beta;

import org.eclipse.che.api.core.model.factory.Factory;
import org.eclipse.che.api.core.model.workspace.Workspace;
import org.eclipse.che.api.factory.shared.dto.FactoryDto;
import org.eclipse.che.api.workspace.shared.dto.WorkspaceConfigDto;
import org.eclipse.che.ide.api.machine.ActiveRuntime;
import org.eclipse.che.ide.api.machine.DevMachine;
import org.eclipse.che.ide.api.resources.Container;
import org.eclipse.che.ide.api.resources.Project;
import org.eclipse.che.ide.api.resources.Resource;
import org.eclipse.che.ide.resource.Path;

import java.util.List;
import java.util.Map;

/**表示IDE应用程序的当前上下文。
 * Represents current context of the IDE application.
 *
 * @author Vitaly Parfonov
 * @author Artem Zatsarynnyi
 * @author Vlad Zhukovskyi
 * @author Yevhenii Voevodin
 */
public interface AppContext {

    /**返回工作区根容器，它是已注册项目的所有者。
     * Returns the workspace root container, which is holder of registered projects.
     *
     * @return the workspace root
     * @since 4.4.0
     */
    @Beta
    Container getWorkspaceRoot();

    /**返回当前工作区中已注册的项目。如果未注册任何项目，则返回空数组。
     * Returns the registered projects in current workspace. 
     * If no projects were registered before,then empty array is returned.
     * @return the registered projects
     * @see Container#newProject()
     * @since 4.4.0
     */
    @Beta
    Project[] getProjects();

    /**返回当前上下文中的资源。按当前上下文方式，如果IDE中，该资源可能在指定的部分中使用。例如,
     *  项目部分可以提供所属的资源。选择此时，编辑器可以提供打开的资源，全文搜索可以提供正在选择的资源。
     * Returns the resource which is in current context. By current context means, that resource may be
     * in use in specified part if IDE. For example, project part may provide resource which is under
     * selection at this moment, editor may provide resource which is open, full text search may provide
     * resource which is under selection.
     * <p/>
     * If specified part provides more than one resource, then last selected resource is returned.
     * <p/>
     * May return {@code null} if there is no resource in context.
     *
     * @return the resource in context
     * @see Resource
     * @see #getResources()
     * @since 4.4.0
     */
    @Beta
    Resource getResource();

    /**
     * Returns the resources which are in current context. By current context means, that resources may be
     * in use in specified part if IDE. For example, project part may provide resources which are under
     * selection at this moment, editor may provide resource which is open, full text search may provide
     * resources which are under selection.
     * <p/>
     * If specified part provides more than one resource, then all selected resources are returned.
     * <p/>
     * May return {@code null} if there is no resources in context.
     *
     * @return the resource in context
     * @see Resource
     * @see #getResource()
     * @since 4.4.0
     */
    @Beta
    Resource[] getResources();

    /**返回处于上下文中的根项目。
     * Returns the root project which is in context. To find out specified sub-project in context, method
     * {@link #getResource()} should be called. Resource is bound to own project and to get {@link Project}
     * instance from {@link Resource}, method {@link Resource#getRelatedProject()} should be called.
     * <p/>
     * May return {@code null} if there is no project in context.
     *
     * @return the root project or {@code null}
     * @see Project
     * @since 4.4.0
     */
    @Beta
    Project getRootProject();

    /**
     * Returns the workspace human readable name.
     *
     * @return the workspace name
     * @see WorkspaceConfigDto#getName()
     * @since 4.3.0
     */
    @Beta
    String getWorkspaceName();

    /**返回开发人员机器的实例（工作区被绑定）
     * Returns instance  of the developer machine (where workspace is bound).
     *
     * @return the object which describes developer machine
     * @see DevMachine
     * @since 4.2.0
     */
    DevMachine getDevMachine();

    /**
     * Returns the path where projects are stored on file system.
     *
     * @return the path to projects root.
     * @since 4.2.0
     */
    Path getProjectsRoot();

    /**
     * Returns list of start-up actions with parameters that comes form URL during IDE initialization.
     *
     * @return the list of actions
     * @see StartUpAction
     */
    List<StartUpAction> getStartAppActions();

    /**
     * Returns current user.
     *
     * @return current user
     */
    CurrentUser getCurrentUser();

    /**
     * Returns list of projects paths which are in importing state.
     *
     * @return list of project paths
     */
    List<String> getImportingProjects();

    /**
     * Adds project path to list of projects which are in importing state.
     *
     * @param pathToProject
     *         project path
     */
    void addProjectToImporting(String pathToProject);

    /**
     * Removes project path to list of projects which are in importing state.
     *
     * @param pathToProject
     *         project path
     */
    void removeProjectFromImporting(String pathToProject);

    /**
     * List of action with params that comes from startup URL.
     * Can be processed after IDE initialization as usual after
     * starting ws-agent.
     */
    void setStartUpActions(List<StartUpAction> startUpActions);

    /**
     * Returns {@link Factory} instance which id was set on startup,
     * or {@code null} if no factory was specified.
     *
     * @return loaded factory or {@code null}
     */
    FactoryDto getFactory();

    void setFactory(FactoryDto factory);

    String getWorkspaceId();

    /**
     * Returns {@link Workspace} instance of current workspace.
     *
     * @return current workspace
     */
    Workspace getWorkspace();

    /**
     * Sets current workspace.
     *
     * @param workspace
     *         current workspace or {@code null}
     */
    void setWorkspace(Workspace workspace);

    ActiveRuntime getActiveRuntime();


    String getMasterEndpoint();


    String getDevAgentEndpoint();

    /**
     * Returns web application identifier. Most obvious use - to distinguish web applications
     * on server side (e.g. connected via websocket)
     *
     * @return identifier
     */
    String getAppId();

    /**
     * Returns context properties, key-value storage that allows to store
     * data in the context for plugins and extensions.
     *
     * @return a modifiable properties map
     * @since 5.11.0
     */
    Map<String, String> getProperties();
}
