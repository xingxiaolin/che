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
import org.eclipse.che.api.core.model.project.GZProjectConfig;
import org.eclipse.che.api.core.model.project.SourceStorage;
import org.eclipse.che.api.core.model.project.type.Attribute;
import org.eclipse.che.api.core.model.project.type.Value;
import org.eclipse.che.api.project.server.type.AttributeValue;
import org.eclipse.che.api.project.server.type.ProjectTypeDef;
import org.eclipse.che.api.project.server.type.ProjectTypeRegistry;
import org.eclipse.che.api.project.server.type.ValueProvider;
import org.eclipse.che.api.project.server.type.ValueStorageException;
import org.eclipse.che.api.project.server.type.Variable;
import org.eclipse.che.api.vfs.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import static java.lang.String.format;
import static org.eclipse.che.api.core.ErrorCodes.ATTRIBUTE_NAME_PROBLEM;
import static org.eclipse.che.api.core.ErrorCodes.NO_PROJECT_CONFIGURED_IN_WS;
import static org.eclipse.che.api.core.ErrorCodes.NO_PROJECT_ON_FILE_SYSTEM;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Internal Project implementation.
 * It is supposed that it is object always consistent.
 *
 * @author gazarenkov
 */
public class RegisteredGZProject implements GZProjectConfig {
	private static final Logger LOG = LoggerFactory.getLogger(RegisteredGZProject.class);
    private final List<GZProblem>      problems;
    private final Map<String, Value> attributes;

    private final FolderEntry   folder;
    private final GZProjectConfig config;
    private       boolean       updated;
    private       boolean       detected;
    private final GZProjectTypes  types;

    /**
     * Either root folder or config can be null, in this case GZProject is configured with problem.
     *
     * @param folder
     *         root local folder or null
     * @param config
     *         gzproject configuration in workspace
     * @param updated
     *         if this object was updated, i.e. no more synchronized with workspace master
     * @param detected
     *         if this gzproject was detected, initialized when "parent" gzproject initialized
     * @param projectTypeRegistry
     *         gzproject type registry
     * @throws ServerException
     *         when path for project is undefined
     */
    RegisteredGZProject(FolderEntry folder,
                      GZProjectConfig config,
                      boolean updated,
                      boolean detected,
                      ProjectTypeRegistry projectTypeRegistry) throws ServerException {
        problems = new ArrayList<>();
        attributes = new HashMap<>();
        Path path;
        if (folder != null) {
            path = folder.getPath();
        } else if (config != null) {
            path = Path.of(config.getPath());
        } else {
            throw new ServerException("Invalid GZProject Configuration. Path undefined.");
        }
        this.folder = folder;
        this.config = config == null ? new NewGZProjectConfigImpl(path) : config;
        this.updated = updated;
        this.detected = detected;

        if (folder == null || folder.isFile()) {
            problems.add(new GZProblem(NO_PROJECT_ON_FILE_SYSTEM, "No gzproject folder on file system " + this.config.getPath()));
        }
        if (config == null) {
            problems.add(new GZProblem(NO_PROJECT_CONFIGURED_IN_WS, "No gzproject configured in workspace " + this.config.getPath()));
        }
        LOG.info("this.config.getType()=====/"+this.config.getType());
        // 1. init project types
        this.types = new GZProjectTypes(this.config.getPath(), this.config.getType(), this.config.getMixins(), projectTypeRegistry, problems);
        // 2. init transient (implicit, like git) project types.
        types.addTransient(folder);
        // 3. initialize attributes
        initAttributes();
    }


    /**
     * Initialize gzproject attributes.
     * Note: the problem with {@link GZProblem#code} = 13 will be added when a value for some attribute is not initialized
     */
    private void initAttributes() {
        // we take only defined attributes, others ignored
        for (Map.Entry<String, Attribute> entry : types.getAttributeDefs().entrySet()) {
            final Attribute definition = entry.getValue();
            final String name = entry.getKey();
            AttributeValue value = new AttributeValue(config.getAttributes().get(name));
            if (!definition.isVariable()) {
                // constant, value always assumed as stated in definition
                attributes.put(name, definition.getValue());
            } else {
                // variable
                final Variable variable = (Variable)definition;
                // value provided
                if (variable.isValueProvided()) {
                    final ValueProvider valueProvider = variable.getValueProviderFactory().newInstance(folder);
                    if (folder != null) {
                        try {
                            if (!valueProvider.isSettable() || value.isEmpty()) {
                                // get provided value
                                value = new AttributeValue(valueProvider.getValues(name));
                            } else {
                                // set provided (not empty) value
                                valueProvider.setValues(name, value.getList());
                            }
                        } catch (ValueStorageException e) {
                            final GZProblem problem = new GZProblem(ATTRIBUTE_NAME_PROBLEM,
                                                                format("Value for attribute %s is not initialized, caused by: %s",
                                                                       variable.getId(), e.getLocalizedMessage()));
                            this.problems.add(problem);
                        }

                    } else {
                        continue;
                    }
                }

                if (value.isEmpty() && variable.isRequired()) {
                    final GZProblem problem = new GZProblem(ATTRIBUTE_NAME_PROBLEM,
                                                        "Value for required attribute is not initialized " + variable.getId());
                    this.problems.add(problem);
                    //throw new ProjectTypeConstraintException("Value for required attribute is not initialized " + variable.getId());
                }

                if (!value.isEmpty()) {
                    this.attributes.put(name, value);
                }
            }
        }
    }

    /**
     * @return primary gzproject type
     */
    public ProjectTypeDef getProjectType() {
        return types.getPrimary();
    }

    /**
     * @return mixin gzproject types
     */
    public Map<String, ProjectTypeDef> getMixinTypes() {
        return types.getMixins();
    }

    /**
     * @return all gzproject types (primary + mixins, convenient method)
     */
    public Map<String, ProjectTypeDef> getTypes() {
        return types.getAll();
    }

    /**
     * @return attributes as name / Value Map
     */
    public Map<String, Value> getAttributeEntries() {
        return attributes;
    }

    /**
     * @return whether this project is synchronized with Workspace storage
     * On the other words this project is not updated
     */
    public boolean isSynced() {
        return !this.updated;
    }

    /**
     * should be called after synchronization with Workspace storage
     */
    public void setSync() {
        this.updated = false;
    }

    /**
     * @return whether this project is detected using Project Type resolver
     * If so it should not be persisted to Workspace storage
     */
    public boolean isDetected() {
        return detected;
    }

    /**
     * @return root folder or null
     */
    public FolderEntry getBaseFolder() {
        return folder;
    }

    /**
     * @return problems in case if root or config is null (project is not synced)
     */
    public List<GZProblem> getProblems() {
        return problems;
    }

    /**
     * @return list of Problems as a String
     */
    public String getProblemsStr() {
        StringBuilder builder = new StringBuilder();
        int i = 0;
        for( RegisteredGZProject.GZProblem prb : problems ) {
            builder.append("[").append(i++).append("] : ").append(prb.message).append("\n");
        }
        return builder.toString();
    }

    /**
     * @return non provided attributes, those attributes can be persisted to Workspace storage
     */
    public Map<String, List<String>> getPersistableAttributes() {
        Map<String, List<String>> attrs = new HashMap<>();
        for (HashMap.Entry<String, Value> entry : getAttributeEntries().entrySet()) {
            Attribute def = types.getAttributeDefs().get(entry.getKey());
            // not provided, not constants
            if (def != null &&
                ((def.isVariable() && ((Variable)def).getValueProviderFactory() == null)))
                attrs.put(entry.getKey(), entry.getValue().getList());
        }
        return attrs;
    }

    /* ------------------------------------------- */
    /* Implementation of GZProjectConfig interface   */
    /* ------------------------------------------- */

    @Override
    public String getPath() {
        return ProjectRegistry.absolutizePath(config.getPath());
    }

    @Override
    public String getName() {
        return config.getName();
    }

    @Override
    public String getDescription() {
        return config.getDescription();
    }

    @Override
    public SourceStorage getSource() {
        return config.getSource();
    }

    @Override
    public String getType() {
        return types.getPrimary().getId();
    }

    @Override
    public List<String> getMixins() {
        return types.getMixins().values()
                    .stream()
                    .filter(ProjectTypeDef::isPersisted)
                    .map(ProjectTypeDef::getId)
                    .collect(Collectors.toList());
    }

    @Override
    public Map<String, List<String>> getAttributes() {
        Map<String, List<String>> attrs = new HashMap<>();
        for (Map.Entry<String, Value> entry : getAttributeEntries().entrySet()) {
            attrs.put(entry.getKey(), entry.getValue().getList());
        }
        return attrs;
    }

    public static class GZProblem {
        GZProblem(int code, String message) {
            this.code = code;
            this.message = message;
        }

        int    code;
        String message;
    }
}
