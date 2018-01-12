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
package org.eclipse.che.plugin.testing.testng.ide.action;

import static java.util.Collections.singletonList;
import static org.eclipse.che.ide.part.perspectives.project.ProjectPerspective.PROJECT_PERSPECTIVE_ID;

import com.google.inject.Inject;
import org.eclipse.che.ide.api.action.ActionEvent;
import org.eclipse.che.ide.api.app.AppContext;
import org.eclipse.che.ide.api.debug.DebugConfigurationsManager;
import org.eclipse.che.ide.api.notification.NotificationManager;
import org.eclipse.che.ide.dto.DtoFactory;
import org.eclipse.che.ide.util.Pair;
import org.eclipse.che.plugin.testing.ide.TestServiceClient;
import org.eclipse.che.plugin.testing.ide.detector.TestDetector;
import org.eclipse.che.plugin.testing.ide.handler.TestingHandler;
import org.eclipse.che.plugin.testing.ide.view.TestResultPresenter;
import org.eclipse.che.plugin.testing.testng.ide.TestNgLocalizationConstant;
import org.eclipse.che.plugin.testing.testng.ide.TestNgResources;

/** Action for debugging TestNg test. */
public class DebugTestNgTestAction extends AbstractTestNgTestAction {

  @Inject
  public DebugTestNgTestAction(
      TestNgResources resources,
      TestDetector testDetector,
      TestServiceClient client,
      TestingHandler testingHandler,
      DtoFactory dtoFactory,
      NotificationManager notificationManager,
      DebugConfigurationsManager debugConfigurationsManager,
      AppContext appContext,
      TestResultPresenter testResultPresenter,
      TestNgLocalizationConstant localization) {
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
        localization.actionDebugDescription(),
        localization.actionDebugTestTitle(),
        resources.testIcon());
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    Pair<String, String> frameworkAndTestName = Pair.of(TESTNG_FRAMEWORK_NAME, null);
    actionPerformed(frameworkAndTestName, true);
  }
}
