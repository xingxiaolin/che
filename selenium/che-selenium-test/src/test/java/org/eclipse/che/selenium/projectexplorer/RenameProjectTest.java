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
package org.eclipse.che.selenium.projectexplorer;

import static org.eclipse.che.commons.lang.NameGenerator.generate;
import static org.eclipse.che.selenium.core.constant.TestProjectExplorerContextMenuConstants.ContextMenuFirstLevelItems.RENAME;
import static org.eclipse.che.selenium.pageobject.ProjectExplorer.FolderTypes.PROJECT_FOLDER;

import com.google.inject.Inject;
import java.net.URL;
import java.nio.file.Paths;
import org.eclipse.che.selenium.core.client.TestProjectServiceClient;
import org.eclipse.che.selenium.core.constant.TestMenuCommandsConstants.Edit;
import org.eclipse.che.selenium.core.project.ProjectTemplates;
import org.eclipse.che.selenium.core.workspace.TestWorkspace;
import org.eclipse.che.selenium.pageobject.AskForValueDialog;
import org.eclipse.che.selenium.pageobject.Ide;
import org.eclipse.che.selenium.pageobject.Menu;
import org.eclipse.che.selenium.pageobject.ProjectExplorer;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/** @author Andrey Chizhikov */
public class RenameProjectTest {
  private static final String PROJECT_NAME = generate("project", 5);
  private static final String NEW_PROJECT_NAME = generate("new-project", 5);

  @Inject private TestProjectServiceClient testProjectServiceClient;
  @Inject private AskForValueDialog askForValueDialog;
  @Inject private ProjectExplorer projectExplorer;
  @Inject private TestWorkspace testWorkspace;
  @Inject private Menu menu;
  @Inject private Ide ide;

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
  public void renameProjectTest() {
    projectExplorer.waitProjectExplorer();
    projectExplorer.waitItem(PROJECT_NAME);

    // Rename project from context menu
    projectExplorer.waitAndSelectItem(PROJECT_NAME);
    projectExplorer.openContextMenuByPathSelectedItem(PROJECT_NAME);
    projectExplorer.clickOnItemInContextMenu(RENAME);
    askForValueDialog.waitFormToOpen();
    askForValueDialog.clearInput();
    askForValueDialog.typeAndWaitText(NEW_PROJECT_NAME);
    askForValueDialog.clickOkBtn();
    askForValueDialog.waitFormToClose();

    // Wait that project renamed and folder has project type
    projectExplorer.waitItem(NEW_PROJECT_NAME);
    projectExplorer.waitItemInvisibility(PROJECT_NAME);
    projectExplorer.waitDefinedTypeOfFolder(NEW_PROJECT_NAME, PROJECT_FOLDER);

    // Test that the Rename project dialog is started from menu
    projectExplorer.waitAndSelectItem(NEW_PROJECT_NAME);
    menu.runCommand(Edit.EDIT, Edit.RENAME);
    askForValueDialog.waitFormToOpen();
    askForValueDialog.clickCancelBtn();
    askForValueDialog.waitFormToClose();

    // Test that the Rename project dialog is started by SHIFT + F6 keys
    projectExplorer.waitAndSelectItem(NEW_PROJECT_NAME);
    askForValueDialog.launchFindFormByKeyboard();
    askForValueDialog.waitFormToOpen();
    askForValueDialog.clickCancelBtn();
    askForValueDialog.waitFormToClose();
  }
}
