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
package org.eclipse.che.ide.ext.java.client.action;

import static com.google.common.base.Preconditions.checkState;
import static java.util.Collections.singletonList;
import static org.eclipse.che.ide.api.notification.StatusNotification.DisplayMode.EMERGE_MODE;
import static org.eclipse.che.ide.api.notification.StatusNotification.Status.FAIL;
import static org.eclipse.che.ide.ext.java.client.util.JavaUtil.isJavaProject;
import static org.eclipse.che.ide.part.perspectives.project.ProjectPerspective.PROJECT_PERSPECTIVE_ID;

import com.google.common.base.Optional;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import java.util.List;
import org.eclipse.che.api.promises.client.Operation;
import org.eclipse.che.api.promises.client.OperationException;
import org.eclipse.che.api.promises.client.PromiseError;
import org.eclipse.che.ide.api.action.AbstractPerspectiveAction;
import org.eclipse.che.ide.api.action.ActionEvent;
import org.eclipse.che.ide.api.app.AppContext;
import org.eclipse.che.ide.api.notification.NotificationManager;
import org.eclipse.che.ide.api.resources.Container;
import org.eclipse.che.ide.api.resources.Project;
import org.eclipse.che.ide.api.resources.Resource;
import org.eclipse.che.ide.ext.java.client.JavaLocalizationConstant;
import org.eclipse.che.ide.ext.java.client.JavaResources;
import org.eclipse.che.ide.ext.java.client.project.classpath.ClasspathResolver;
import org.eclipse.che.ide.ext.java.client.project.classpath.service.ClasspathServiceClient;
import org.eclipse.che.ide.ext.java.client.resource.SourceFolderMarker;
import org.eclipse.che.ide.ext.java.shared.dto.classpath.ClasspathEntryDto;

/**
 * The action which marks a folder into the project as source folder.
 *
 * @author Valeriy Svydenko
 */
@Singleton
public class MarkDirAsSourceAction extends AbstractPerspectiveAction {
  private final AppContext appContext;
  private final ClasspathServiceClient classpathService;
  private final ClasspathResolver classpathResolver;
  private final NotificationManager notificationManager;

  @Inject
  public MarkDirAsSourceAction(
      JavaResources javaResources,
      AppContext appContext,
      ClasspathServiceClient classpathService,
      ClasspathResolver classpathResolver,
      NotificationManager notificationManager,
      JavaLocalizationConstant locale) {
    super(
        singletonList(PROJECT_PERSPECTIVE_ID),
        locale.markDirectoryAsSourceAction(),
        locale.markDirectoryAsSourceDescription(),
        javaResources.sourceFolder());

    this.appContext = appContext;
    this.classpathService = classpathService;
    this.classpathResolver = classpathResolver;
    this.notificationManager = notificationManager;
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    final Resource resource = appContext.getResource();

    checkState(resource instanceof Container, "Parent should be a container");

    final Optional<Project> project = resource.getRelatedProject();

    checkState(project.isPresent());

    classpathService
        .getClasspath(project.get().getLocation().toString())
        .then(
            new Operation<List<ClasspathEntryDto>>() {
              @Override
              public void apply(List<ClasspathEntryDto> arg) throws OperationException {
                classpathResolver.resolveClasspathEntries(arg);
                classpathResolver.getSources().add(resource.getLocation().toString());
                classpathResolver.updateClasspath();
              }
            })
        .catchError(
            new Operation<PromiseError>() {
              @Override
              public void apply(PromiseError arg) throws OperationException {
                notificationManager.notify(
                    "Can't get classpath", arg.getMessage(), FAIL, EMERGE_MODE);
              }
            });
  }

  @Override
  public void updateInPerspective(ActionEvent e) {
    final Resource[] resources = appContext.getResources();

    if (resources == null || resources.length != 1) {
      e.getPresentation().setEnabledAndVisible(false);
      return;
    }

    Resource resource = resources[0];
    final boolean inJavaProject = isJavaProject(resource.getRelatedProject().get());

    e.getPresentation()
        .setEnabledAndVisible(
            inJavaProject
                && resource.isFolder()
                && !resource.getMarker(SourceFolderMarker.ID).isPresent());
  }
}
