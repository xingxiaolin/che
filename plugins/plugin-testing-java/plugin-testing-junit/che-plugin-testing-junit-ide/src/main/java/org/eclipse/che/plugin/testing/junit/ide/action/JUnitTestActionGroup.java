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
package org.eclipse.che.plugin.testing.junit.ide.action;

import com.google.inject.Inject;
import org.eclipse.che.ide.api.action.Action;
import org.eclipse.che.ide.api.action.ActionManager;
import org.eclipse.che.ide.api.action.DefaultActionGroup;
import org.eclipse.che.plugin.testing.ide.action.TestAction;

/** Registrar of JUnit run/debug actions. */
public class JUnitTestActionGroup implements TestAction {
  public static final String TEST_ACTION_RUN = "TestJUnitActionRun";
  public static final String TEST_ACTION_DEBUG = "TestJUnitActionDebug";
  private final Action runTestAction;
  private final Action debugTestAction;

  @Inject
  public JUnitTestActionGroup(
      ActionManager actionManager,
      RunJUnitTestAction runJUnitTestAction,
      DebugJUnitTestAction debugJUnitTestAction) {
    actionManager.registerAction(TEST_ACTION_RUN, runJUnitTestAction);
    actionManager.registerAction(TEST_ACTION_DEBUG, debugJUnitTestAction);
    this.runTestAction = runJUnitTestAction;
    this.debugTestAction = debugJUnitTestAction;
  }

  @Override
  public void addMainMenuItems(DefaultActionGroup testMainMenu) {
    testMainMenu.add(runTestAction);
    testMainMenu.add(debugTestAction);
  }

  @Override
  public void addContextMenuItems(DefaultActionGroup testContextMenu) {
    testContextMenu.add(runTestAction);
    testContextMenu.add(debugTestAction);
  }
}
