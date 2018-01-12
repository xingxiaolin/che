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
package org.eclipse.che.plugin.testing.ide.action;

import static java.util.Collections.singletonList;
import static org.eclipse.che.ide.part.perspectives.project.ProjectPerspective.PROJECT_PERSPECTIVE_ID;

import com.google.inject.Singleton;
import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import org.eclipse.che.ide.api.action.ActionEvent;
import org.eclipse.che.ide.api.action.Presentation;
import org.eclipse.che.ide.api.app.AppContext;
import org.eclipse.che.ide.api.debug.DebugConfigurationsManager;
import org.eclipse.che.ide.api.notification.NotificationManager;
import org.eclipse.che.ide.dto.DtoFactory;
import org.eclipse.che.ide.util.Pair;
import org.eclipse.che.plugin.testing.ide.TestResources;
import org.eclipse.che.plugin.testing.ide.TestServiceClient;
import org.eclipse.che.plugin.testing.ide.detector.TestDetector;
import org.eclipse.che.plugin.testing.ide.handler.TestingHandler;
import org.eclipse.che.plugin.testing.ide.view.TestResultPresenter;

/** Action that allows to run tests from current editor. */
@Singleton
public class RunTestAction extends RunDebugTestAbstractAction {
  private TestDetector testDetector;

  @Inject
  public RunTestAction(
      TestServiceClient client,
      TestDetector testDetector,
      DtoFactory dtoFactory,
      TestResources testResources,
      AppContext appContext,
      NotificationManager notificationManager,
      DebugConfigurationsManager debugConfigurationsManager,
      TestingHandler testingHandler,
      TestResultPresenter testResultPresenter) {
    super(
        testDetector,
        testResultPresenter,
        testingHandler,
        debugConfigurationsManager,
        client,
        dtoFactory,
        appContext,
        notificationManager,
        singletonList(PROJECT_PERSPECTIVE_ID),
        "Run Test",
        "Run Test",
        testResources.testIcon());
    this.testDetector = testDetector;
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    Pair<String, String> frameworkAndTestName = getTestingFrameworkAndTestName();
    actionPerformed(frameworkAndTestName, false);
  }

  @Override
  public void updateInPerspective(@NotNull ActionEvent event) {
    Presentation presentation = event.getPresentation();
    presentation.setVisible(testDetector.isEditorInFocus());
    presentation.setEnabled(testDetector.isEnabled());
  }
}
