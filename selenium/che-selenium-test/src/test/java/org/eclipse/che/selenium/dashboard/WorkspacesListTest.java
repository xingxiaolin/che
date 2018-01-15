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

import static org.eclipse.che.selenium.core.project.ProjectTemplates.MAVEN_SPRING;
import static org.testng.Assert.assertTrue;

import com.google.inject.Inject;
import java.net.URL;
import java.nio.file.Paths;
import java.util.ArrayList;
import org.eclipse.che.commons.lang.NameGenerator;
import org.eclipse.che.selenium.core.SeleniumWebDriver;
import org.eclipse.che.selenium.core.client.TestProjectServiceClient;
import org.eclipse.che.selenium.core.client.TestWorkspaceServiceClient;
import org.eclipse.che.selenium.core.user.TestUser;
import org.eclipse.che.selenium.core.workspace.TestWorkspace;
import org.eclipse.che.selenium.pageobject.dashboard.Dashboard;
import org.eclipse.che.selenium.pageobject.dashboard.workspaces.WorkspaceConfig;
import org.eclipse.che.selenium.pageobject.dashboard.workspaces.WorkspaceDetails;
import org.eclipse.che.selenium.pageobject.dashboard.workspaces.WorkspaceProjects;
import org.eclipse.che.selenium.pageobject.dashboard.workspaces.Workspaces;
import org.eclipse.che.selenium.pageobject.dashboard.workspaces.Workspaces.Statuses;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class WorkspacesListTest {

  private static final String PROJECT_NAME = NameGenerator.generate("project", 4);

  private String workspaceName, workspaceName1, workspaceName2;

  @Inject private Dashboard dashboard;
  @Inject private WorkspaceDetails workspaceDetails;
  @Inject private WorkspaceProjects workspaceProjects;
  @Inject private WorkspaceConfig workspaceConfig;
  @Inject private SeleniumWebDriver seleniumWebDriver;
  @Inject private TestWorkspaceServiceClient workspaceServiceClient;
  @Inject private TestWorkspace testWorkspace;
  @Inject private TestWorkspace testWorkspace1;
  @Inject private TestWorkspace testWorkspace2;
  @Inject private TestProjectServiceClient testProjectServiceClient;
  @Inject private TestUser defaultTestUser;
  @Inject private Workspaces workspaces;

  @BeforeClass
  public void setUp() throws Exception {
    URL resource = getClass().getResource("/projects/guess-project");
    testProjectServiceClient.importProject(
        testWorkspace1.getId(), Paths.get(resource.toURI()), PROJECT_NAME, MAVEN_SPRING);

    this.workspaceName = testWorkspace.getName();
    this.workspaceName1 = testWorkspace1.getName();
    this.workspaceName2 = testWorkspace2.getName();

    dashboard.open();
  }

  @AfterClass
  public void tearDown() throws Exception {
    workspaceServiceClient.delete(testWorkspace.getName(), defaultTestUser.getName());
    workspaceServiceClient.delete(testWorkspace1.getName(), defaultTestUser.getName());
    workspaceServiceClient.delete(testWorkspace2.getName(), defaultTestUser.getName());
  }

  @BeforeMethod
  public void openWorkspacesPage() {
    dashboard.waitDashboardToolbarTitle();
    dashboard.selectWorkspacesItemOnDashboard();
  }

  @Test
  public void checkWorkspacesList() {
    // check UI views of workspaces list
    workspaces.waitToolbarTitleName();
    workspaces.waitDocumentationLink();
    workspaces.waitAddWorkspaceButton();
    workspaces.waitSearchWorkspaceByNameField();
    workspaces.waitBulkCheckbox();

    // check all headers are present
    ArrayList<String> headers = workspaces.getWorkspaceListHeaders();
    assertTrue(headers.contains("NAME"));
    assertTrue(headers.contains("RAM"));
    assertTrue(headers.contains("PROJECTS"));
    assertTrue(headers.contains("STACK"));
    assertTrue(headers.contains("ACTIONS"));

    // check workspaces info
    Assert.assertEquals(workspaces.getWorkspaceRamValue(workspaceName), "2048 MB");
    Assert.assertEquals(workspaces.getWorkspaceProjectsValue(workspaceName), "0");
    Assert.assertEquals(workspaces.getWorkspaceProjectsValue(workspaceName1), "1");
  }

  @Test
  public void checkWorkspaceSelectingByCheckbox() {
    // select the test workspace by checkbox and select it is checked
    workspaces.selectWorkspaceByCheckbox(workspaceName);
    assertTrue(workspaces.isWorkspaceChecked(workspaceName));
    workspaces.selectWorkspaceByCheckbox(workspaceName);
    Assert.assertFalse(workspaces.isWorkspaceChecked(workspaceName));

    // click on the Bulk button and check that all workspaces are checked
    workspaces.selectAllWorkspacesByBulk();
    assertTrue(workspaces.isWorkspaceChecked(workspaceName));
    assertTrue(workspaces.isWorkspaceChecked(workspaceName1));
    assertTrue(workspaces.isWorkspaceChecked(workspaceName2));

    workspaces.selectAllWorkspacesByBulk();
    Assert.assertFalse(workspaces.isWorkspaceChecked(workspaceName));
  }

  @Test
  public void checkWorkspaceActions() {
    // open the Config page of the test workspace
    dashboard.selectWorkspacesItemOnDashboard();
    workspaces.clickOnWorkspaceConfigureButton(workspaceName);
    workspaceDetails.waitToolbarTitleName(workspaceName);
    workspaceConfig.waitConfigForm();
    seleniumWebDriver.navigate().back();
    workspaces.waitWorkspaceIsPresent(workspaceName);

    // open the Projects page of the test workspace
    dashboard.selectWorkspacesItemOnDashboard();
    workspaces.clickOnWorkspaceAddProjectButton(workspaceName1);
    workspaceDetails.waitToolbarTitleName(workspaceName1);
    workspaceProjects.waitProjectIsPresent(PROJECT_NAME);
    seleniumWebDriver.navigate().back();
    workspaces.waitWorkspaceIsPresent(workspaceName);

    // check statuses of the created workspaces
    workspaces.waitWorkspaceStatusIs(workspaceName, Statuses.RUNNING);
    workspaces.waitWorkspaceStatusIs(workspaceName1, Statuses.RUNNING);
    workspaces.waitWorkspaceStatusIs(workspaceName2, Statuses.RUNNING);

    // stop the workspace by the Actions button and check its status is STOPPED
    dashboard.selectWorkspacesItemOnDashboard();
    Assert.assertEquals(workspaces.getWorkspaceStatus(workspaceName2), Statuses.RUNNING);
    workspaces.clickOnWorkspaceActionsButton(workspaceName2);
    workspaces.waitWorkspaceStatusIs(workspaceName2, Statuses.STOPPED);
  }

  @Test
  public void checkWorkspaceFiltering() {
    workspaces.waitSearchWorkspaceByNameField();

    workspaces.typeToSearchInput("*");
    workspaces.waitNoWorkspacesFound();

    workspaces.typeToSearchInput("works");
    workspaces.waitWorkspaceIsPresent(workspaceName);
    workspaces.waitWorkspaceIsPresent(workspaceName1);
    workspaces.waitWorkspaceIsPresent(workspaceName2);

    // search a workspace by full name
    workspaces.typeToSearchInput(workspaceName);
    workspaces.waitWorkspaceIsPresent(workspaceName);

    // search a workspace by part name
    workspaces.typeToSearchInput(workspaceName2.substring(workspaceName2.length() / 2));
    workspaces.waitWorkspaceIsPresent(workspaceName2);
  }

  @Test(priority = 1)
  public void checkWorkspaceDeleting() {
    // delete all created test workspaces
    workspaces.selectWorkspaceByCheckbox(workspaceName);
    workspaces.selectWorkspaceByCheckbox(workspaceName1);
    workspaces.selectWorkspaceByCheckbox(workspaceName2);

    workspaces.clickOnDeleteWorkspacesBtn();
    workspaces.clickOnDeleteButtonInDialogWindow();

    workspaces.waitWorkspaceIsNotPresent(workspaceName);
    workspaces.waitWorkspaceIsNotPresent(workspaceName1);
    workspaces.waitWorkspaceIsNotPresent(workspaceName2);
  }
}
