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
package org.eclipse.che.selenium.dashboard;

import static org.eclipse.che.selenium.pageobject.dashboard.ProjectSourcePage.Sources.ZIP;

import com.google.inject.Inject;
import java.util.concurrent.ExecutionException;
import org.eclipse.che.commons.lang.NameGenerator;
import org.eclipse.che.selenium.core.client.TestWorkspaceServiceClient;
import org.eclipse.che.selenium.core.constant.TestStacksConstants;
import org.eclipse.che.selenium.core.user.TestUser;
import org.eclipse.che.selenium.core.webdriver.SeleniumWebDriverHelper;
import org.eclipse.che.selenium.pageobject.Ide;
import org.eclipse.che.selenium.pageobject.ProjectExplorer;
import org.eclipse.che.selenium.pageobject.dashboard.Dashboard;
import org.eclipse.che.selenium.pageobject.dashboard.NewWorkspace;
import org.eclipse.che.selenium.pageobject.dashboard.ProjectSourcePage;
import org.eclipse.che.selenium.pageobject.dashboard.workspaces.Workspaces;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/** @author Andrey Chizhikov */
public class ImportProjectFromZipTest {
  private final String WORKSPACE = NameGenerator.generate("ImptPrjFromZip", 4);
  private static final String PROJECT_NAME = "master";

  @Inject private Dashboard dashboard;
  @Inject private ProjectExplorer explorer;
  @Inject private NewWorkspace newWorkspace;
  @Inject private ProjectSourcePage projectSourcePage;
  @Inject private SeleniumWebDriverHelper seleniumWebDriverHelper;
  @Inject private TestWorkspaceServiceClient workspaceServiceClient;
  @Inject private TestUser defaultTestUser;
  @Inject private Workspaces workspaces;
  @Inject private Ide ide;

  @BeforeClass
  public void setUp() {
    dashboard.open();
  }

  @AfterClass
  public void tearDown() throws Exception {
    workspaceServiceClient.delete(WORKSPACE, defaultTestUser.getName());
  }

  @Test
  public void importProjectFromZipTest() throws ExecutionException, InterruptedException {
    dashboard.waitDashboardToolbarTitle();
    dashboard.selectWorkspacesItemOnDashboard();

    workspaces.clickOnAddWorkspaceBtn();
    newWorkspace.waitToolbar();
    newWorkspace.selectStack(TestStacksConstants.JAVA.getId());
    newWorkspace.typeWorkspaceName(WORKSPACE);

    projectSourcePage.clickOnAddOrImportProjectButton();
    projectSourcePage.selectSourceTab(ZIP);
    projectSourcePage.typeZipLocation(
        "https://github.com/iedexmain1/multimodule-project/archive/master.zip");
    projectSourcePage.skipRootFolder();
    projectSourcePage.clickOnAddProjectButton();

    newWorkspace.clickOnCreateButtonAndOpenInIDE();
    seleniumWebDriverHelper.switchToIdeFrameAndWaitAvailability();

    ide.waitOpenedWorkspaceIsReadyToUse();
    explorer.waitItem(PROJECT_NAME);
    explorer.waitAndSelectItem(PROJECT_NAME);
    explorer.openContextMenuByPathSelectedItem(PROJECT_NAME);
  }
}
