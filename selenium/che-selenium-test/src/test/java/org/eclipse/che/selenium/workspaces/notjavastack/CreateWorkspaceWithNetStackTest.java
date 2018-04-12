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
package org.eclipse.che.selenium.workspaces.notjavastack;

import com.google.inject.Inject;
import org.eclipse.che.commons.lang.NameGenerator;
import org.eclipse.che.selenium.core.SeleniumWebDriver;
import org.eclipse.che.selenium.core.client.TestWorkspaceServiceClient;
import org.eclipse.che.selenium.core.constant.TestStacksConstants;
import org.eclipse.che.selenium.core.user.TestUser;
import org.eclipse.che.selenium.pageobject.Loader;
import org.eclipse.che.selenium.pageobject.ProjectExplorer;
import org.eclipse.che.selenium.pageobject.dashboard.Dashboard;
import org.eclipse.che.selenium.pageobject.dashboard.NavigationBar;
import org.eclipse.che.selenium.pageobject.dashboard.NewWorkspace;
import org.eclipse.che.selenium.pageobject.dashboard.workspaces.WorkspaceDetails;
import org.eclipse.che.selenium.pageobject.dashboard.workspaces.Workspaces;
import org.eclipse.che.selenium.pageobject.machineperspective.MachineTerminal;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/** @author Andrey Chizhikov */
public class CreateWorkspaceWithNetStackTest {
  private final String WORKSPACE = NameGenerator.generate("WsNet", 4);

  @Inject private TestUser defaultTestUser;
  @Inject private NavigationBar navigationBar;
  @Inject private NewWorkspace newWorkspace;
  @Inject private Dashboard dashboard;
  @Inject private WorkspaceDetails workspaceDetails;
  @Inject private ProjectExplorer projectExplorer;
  @Inject private Loader loader;
  @Inject private MachineTerminal terminal;
  @Inject private SeleniumWebDriver seleniumWebDriver;
  @Inject private TestWorkspaceServiceClient workspaceServiceClient;
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
  public void createWorkspaceWithNetStackTest() {
    dashboard.waitDashboardToolbarTitle();
    dashboard.selectWorkspacesItemOnDashboard();
    dashboard.waitToolbarTitleName("Workspaces");
    workspaces.clickOnAddWorkspaceBtn();

    newWorkspace.waitToolbar();
    newWorkspace.typeWorkspaceName(WORKSPACE);
    newWorkspace.selectStack(TestStacksConstants.DOTNET.getId());
    newWorkspace.setMachineRAM("dev-machine", 2.0);
    newWorkspace.clickOnCreateButtonAndOpenInIDE();

    dashboard.waitNotificationIsClosed();
    seleniumWebDriver.switchFromDashboardIframeToIde();

    projectExplorer.waitProjectExplorer();
    terminal.waitTerminalTab(60);
  }
}
