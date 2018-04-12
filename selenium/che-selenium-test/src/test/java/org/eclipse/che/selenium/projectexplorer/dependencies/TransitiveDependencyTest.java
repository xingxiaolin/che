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
package org.eclipse.che.selenium.projectexplorer.dependencies;

import static org.eclipse.che.selenium.core.constant.TestProjectExplorerContextMenuConstants.ContextMenuFirstLevelItems.MAVEN;
import static org.eclipse.che.selenium.core.constant.TestProjectExplorerContextMenuConstants.ContextMenuFirstLevelItems.REIMPORT;
import static org.openqa.selenium.Keys.DELETE;
import static org.openqa.selenium.Keys.DOWN;
import static org.openqa.selenium.Keys.SHIFT;

import com.google.inject.Inject;
import java.net.URL;
import java.nio.file.Paths;
import java.util.Random;
import org.eclipse.che.selenium.core.client.TestProjectServiceClient;
import org.eclipse.che.selenium.core.project.ProjectTemplates;
import org.eclipse.che.selenium.core.webdriver.SeleniumWebDriverHelper;
import org.eclipse.che.selenium.core.workspace.TestWorkspace;
import org.eclipse.che.selenium.pageobject.CodenvyEditor;
import org.eclipse.che.selenium.pageobject.Ide;
import org.eclipse.che.selenium.pageobject.Loader;
import org.eclipse.che.selenium.pageobject.PopupDialogsBrowser;
import org.eclipse.che.selenium.pageobject.ProjectExplorer;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * @author Andrienko Alexander
 * @author Andrey Chizhikov
 */
public class TransitiveDependencyTest {

  private static final String PROJECT_NAME =
      TransitiveDependencyTest.class.getSimpleName() + new Random().nextInt(10);
  private static final String LIB_FOLDER = "External Libraries";
  private static final String MAIN_LIBRARY = "spring-webmvc-3.0.5.RELEASE.jar";
  private static final String TRANSITIVE_DEPENDENCY_FOR_MAIN_LIBRARY =
      "spring-core-3.0.5.RELEASE.jar";

  @Inject private TestWorkspace testWorkspace;
  @Inject private Ide ide;
  @Inject private ProjectExplorer projectExplorer;
  @Inject private CodenvyEditor editor;
  @Inject private Loader loader;
  @Inject private PopupDialogsBrowser popupDialogsBrowser;
  @Inject private TestProjectServiceClient testProjectServiceClient;
  @Inject private SeleniumWebDriverHelper seleniumWebDriverHelper;

  @BeforeClass
  public void setUp() throws Exception {
    URL resource = getClass().getResource("/projects/default-spring-project");
    testProjectServiceClient.importProject(
        testWorkspace.getId(),
        Paths.get(resource.toURI()),
        PROJECT_NAME,
        ProjectTemplates.MAVEN_SPRING);
    ide.open(testWorkspace);
  }

  @Test
  public void transitiveDependencyTest() throws Exception {
    projectExplorer.waitItem(PROJECT_NAME);
    projectExplorer.expandPathInProjectExplorer(
        PROJECT_NAME + "/src/main/java/org.eclipse.qa.examples");
    projectExplorer.openContextMenuByPathSelectedItem(PROJECT_NAME);
    projectExplorer.clickOnItemInContextMenu(MAVEN);
    projectExplorer.clickOnNewContextMenuItem(REIMPORT);
    loader.waitOnClosed();
    projectExplorer.waitItem(PROJECT_NAME + "/pom.xml");
    projectExplorer.openItemByPath(PROJECT_NAME + "/pom.xml");
    editor.waitActive();

    projectExplorer.openItemByVisibleNameInExplorer(LIB_FOLDER);
    projectExplorer.waitLibraryIsPresent(MAIN_LIBRARY);
    projectExplorer.waitLibraryIsPresent(TRANSITIVE_DEPENDENCY_FOR_MAIN_LIBRARY);
    projectExplorer.openItemByPath(PROJECT_NAME + "/pom.xml");
    loader.waitOnClosed();
    editor.waitActive();

    deleteDependency();

    popupDialogsBrowser.waitAlertClose();
    projectExplorer.waitLibraryIsNotPresent(MAIN_LIBRARY);
    projectExplorer.waitLibraryIsNotPresent(TRANSITIVE_DEPENDENCY_FOR_MAIN_LIBRARY);
  }

  private void deleteDependency() {
    editor.waitActive();
    editor.setCursorToLine(36);
    seleniumWebDriverHelper
        .getAction()
        .keyDown(SHIFT)
        .sendKeys(DOWN, DOWN, DOWN, DOWN, DOWN)
        .keyUp(SHIFT)
        .sendKeys(DELETE)
        .perform();
  }
}
