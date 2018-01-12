/*
 * Copyright (c) 2012-2018 Red Hat, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Red Hat, Inc. - initial API and implementation
 */
package org.eclipse.che.plugin.urlfactory;

import static java.util.Collections.singletonList;

import java.util.List;
import javax.inject.Singleton;
import org.eclipse.che.api.factory.shared.dto.FactoryDto;
import org.eclipse.che.api.workspace.shared.dto.ProjectConfigDto;

/**
 * Merge or add inside a factory the source storage dto
 *
 * @author Florent Benoit
 */
@Singleton
public class ProjectConfigDtoMerger {

  /**
   * Apply the merging of project config dto including source storage dto into the existing factory
   *
   * <p>here are the following rules
   *
   * <ul>
   *   <li>no projects --> add whole project
   *   <li>if projects
   *       <ul>
   *         <li>: if there is only one project: add source if missing
   *         <li>if many projects: do nothing
   *       </ul>
   * </ul>
   *
   * @param factory
   * @param computedProjectConfig
   * @return
   */
  public FactoryDto merge(FactoryDto factory, ProjectConfigDto computedProjectConfig) {

    final List<ProjectConfigDto> projects = factory.getWorkspace().getProjects();
    if (projects == null || projects.isEmpty()) {
      factory.getWorkspace().setProjects(singletonList(computedProjectConfig));
      return factory;
    }

    // if we're here, they are projects
    if (projects.size() == 1) {
      ProjectConfigDto projectConfig = projects.get(0);
      if (projectConfig.getSource() == null)
        projectConfig.setSource(computedProjectConfig.getSource());
    }

    return factory;
  }
}
