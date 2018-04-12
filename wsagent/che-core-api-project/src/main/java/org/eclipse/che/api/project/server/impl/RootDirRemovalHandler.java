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
package org.eclipse.che.api.project.server.impl;

import static org.eclipse.che.api.fs.server.WsPathUtils.ROOT;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.eclipse.che.api.core.ServerException;
import org.eclipse.che.api.project.server.ProjectManager;
import org.eclipse.che.api.watcher.server.FileWatcherManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Cleans up project config registry when some project is removed bypassing {@link ProjectManager}
 *
 * @author Roman Nikitenko
 */
@Singleton
public class RootDirRemovalHandler {
  private static final Logger LOGGER = LoggerFactory.getLogger(RootDirRemovalHandler.class);

  private final ProjectSynchronizer projectSynchronizer;
  private final ProjectConfigRegistry projectConfigRegistry;
  private final FileWatcherManager fileWatcherManager;

  @Inject
  public RootDirRemovalHandler(
      ProjectSynchronizer projectSynchronizer,
      ProjectConfigRegistry projectConfigRegistry,
      FileWatcherManager fileWatcherManager) {
    this.projectSynchronizer = projectSynchronizer;
    this.projectConfigRegistry = projectConfigRegistry;
    this.fileWatcherManager = fileWatcherManager;
  }

  @PostConstruct
  private void registerOperation() {
    fileWatcherManager.registerByPath(ROOT, arg -> {}, arg -> {}, this::consumeDelete);
  }

  private void consumeDelete(String wsPath) {
    try {
      if (projectConfigRegistry.isRegistered(wsPath)) {
        projectConfigRegistry.remove(wsPath);
        projectSynchronizer.synchronize();
      }
    } catch (ServerException e) {
      LOGGER.error(
          "Removing project '{}' is detected. Cleaning project config registry is failed: {}",
          wsPath,
          e.getMessage());
    }
  }
}
