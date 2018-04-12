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
package org.eclipse.che.selenium.workspaces;

import static org.eclipse.che.selenium.core.constant.TestMenuCommandsConstants.Workspace.STOP_WORKSPACE;
import static org.eclipse.che.selenium.core.constant.TestMenuCommandsConstants.Workspace.WORKSPACE;

import com.google.inject.Inject;
import org.eclipse.che.selenium.core.workspace.TestWorkspace;
import org.eclipse.che.selenium.pageobject.Ide;
import org.eclipse.che.selenium.pageobject.Menu;
import org.eclipse.che.selenium.pageobject.ToastLoader;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/** @author Andrey Chizhikov */
public class CheckStopStartWsTest {
  @Inject private TestWorkspace testWorkspace;
  @Inject private ToastLoader toastLoader;
  @Inject private Menu menu;
  @Inject private Ide ide;

  @BeforeClass
  public void setUp() throws Exception {
    ide.open(testWorkspace);
  }

  @Test
  public void checkStopStartWorkspaceTest() {
    ide.waitOpenedWorkspaceIsReadyToUse();

    menu.runCommand(WORKSPACE, STOP_WORKSPACE);
    toastLoader.waitExpectedTextInToastLoader("Workspace is not running");

    toastLoader.clickOnStartButton();
    ide.waitOpenedWorkspaceIsReadyToUse();
  }
}
