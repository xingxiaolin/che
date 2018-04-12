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
package org.eclipse.che.selenium.factory;

import static org.eclipse.che.selenium.core.constant.TestMenuCommandsConstants.Workspace.CREATE_PROJECT;
import static org.eclipse.che.selenium.core.constant.TestMenuCommandsConstants.Workspace.WORKSPACE;
import static org.eclipse.che.selenium.core.constant.TestTimeoutsConstants.ELEMENT_TIMEOUT_SEC;
import static org.eclipse.che.selenium.pageobject.Wizard.SamplesName.WEB_JAVA_SPRING;
import static org.eclipse.che.selenium.pageobject.dashboard.DashboardFactories.AddAction.OPEN_FILE;

import com.google.inject.Inject;
import org.eclipse.che.commons.lang.NameGenerator;
import org.eclipse.che.selenium.core.SeleniumWebDriver;
import org.eclipse.che.selenium.core.client.TestFactoryServiceClient;
import org.eclipse.che.selenium.core.client.TestWorkspaceServiceClient;
import org.eclipse.che.selenium.core.user.TestUser;
import org.eclipse.che.selenium.core.webdriver.SeleniumWebDriverHelper;
import org.eclipse.che.selenium.core.workspace.TestWorkspace;
import org.eclipse.che.selenium.pageobject.CodenvyEditor;
import org.eclipse.che.selenium.pageobject.Ide;
import org.eclipse.che.selenium.pageobject.Loader;
import org.eclipse.che.selenium.pageobject.LoadingBehaviorPage;
import org.eclipse.che.selenium.pageobject.Menu;
import org.eclipse.che.selenium.pageobject.ProjectExplorer;
import org.eclipse.che.selenium.pageobject.Wizard;
import org.eclipse.che.selenium.pageobject.dashboard.Dashboard;
import org.eclipse.che.selenium.pageobject.dashboard.DashboardFactories;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/** @author Andrey Chizhikov */
public class CheckOpenFileFeatureTest {
  private static final String PROJECT_NAME = CheckOpenFileFeatureTest.class.getSimpleName();
  private static final String OPEN_FILE_URL = "/CheckOpenFileFeatureTest/pom.xml";
  private static final String FACTORY_NAME = NameGenerator.generate("factory", 4);

  @Inject private ProjectExplorer projectExplorer;
  @Inject private Dashboard dashboard;
  @Inject private DashboardFactories dashboardFactories;
  @Inject private Ide ide;
  @Inject private LoadingBehaviorPage loadingBehaviorPage;
  @Inject private CodenvyEditor editor;
  @Inject private Loader loader;
  @Inject private Wizard wizard;
  @Inject private Menu menu;
  @Inject private TestWorkspace testWorkspace;
  @Inject private TestUser user;
  @Inject private SeleniumWebDriver seleniumWebDriver;
  @Inject private SeleniumWebDriverHelper seleniumWebDriverHelper;
  @Inject private TestWorkspaceServiceClient workspaceServiceClient;
  @Inject private TestFactoryServiceClient factoryServiceClient;

  @BeforeClass
  public void setUp() throws Exception {
    ide.open(testWorkspace);
  }

  @AfterClass
  public void tearDown() throws Exception {
    workspaceServiceClient.deleteFactoryWorkspaces(testWorkspace.getName(), user.getName());
    factoryServiceClient.deleteFactory(FACTORY_NAME);
  }

  @Test
  public void checkOpenFileFeatureTest() throws Exception {
    createProject(PROJECT_NAME);
    projectExplorer.waitItem(PROJECT_NAME);
    dashboard.open();
    dashboard.selectFactoriesOnDashbord();
    dashboardFactories.clickOnAddFactoryBtn();
    dashboardFactories.selectWorkspaceForCreation(testWorkspace.getName());
    dashboardFactories.setFactoryName(FACTORY_NAME);
    dashboardFactories.clickOnCreateFactoryBtn();
    dashboardFactories.selectAction(OPEN_FILE);
    dashboardFactories.enterParamValue(OPEN_FILE_URL);
    dashboardFactories.clickAddOnAddAction();
    dashboardFactories.clickOnOpenFactory();
    String currentWin = seleniumWebDriver.getWindowHandle();
    seleniumWebDriverHelper.switchToNextWindow(currentWin);
    loadingBehaviorPage.waitWhileLoadPageIsClosed();
    seleniumWebDriverHelper.switchToIdeFrameAndWaitAvailability();
    projectExplorer.waitItem(PROJECT_NAME);
    editor.waitTabIsPresent("web-java-spring", ELEMENT_TIMEOUT_SEC);
  }

  private void createProject(String projectName) {
    projectExplorer.waitProjectExplorer();
    loader.waitOnClosed();
    menu.runCommand(WORKSPACE, CREATE_PROJECT);
    wizard.waitCreateProjectWizardForm();
    wizard.typeProjectNameOnWizard(projectName);
    wizard.selectSample(WEB_JAVA_SPRING);
    wizard.clickCreateButton();
    loader.waitOnClosed();
    wizard.waitCloseProjectConfigForm();
    loader.waitOnClosed();
    projectExplorer.waitProjectExplorer();
    projectExplorer.waitItem(projectName);
    loader.waitOnClosed();
  }
}
