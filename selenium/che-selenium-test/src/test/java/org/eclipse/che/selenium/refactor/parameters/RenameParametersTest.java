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
package org.eclipse.che.selenium.refactor.parameters;

import static org.eclipse.che.selenium.pageobject.CodenvyEditor.MarkerLocator.ERROR;

import com.google.inject.Inject;
import java.net.URL;
import java.nio.file.Paths;
import org.eclipse.che.commons.lang.IoUtil;
import org.eclipse.che.commons.lang.NameGenerator;
import org.eclipse.che.selenium.core.client.TestProjectServiceClient;
import org.eclipse.che.selenium.core.project.ProjectTemplates;
import org.eclipse.che.selenium.core.workspace.TestWorkspace;
import org.eclipse.che.selenium.pageobject.AskDialog;
import org.eclipse.che.selenium.pageobject.CodenvyEditor;
import org.eclipse.che.selenium.pageobject.Consoles;
import org.eclipse.che.selenium.pageobject.Ide;
import org.eclipse.che.selenium.pageobject.Loader;
import org.eclipse.che.selenium.pageobject.ProjectExplorer;
import org.eclipse.che.selenium.pageobject.Refactor;
import org.openqa.selenium.Keys;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/** @author Aleksandr Shmaraev on 19.11.15 */
public class RenameParametersTest {
  private static final String PROJECT_NAME = NameGenerator.generate("ParametersProject-", 4);
  private static final String pathToPackageInChePrefix = PROJECT_NAME + "/src/main/java";

  private String pathToCurrentPackage;
  private String contentFromInA;
  private String contentFromOutA;

  @Inject private TestWorkspace workspace;
  @Inject private Ide ide;
  @Inject private ProjectExplorer projectExplorer;
  @Inject private Loader loader;
  @Inject private CodenvyEditor editor;
  @Inject private Refactor refactor;
  @Inject private AskDialog askDialog;
  @Inject private Consoles consoles;
  @Inject private TestProjectServiceClient testProjectServiceClient;

  @BeforeClass
  public void prepare() throws Exception {
    URL resource = RenameParametersTest.this.getClass().getResource("/projects/rename-parameters");
    testProjectServiceClient.importProject(
        workspace.getId(),
        Paths.get(resource.toURI()),
        PROJECT_NAME,
        ProjectTemplates.MAVEN_SIMPLE);
    ide.open(workspace);
    projectExplorer.waitItem(PROJECT_NAME);
    consoles.closeProcessesArea();
    projectExplorer.quickExpandWithJavaScript();
    loader.waitOnClosed();
  }

  @Test
  public void checkRenameParameters0() throws Exception {
    setFieldsForTest("test0");
    projectExplorer.openItemByPath(pathToCurrentPackage + "/A.java");
    editor.waitActive();
    editor.waitTextIntoEditor(contentFromInA);
    editor.goToCursorPositionVisible(14, 17);
    editor.launchRefactorForm();
    refactor.waitRenameParametersFormIsOpen();
    refactor.typeAndWaitNewName("j");
    refactor.clickOkButtonRefactorForm();
    refactor.waitRenameParametersFormIsClosed();
    editor.waitTextIntoEditor(contentFromOutA);
    editor.closeFileByNameWithSaving("A");
  }

  @Test(priority = 1)
  public void checkRenameParameters3() throws Exception {
    setFieldsForTest("test3");
    projectExplorer.openItemByPath(pathToCurrentPackage + "/A.java");
    editor.waitActive();
    editor.waitTextIntoEditor(contentFromInA);
    editor.goToCursorPositionVisible(14, 15);
    editor.launchLocalRefactor();
    editor.typeTextIntoEditor("j");
    editor.typeTextIntoEditor(Keys.ENTER.toString());
    loader.waitOnClosed();
    editor.goToCursorPositionVisible(14, 23);
    editor.launchLocalRefactor();
    editor.typeTextIntoEditor("j1");
    loader.waitOnClosed();
    editor.typeTextIntoEditor(Keys.ENTER.toString());
    editor.waitTextIntoEditor(contentFromOutA);
    editor.closeFileByNameWithSaving("A");
  }

  @Test(priority = 2)
  public void checkRenameParameters6() throws Exception {
    setFieldsForTest("test6");
    projectExplorer.openItemByPath(pathToCurrentPackage + "/A.java");
    editor.waitActive();
    editor.waitTextIntoEditor(contentFromInA);
    editor.goToCursorPositionVisible(14, 17);
    editor.launchLocalRefactor();
    editor.typeTextIntoEditor("k");
    editor.typeTextIntoEditor(Keys.ENTER.toString());
    editor.waitTextIntoEditor(contentFromOutA);
    editor.closeFileByNameWithSaving("A");
  }

  @Test(priority = 3)
  public void checkRenameParameters9() throws Exception {
    setFieldsForTest("test9");
    projectExplorer.openItemByPath(pathToCurrentPackage + "/A.java");
    editor.waitActive();
    editor.waitTextIntoEditor(contentFromInA);
    editor.goToCursorPositionVisible(14, 17);
    editor.launchLocalRefactor();
    editor.typeTextIntoEditor("j");
    editor.typeTextIntoEditor(Keys.ENTER.toString());
    editor.waitTextIntoEditor(contentFromOutA);
    editor.closeFileByNameWithSaving("A");
  }

  @Test(priority = 4)
  public void checkRenameParameters12() throws Exception {
    setFieldsForTest("test12");
    projectExplorer.openItemByPath(pathToCurrentPackage + "/A.java");
    editor.waitActive();
    editor.waitTextIntoEditor(contentFromInA);
    editor.goToCursorPositionVisible(14, 23);
    editor.launchLocalRefactor();
    editor.typeTextIntoEditor("j");
    editor.typeTextIntoEditor(Keys.ENTER.toString());
    editor.waitTextIntoEditor(contentFromOutA);
    editor.closeFileByNameWithSaving("A");
  }

  @Test(priority = 5)
  public void checkRenameParameters15() throws Exception {
    setFieldsForTest("test15");
    projectExplorer.openItemByPath(pathToCurrentPackage + "/A.java");
    editor.waitActive();
    editor.waitTextIntoEditor(contentFromInA);
    editor.goToCursorPositionVisible(14, 15);
    editor.launchRefactorForm();
    refactor.waitRenameParametersFormIsOpen();
    refactor.setAndWaitStateUpdateReferencesCheckbox(true);
    refactor.typeAndWaitNewName("j");
    refactor.clickOkButtonRefactorForm();
    askDialog.acceptDialogWithText("Duplicate parameter j");
    loader.waitOnClosed();
    refactor.waitRenameParametersFormIsClosed();
    editor.waitActive();
    editor.goToCursorPositionVisible(14, 23);
    editor.launchLocalRefactor();
    editor.typeTextIntoEditor("i");
    editor.typeTextIntoEditor(Keys.ENTER.toString());
    editor.waitMarkerInvisibility(ERROR, 14);
    editor.waitTextIntoEditor(contentFromOutA);
    editor.closeFileByNameWithSaving("A");
  }

  @Test(priority = 6)
  public void checkRenameParameters18() throws Exception {
    setFieldsForTest("test18");
    projectExplorer.openItemByPath(pathToCurrentPackage + "/A.java");
    editor.waitActive();
    editor.waitTextIntoEditor(contentFromInA);
    editor.goToCursorPositionVisible(14, 20);
    editor.launchLocalRefactor();
    editor.typeTextIntoEditor("j");
    editor.typeTextIntoEditor(Keys.ENTER.toString());
    editor.waitTextIntoEditor(contentFromOutA);
    editor.closeFileByNameWithSaving("A");
  }

  @Test(priority = 7)
  public void checkRenameParameters21() throws Exception {
    setFieldsForTest("test21");
    projectExplorer.openItemByPath(pathToCurrentPackage + "/A.java");
    editor.waitActive();
    editor.waitTextIntoEditor(contentFromInA);
    editor.goToCursorPositionVisible(14, 17);
    editor.launchLocalRefactor();
    editor.typeTextIntoEditor("j");
    editor.typeTextIntoEditor(Keys.ENTER.toString());
    editor.waitTextIntoEditor(contentFromOutA);
    editor.closeFileByNameWithSaving("A");
  }

  @Test(priority = 8)
  public void checkRenameParameters25() throws Exception {
    setFieldsForTest("test25");
    projectExplorer.openItemByPath(pathToCurrentPackage + "/A.java");
    editor.waitActive();
    editor.waitTextIntoEditor(contentFromInA);
    editor.goToCursorPositionVisible(15, 16);
    editor.launchLocalRefactor();
    editor.typeTextIntoEditor("j");
    editor.typeTextIntoEditor(Keys.ENTER.toString());
    editor.waitTextIntoEditor(contentFromOutA);
    editor.closeFileByNameWithSaving("A");
  }

  @Test(priority = 9)
  public void checkRenameParameters28() throws Exception {
    setFieldsForTest("test28");
    projectExplorer.openItemByPath(pathToCurrentPackage + "/A.java");
    editor.waitActive();
    editor.waitTextIntoEditor(contentFromInA);
    editor.goToCursorPositionVisible(14, 18);
    editor.typeTextIntoEditor(Keys.END.toString());
    editor.typeTextIntoEditor(Keys.ARROW_LEFT.toString());
    editor.typeTextIntoEditor(Keys.ARROW_LEFT.toString());
    editor.launchLocalRefactor();
    editor.typeTextIntoEditor("j");
    editor.typeTextIntoEditor(Keys.ENTER.toString());
    editor.waitTextIntoEditor(contentFromOutA);
    editor.closeFileByNameWithSaving("A");
  }

  @Test(priority = 10)
  public void checkRenameParameters31() throws Exception {
    setFieldsForTest("test31");
    projectExplorer.openItemByPath(pathToCurrentPackage + "/A.java");
    editor.waitActive();
    editor.waitTextIntoEditor(contentFromInA);
    editor.goToCursorPositionVisible(14, 30);
    editor.launchLocalRefactor();
    editor.typeTextIntoEditor("kk");
    editor.typeTextIntoEditor(Keys.ENTER.toString());
    editor.waitTextIntoEditor(contentFromOutA);
    editor.closeFileByNameWithSaving("A");
  }

  @Test(priority = 11)
  public void checkRenameParameters33() throws Exception {
    setFieldsForTest("test33");
    projectExplorer.openItemByPath(pathToCurrentPackage + "/A.java");
    editor.waitActive();
    editor.waitTextIntoEditor(contentFromInA);
    editor.goToCursorPositionVisible(15, 12);
    editor.launchRefactorForm();
    refactor.waitRenameParametersFormIsOpen();
    refactor.setAndWaitStateUpdateReferencesCheckbox(false);
    refactor.typeAndWaitNewName("b");
    refactor.clickOkButtonRefactorForm();
    refactor.waitRenameParametersFormIsClosed();
    editor.waitTextIntoEditor(contentFromOutA);
    editor.closeFileByNameWithSaving("A");
  }

  private void setFieldsForTest(String nameCurrentTest) throws Exception {
    pathToCurrentPackage = pathToPackageInChePrefix + "/" + nameCurrentTest;

    URL resourcesInA =
        getClass()
            .getResource(
                "/org/eclipse/che/selenium/refactor/parameters/" + nameCurrentTest + "/in/A.java");
    URL resourcesOutA =
        getClass()
            .getResource(
                "/org/eclipse/che/selenium/refactor/parameters/" + nameCurrentTest + "/out/A.java");

    contentFromInA = IoUtil.readAndCloseQuietly(resourcesInA.openStream());
    contentFromOutA = IoUtil.readAndCloseQuietly(resourcesOutA.openStream());
  }
}
