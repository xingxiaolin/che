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

import static org.eclipse.che.selenium.pageobject.ProjectExplorer.FolderTypes.PROJECT_FOLDER;
import static org.eclipse.che.selenium.pageobject.dashboard.ProjectSourcePage.Template.CONSOLE_JAVA_SIMPLE;
import static org.eclipse.che.selenium.pageobject.dashboard.ProjectSourcePage.Template.WEB_JAVA_SPRING;
import static org.eclipse.che.selenium.pageobject.dashboard.workspaces.WorkspaceDetails.TabNames.PROJECTS;

import com.google.inject.Inject;
import java.util.concurrent.ExecutionException;
import org.eclipse.che.commons.lang.NameGenerator;
import org.eclipse.che.selenium.core.SeleniumWebDriver;
import org.eclipse.che.selenium.core.client.TestWorkspaceServiceClient;
import org.eclipse.che.selenium.core.constant.TestStacksConstants;
import org.eclipse.che.selenium.core.user.TestUser;
import org.eclipse.che.selenium.pageobject.Loader;
import org.eclipse.che.selenium.pageobject.MavenPluginStatusBar;
import org.eclipse.che.selenium.pageobject.NotificationsPopupPanel;
import org.eclipse.che.selenium.pageobject.ProjectExplorer;
import org.eclipse.che.selenium.pageobject.dashboard.CreateWorkspace;
import org.eclipse.che.selenium.pageobject.dashboard.Dashboard;
import org.eclipse.che.selenium.pageobject.dashboard.NavigationBar;
import org.eclipse.che.selenium.pageobject.dashboard.ProjectSourcePage;
import org.eclipse.che.selenium.pageobject.dashboard.workspaces.WorkspaceDetails;
import org.eclipse.che.selenium.pageobject.dashboard.workspaces.WorkspaceProjects;
import org.eclipse.che.selenium.pageobject.dashboard.workspaces.Workspaces;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/** @author Andrey Chizhikov */
public class CreateAndDeleteProjectsTest {

  private final String WORKSPACE = NameGenerator.generate("workspace", 4);

  @Inject private Dashboard dashboard;
  @Inject private WorkspaceProjects workspaceProjects;
  @Inject private WorkspaceDetails workspaceDetails;
  @Inject private NavigationBar navigationBar;
  @Inject private CreateWorkspace createWorkspace;
  @Inject private ProjectSourcePage projectSourcePage;
  @Inject private Loader loader;
  @Inject private ProjectExplorer explorer;
  @Inject private SeleniumWebDriver seleniumWebDriver;
  @Inject private TestWorkspaceServiceClient workspaceServiceClient;
  @Inject private TestUser defaultTestUser;
  @Inject private NotificationsPopupPanel notificationsPopupPanel;
  @Inject private MavenPluginStatusBar mavenPluginStatusBar;
  @Inject private Workspaces workspaces;

  @BeforeClass
  public void setUp() {
    dashboard.open();
  }

  @AfterClass
  public void tearDown() throws Exception {
    workspaceServiceClient.delete(WORKSPACE, defaultTestUser.getName());
  }

  @Test
  public void createAndDeleteProjectTest() throws ExecutionException, InterruptedException {
    dashboard.waitDashboardToolbarTitle();
    dashboard.selectWorkspacesItemOnDashboard();
    workspaces.clickOnAddWorkspaceBtn();
    createWorkspace.waitToolbar();

    createWorkspace.selectStack(TestStacksConstants.JAVA.getId());
    createWorkspace.typeWorkspaceName(WORKSPACE);
    projectSourcePage.clickOnAddOrImportProjectButton();
    projectSourcePage.selectSample(WEB_JAVA_SPRING);
    projectSourcePage.selectSample(CONSOLE_JAVA_SIMPLE);
    projectSourcePage.clickOnAddProjectButton();
    createWorkspace.clickOnCreateWorkspaceButton();

    String dashboardWindow = seleniumWebDriver.getWindowHandle();
    seleniumWebDriver.switchFromDashboardIframeToIde();
    loader.waitOnClosed();
    explorer.waitProjectExplorer();
    explorer.waitItem(CONSOLE_JAVA_SIMPLE);
    notificationsPopupPanel.waitPopUpPanelsIsClosed();
    mavenPluginStatusBar.waitClosingInfoPanel();
    explorer.waitFolderDefinedTypeOfFolderByPath(CONSOLE_JAVA_SIMPLE, PROJECT_FOLDER);
    explorer.waitFolderDefinedTypeOfFolderByPath(WEB_JAVA_SPRING, PROJECT_FOLDER);

    switchToWindow(dashboardWindow);
    dashboard.selectWorkspacesItemOnDashboard();
    workspaces.selectWorkspaceItemName(WORKSPACE);
    workspaceDetails.selectTabInWorkspaceMenu(PROJECTS);
    workspaceProjects.waitProjectIsPresent(WEB_JAVA_SPRING);
    workspaceProjects.waitProjectIsPresent(CONSOLE_JAVA_SIMPLE);
    workspaceProjects.openSettingsForProjectByName(WEB_JAVA_SPRING);
    workspaceProjects.clickOnDeleteProject();
    workspaceProjects.clickOnDeleteItInDialogWindow();
    workspaceProjects.waitProjectIsNotPresent(WEB_JAVA_SPRING);
    workspaceProjects.openSettingsForProjectByName(CONSOLE_JAVA_SIMPLE);
    workspaceProjects.clickOnDeleteProject();
    workspaceProjects.clickOnDeleteItInDialogWindow();
    workspaceProjects.waitProjectIsNotPresent(CONSOLE_JAVA_SIMPLE);
  }

  private void switchToWindow(String windowHandle) {
    seleniumWebDriver.switchTo().window(windowHandle);
  }
}
