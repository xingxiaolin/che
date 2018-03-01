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

import org.eclipse.che.api.core.NotFoundException;
import org.eclipse.che.api.core.model.project.type.Attribute;
import org.eclipse.che.api.project.server.RegisteredGZProject.GZProblem;
import org.eclipse.che.api.project.server.type.ProjectTypeDef;
import org.eclipse.che.api.project.server.type.ProjectTypeRegistry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import static com.google.common.collect.Lists.newArrayList;
import static java.lang.String.format;
import static org.eclipse.che.api.core.ErrorCodes.ATTRIBUTE_NAME_PROBLEM;
import static org.eclipse.che.api.core.ErrorCodes.PROJECT_TYPE_IS_NOT_REGISTERED;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author gazarenkov
 */
public class GZProjectTypes {
	private static final Logger LOG = LoggerFactory.getLogger(GZProjectTypes.class);
    private final String                      projectPath;
    private final ProjectTypeRegistry         projectTypeRegistry;
    private       ProjectTypeDef              primary;
    private final Map<String, ProjectTypeDef> mixins;
    private final Map<String, ProjectTypeDef> all;
    private final Map<String, Attribute>      attributeDefs;
    private final List<GZProblem>           gzproblems;

    GZProjectTypes(String projectPath, String type, List<String> mixinTypes, ProjectTypeRegistry projectTypeRegistry, List<GZProblem> gzproblems) {
		   mixins = new HashMap<>();
		   all = new HashMap<>();
		   attributeDefs = new HashMap<>();
		   this.gzproblems = gzproblems != null ? gzproblems : newArrayList();
		
		   this.projectTypeRegistry = projectTypeRegistry;
		   this.projectPath = projectPath;
		
		   ProjectTypeDef tmpPrimary;
		   LOG.info("*****************************222GZProjectType.type===="+type);
		   if (type == null) {
		       this.gzproblems.add(new GZProblem(PROJECT_TYPE_IS_NOT_REGISTERED, "No primary type defined for " + projectPath + " Base Project Type assigned."));
		       tmpPrimary = ProjectTypeRegistry.BASE_TYPE;
		   } else {
		       try {
		    	   LOG.info("222qqqqqqqqqqqqqqqqqqqqqqqqqqq");
		           tmpPrimary = projectTypeRegistry.getProjectType(type);
		       } catch (NotFoundException e) {
		           this.gzproblems.add(new GZProblem(PROJECT_TYPE_IS_NOT_REGISTERED, "Primary type " + type + " defined for " + projectPath +
		                                        " is not registered. Base Project Type assigned."));
		           tmpPrimary = ProjectTypeRegistry.BASE_TYPE;
		           LOG.info("222wwwwwwwwwwwwwwwwwwwww==//"+tmpPrimary.getId());
		       }
		       LOG.info("222rrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrr==//" +tmpPrimary.getId());
		       if (!tmpPrimary.isPrimaryable()) {
		           this.gzproblems.add(new GZProblem(PROJECT_TYPE_IS_NOT_REGISTERED, "Project type " + tmpPrimary.getId() + " is not allowable to be primary type. Base Project Type assigned."));
		           tmpPrimary = ProjectTypeRegistry.BASE_TYPE;
		           LOG.info("222tttttttttttttttttttttttttttttttt==//"+tmpPrimary.getId());
		       }
		   }
		
		   this.primary = tmpPrimary;
		   all.put(primary.getId(), primary);
		
		   List<String> mixinsFromConfig = mixinTypes;
		
		   if (mixinsFromConfig == null) {
		       mixinsFromConfig = new ArrayList<>();
		   }
		
		   for (Attribute attr : primary.getAttributes()) {
		       attributeDefs.put(attr.getName(), attr);
		   }
		
		   for (String mixinFromConfig : mixinsFromConfig) {
		       if (mixinFromConfig.equals(primary.getId())) {
		           continue;
		       }
		
		       final ProjectTypeDef mixin;
		       try {
		           mixin = projectTypeRegistry.getProjectType(mixinFromConfig);
		       } catch (NotFoundException e) {
		           this.gzproblems.add(new GZProblem(PROJECT_TYPE_IS_NOT_REGISTERED, "Project type " + mixinFromConfig + " is not registered. Skipped."));
		           continue;
		       }
		
		       if (!mixin.isMixable()) {
		           this.gzproblems.add(new GZProblem(PROJECT_TYPE_IS_NOT_REGISTERED, "Project type " + mixin + " is not allowable to be mixin. It not mixable. Skipped."));
		           continue;
		       }
		
		       if (!mixin.isPersisted()) {
		           continue;
		       }
		
		       // detect duplicated attributes
		       for (Attribute attr : mixin.getAttributes()) {
		           final String attrName = attr.getName();
		           final Attribute attribute = attributeDefs.get(attrName);
		           if (attribute != null && !attribute.getProjectType().equals(attr.getProjectType())) {
		               this.gzproblems.add(new GZProblem(ATTRIBUTE_NAME_PROBLEM,
		                                             format("Attribute name conflict. Duplicated attributes detected for %s. " +
		                                                    "Attribute %s declared in %s already declared in %s. Skipped.",
		                                                    projectPath, attrName, mixin.getId(), attribute.getProjectType())));
		               continue;
		           }
		           attributeDefs.put(attrName, attr);
		       }
		       // Silently remove repeated items from mixins if any
		       mixins.put(mixinFromConfig, mixin);
		       all.put(mixinFromConfig, mixin);
		   }
	}

    public Map<String, Attribute> getAttributeDefs() {
        return attributeDefs;
    }

    public ProjectTypeDef getPrimary() {
        return primary;
    }

    public Map<String, ProjectTypeDef> getMixins() {
        return mixins;
    }

    public Map<String, ProjectTypeDef> getAll() {
        return all;
    }

    /**
     * Reset project types and atrributes after initialization
     * in case when some attributes are not valid
     * (for instance required attributes are not initialized)
     *
     * @param attributesToDel - invalid attributes
     */
    void reset(Set<Attribute> attributesToDel) {

        Set<String> ptsToDel = new HashSet<>();
        for (Attribute attr : attributesToDel) {
            ptsToDel.add(attr.getProjectType());
        }

        Set<String> attrNamesToDel = new HashSet<>();
        for (String pt : ptsToDel) {
            ProjectTypeDef typeDef = all.get(pt);
            for (Attribute attrDef : typeDef.getAttributes()) {
                attrNamesToDel.add(attrDef.getName());
            }
        }

        // remove project types
        for (String typeId : ptsToDel) {
            this.all.remove(typeId);
            if (this.primary.getId().equals(typeId)) {
                this.primary = ProjectTypeRegistry.BASE_TYPE;
                this.all.put(ProjectTypeRegistry.BASE_TYPE.getId(), ProjectTypeRegistry.BASE_TYPE);
            } else {
                mixins.remove(typeId);
            }
        }

        // remove attributes
        for (String attr : attrNamesToDel) {
            this.attributeDefs.remove(attr);
        }
    }

    void addTransient(FolderEntry projectFolder) {
        for (ProjectTypeDef pt : projectTypeRegistry.getProjectTypes()) {
            // NOTE: Only mixable types allowed
            if (pt.isMixable() && !pt.isPersisted() && pt.resolveSources(projectFolder).matched()) {
                all.put(pt.getId(), pt);
                mixins.put(pt.getId(), pt);
                for (Attribute attr : pt.getAttributes()) {
                    final String attrName = attr.getName();
                    final Attribute attribute = attributeDefs.get(attr.getName());
                    // If attr from mixin is going to be added but we already have some attribute with the same name,
                    // check whether it's the same attribute that comes from the common parent PT, e.g. from Base PT.
                    if (attribute != null && !attribute.getProjectType().equals(attr.getProjectType())) {
                        gzproblems.add(new GZProblem(ATTRIBUTE_NAME_PROBLEM,
                                                 format("Attribute name conflict. Duplicated attributes detected for %s. " +
                                                        "Attribute %s declared in %s already declared in %s. Skipped.",
                                                        projectPath, attrName, pt.getId(), attribute.getProjectType())));
                        continue;
                    }

                    attributeDefs.put(attrName, attr);
                }
            }
        }
    }
    
    //==========================
   
}
