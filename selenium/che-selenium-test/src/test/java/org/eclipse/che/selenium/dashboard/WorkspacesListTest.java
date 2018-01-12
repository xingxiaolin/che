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

import static org.testng.Assert.assertTrue;

import com.google.inject.Inject;
import java.util.ArrayList;
import org.eclipse.che.commons.lang.NameGenerator;
import org.eclipse.che.selenium.core.SeleniumWebDriver;
import org.eclipse.che.selenium.core.client.TestWorkspaceServiceClient;
import org.eclipse.che.selenium.core.user.TestUser;
import org.eclipse.che.selenium.core.workspace.TestWorkspace;
import org.eclipse.che.selenium.pageobject.dashboard.Dashboard;
import org.eclipse.che.selenium.pageobject.dashboard.workspaces.WorkspaceDetails;
import org.eclipse.che.selenium.pageobject.dashboard.workspaces.Workspaces;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class WorkspacesListTest {

  private final String WORKSPACE = NameGenerator.generate("workspace", 4);

  private String workspaceName;

  @Inject private Dashboard dashboard;
  @Inject private WorkspaceDetails workspaceDetails;
  @Inject private SeleniumWebDriver seleniumWebDriver;
  @Inject private TestWorkspaceServiceClient workspaceServiceClient;
  @Inject private TestWorkspace testWorkspace;
  @Inject private TestUser defaultTestUser;
  @Inject private Workspaces workspaces;

  @BeforeClass
  public void setUp() throws Exception {
    this.workspaceName = testWorkspace.getName();
    dashboard.open();
  }

  @AfterClass
  public void tearDown() throws Exception {
    workspaceServiceClient.delete(WORKSPACE, defaultTestUser.getName());
  }

  @BeforeMethod
  public void openWorkspacesPage() {
    dashboard.waitDashboardToolbarTitle();
    dashboard.selectWorkspacesItemOnDashboard();
  }

  @Test
  public void checkWorkspacesList() {
    workspaces.waitToolbarTitleName();
    workspaces.waitDocumentationLink(); // TODO add checking the Documentation page
    workspaces.waitAddWorkspaceButton();
    workspaces.waitSearchWorkspaceByNameField();
    workspaces.waitBulkCheckbox();

    // check a workspace list headers names
    ArrayList<String> headers = workspaces.getWorkspaceListHeaders();
    assertTrue(headers.contains("NAME"));
    assertTrue(headers.contains("RAM"));
    assertTrue(headers.contains("PROJECTS"));
    assertTrue(headers.contains("STACK"));
    assertTrue(headers.contains("ACTIONS"));
  }

  @Test
  public void checkWorkspacesInfo() {

    Assert.assertEquals(workspaces.getWorkspaceRamValue(workspaceName), "2048 MB");
    Assert.assertEquals(workspaces.getWorkspaceProjectsValue(workspaceName), "0");
    // System.out.println(workspaces.getWorkspaceStackName(workspaceName));
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
    workspaces.selectAllWorkspacesByBulk();
    Assert.assertFalse(workspaces.isWorkspaceChecked(workspaceName));
  }

  @Test
  public void checkWorkspaceActions() {
    // open the Config page of the test workspace
    dashboard.selectWorkspacesItemOnDashboard();
    workspaces.clickOnWorkspaceConfigureButton(workspaceName);
    workspaceDetails.waitToolbarTitleName(workspaceName);
    // TODO add checking that it the Config tab is opened

    // open the Projects page of the test workspace
    dashboard.selectWorkspacesItemOnDashboard();
    workspaces.clickOnWorkspaceAddProjectButton(workspaceName);
    workspaceDetails.waitToolbarTitleName(workspaceName);
    // TODO add checking that it the Projects tab is opened
  }

  @Test
  public void checkWorkspaceFiltering() {
    // TODO filter workspaces by the Search feature
    // by full name
    // by part name
    workspaces.waitSearchWorkspaceByNameField();
    workspaces.typeToSearchInput(" ");
    workspaces.waitWorkspaceIsPresent(workspaceName);
  }

  @Test(priority = 1)
  public void checkWorkspaceDeleting() {
    // delete the test workspace
    workspaces.selectWorkspaceByCheckbox(workspaceName);
    workspaces.clickOnDeleteWorkspacesBtn();
    workspaces.clickOnDeleteButtonInDialogWindow();
    workspaces.waitWorkspaceIsNotPresent(workspaceName);

    // select all workspaces by the Bulk and delete them
    workspaces.selectAllWorkspacesByBulk();
    workspaces.clickOnDeleteWorkspacesBtn();
    workspaces.clickOnDeleteButtonInDialogWindow();
    // TODO check that all workspaces are not exit in the Workspaces list
  }
}
