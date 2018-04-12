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
package org.eclipse.che.ide.ext.git.client.plugins;

import static org.eclipse.che.ide.ext.git.client.GitUtil.getRootPath;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import org.eclipse.che.api.git.shared.FileChangedEventDto;
import org.eclipse.che.api.git.shared.StatusChangedEventDto;
import org.eclipse.che.api.project.shared.dto.event.FileWatcherEventType;
import org.eclipse.che.api.project.shared.dto.event.ProjectTreeStateUpdateDto;
import org.eclipse.che.ide.api.resources.Resource;
import org.eclipse.che.ide.api.vcs.VcsStatus;
import org.eclipse.che.ide.dto.DtoFactory;
import org.eclipse.che.ide.ext.git.client.GitEventSubscribable;
import org.eclipse.che.ide.ext.git.client.GitEventsSubscriber;
import org.eclipse.che.ide.part.explorer.project.ProjectExplorerPresenter;
import org.eclipse.che.ide.resource.Path;
import org.eclipse.che.ide.resources.ProjectTreeChangeHandler;
import org.eclipse.che.ide.resources.tree.FileNode;
import org.eclipse.che.ide.resources.tree.ResourceNode;
import org.eclipse.che.ide.ui.smartTree.Tree;

/**
 * Responsible for colorize project explorer items depending on their git status.
 *
 * @author Igor Vinokur
 * @author Mykola Morhun
 */
@Singleton
public class ProjectExplorerTreeColorizer implements GitEventsSubscriber {

  private final Provider<ProjectExplorerPresenter> projectExplorerPresenterProvider;
  private final ProjectTreeChangeHandler treeChangeHandler;
  private final DtoFactory dtoFactory;

  @Inject
  public ProjectExplorerTreeColorizer(
      GitEventSubscribable subscribeToGitEvents,
      Provider<ProjectExplorerPresenter> projectExplorerPresenterProvider,
      ProjectTreeChangeHandler treeChangeHandler,
      DtoFactory dtoFactory) {
    this.projectExplorerPresenterProvider = projectExplorerPresenterProvider;
    this.treeChangeHandler = treeChangeHandler;
    this.dtoFactory = dtoFactory;

    subscribeToGitEvents.addSubscriber(this);
  }

  @Override
  public void onFileChanged(String endpointId, FileChangedEventDto dto) {
    Tree tree = projectExplorerPresenterProvider.get().getTree();
    tree.getNodeStorage()
        .getAll()
        .stream()
        .filter(
            node ->
                node instanceof FileNode
                    && ((FileNode) node)
                        .getData()
                        .getLocation()
                        .equals(Path.valueOf(dto.getPath())))
        .forEach(
            node -> {
              ((FileNode) node)
                  .getData()
                  .asFile()
                  .setVcsStatus(VcsStatus.from(dto.getStatus().toString()));
              tree.refresh(node);
            });
  }

  @Override
  public void onGitStatusChanged(String endpointId, StatusChangedEventDto statusChangedEventDto) {
    Tree tree = projectExplorerPresenterProvider.get().getTree();
    tree.getNodeStorage()
        .getAll()
        .stream()
        .filter(
            node ->
                node instanceof FileNode
                    && statusChangedEventDto
                        .getProjectName()
                        .equals(getRootPath(((FileNode) node).getData().getLocation())))
        .map(node -> (FileNode) node)
        .map(ResourceNode::getData)
        .map(Resource::getLocation)
        .map(Path::toString)
        .forEach(
            location ->
                treeChangeHandler.handleFileChange(
                    dtoFactory
                        .createDto(ProjectTreeStateUpdateDto.class)
                        .withPath(location)
                        .withType(FileWatcherEventType.MODIFIED)));
  }
}
