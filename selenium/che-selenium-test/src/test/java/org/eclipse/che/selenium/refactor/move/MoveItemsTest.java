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
package org.eclipse.che.selenium.refactor.move;

import com.google.inject.Inject;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import org.eclipse.che.commons.lang.NameGenerator;
import org.eclipse.che.selenium.core.client.TestProjectServiceClient;
import org.eclipse.che.selenium.core.constant.TestMenuCommandsConstants;
import org.eclipse.che.selenium.core.project.ProjectTemplates;
import org.eclipse.che.selenium.core.workspace.TestWorkspace;
import org.eclipse.che.selenium.pageobject.CodenvyEditor;
import org.eclipse.che.selenium.pageobject.Ide;
import org.eclipse.che.selenium.pageobject.Loader;
import org.eclipse.che.selenium.pageobject.Menu;
import org.eclipse.che.selenium.pageobject.ProjectExplorer;
import org.eclipse.che.selenium.pageobject.Refactor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/** @author Aleksandr Shmaraev on 18.12.15 */
public class MoveItemsTest {
  private static final Logger LOG = LoggerFactory.getLogger(MoveItemsTest.class);
  private static final String PROJECT_NAME = NameGenerator.generate("MoveItemsProject-", 4);
  private static final String pathToPackageInChePrefix = PROJECT_NAME + "/src/main/java";

  private String contentFromInA, contentFromInB;
  private String contentFromOutA, contentFromOutB;

  @Inject private TestWorkspace workspace;
  @Inject private Ide ide;
  @Inject private ProjectExplorer projectExplorer;
  @Inject private Loader loader;
  @Inject private CodenvyEditor editor;
  @Inject private Refactor refactor;
  @Inject private Menu menu;
  @Inject private TestProjectServiceClient testProjectServiceClient;

  @BeforeClass
  public void prepare() throws Exception {
    URL resource = getClass().getResource("/projects/move-items-project");
    testProjectServiceClient.importProject(
        workspace.getId(),
        Paths.get(resource.toURI()),
        PROJECT_NAME,
        ProjectTemplates.MAVEN_SPRING);

    ide.open(workspace);
    projectExplorer.waitItem(PROJECT_NAME);
    projectExplorer.quickExpandWithJavaScript();
    loader.waitOnClosed();
  }

  @AfterMethod
  public void closeForm() {
    try {
      if (refactor.isWidgetOpened()) {
        refactor.clickCancelButtonRefactorForm();
        editor.closeAllTabs();
      }
    } catch (Exception e) {
      LOG.error(e.getLocalizedMessage(), e);
    }
  }

  @Test
  public void checkMoveItem0() throws Exception {
    setFieldsForTest("test0");
    projectExplorer.openItemByPath(pathToPackageInChePrefix + "/r/A0.java");
    editor.waitActive();
    editor.waitTextIntoEditor(contentFromInA);
    projectExplorer.waitAndSelectItemByName("A0.java");
    menu.runCommand(
        TestMenuCommandsConstants.Assistant.ASSISTANT,
        TestMenuCommandsConstants.Assistant.Refactoring.REFACTORING,
        TestMenuCommandsConstants.Assistant.Refactoring.MOVE);

    refactor.waitMoveItemFormIsOpen();
    refactor.clickCancelButtonRefactorForm();
    refactor.waitRenameFieldFormIsClosed();
    menu.runCommand(
        TestMenuCommandsConstants.Assistant.ASSISTANT,
        TestMenuCommandsConstants.Assistant.Refactoring.REFACTORING,
        TestMenuCommandsConstants.Assistant.Refactoring.MOVE);

    refactor.waitMoveItemFormIsOpen();
    refactor.clickOnExpandIconTree(PROJECT_NAME);
    refactor.clickOnExpandIconTree("/src/main/java");
    refactor.chooseDestinationForItem("p1");
    refactor.clickOkButtonRefactorForm();
    refactor.waitMoveItemFormIsClosed();
    projectExplorer.openItemByPath(pathToPackageInChePrefix + "/p1/A0.java");
    editor.waitTextIntoEditor(contentFromOutA);
    projectExplorer.waitDisappearItemByPath(pathToPackageInChePrefix + "/r/A0.java");
    editor.closeFileByNameWithSaving("A0");
  }

  @Test(priority = 1)
  public void checkMoveItem1() throws Exception {
    setFieldsForTest("test1");
    projectExplorer.openItemByPath(pathToPackageInChePrefix + "/r/A1.java");
    editor.waitActive();
    editor.waitTextIntoEditor(contentFromInA);
    projectExplorer.waitAndSelectItem(pathToPackageInChePrefix + "/r/A1.java");
    projectExplorer.launchRefactorMoveByKeyboard();
    refactor.waitMoveItemFormIsOpen();
    refactor.clickOnExpandIconTree(PROJECT_NAME);
    refactor.clickOnExpandIconTree("/src/main/java");
    refactor.chooseDestinationForItem("p1");
    refactor.clickOkButtonRefactorForm();
    refactor.waitMoveItemFormIsClosed();
    projectExplorer.openItemByPath(pathToPackageInChePrefix + "/p1/A1.java");
    editor.waitTextIntoEditor(contentFromOutA);
    projectExplorer.waitDisappearItemByPath(pathToPackageInChePrefix + "/r/A1.java");
    editor.closeFileByNameWithSaving("A1");
  }

  @Test(priority = 2)
  public void checkMoveItem2() throws Exception {
    setFieldsForTest("test2");
    setFieldsForTestB("test2");
    projectExplorer.openItemByPath(pathToPackageInChePrefix + "/r/A2.java");
    editor.waitActive();
    editor.waitTextIntoEditor(contentFromInA);
    projectExplorer.openItemByPath(pathToPackageInChePrefix + "/r/fred2/B2.java");
    editor.waitActive();
    editor.waitTextIntoEditor(contentFromInB);
    projectExplorer.waitAndSelectItem(pathToPackageInChePrefix + "/r/A2.java");
    editor.closeFileByNameWithSaving("B2");
    editor.closeFileByNameWithSaving("A2");
    menu.runCommand(
        TestMenuCommandsConstants.Assistant.ASSISTANT,
        TestMenuCommandsConstants.Assistant.Refactoring.REFACTORING,
        TestMenuCommandsConstants.Assistant.Refactoring.MOVE);

    refactor.waitMoveItemFormIsOpen();
    refactor.clickOnExpandIconTree(PROJECT_NAME);
    refactor.clickOnExpandIconTree("/src/main/java");
    refactor.chooseDestinationForItem("p1");
    refactor.clickOkButtonRefactorForm();
    loader.waitOnClosed();
    refactor.waitMoveItemFormIsClosed();
    loader.waitOnClosed();
    projectExplorer.openItemByPath(pathToPackageInChePrefix + "/p1/A2.java");
    editor.waitTextIntoEditor(contentFromOutA);
    projectExplorer.openItemByPath(pathToPackageInChePrefix + "/r/fred2/B2.java");
    editor.waitTextIntoEditor(contentFromOutB);
    projectExplorer.waitDisappearItemByPath(pathToPackageInChePrefix + "/r/A2.java");
    editor.closeFileByNameWithSaving("A2");
    editor.closeFileByNameWithSaving("B2");
  }

  @Test(priority = 3)
  public void checkMoveItem3() throws Exception {
    setFieldsForTest("test3");
    setFieldsForTestB("test3");
    projectExplorer.openItemByPath(pathToPackageInChePrefix + "/r/r/A3.java");
    editor.waitActive();
    editor.waitTextIntoEditor(contentFromInA);
    projectExplorer.openItemByPath(pathToPackageInChePrefix + "/r/fred3/B3.java");
    editor.waitActive();
    editor.waitTextIntoEditor(contentFromInB);
    projectExplorer.waitAndSelectItem(pathToPackageInChePrefix + "/r/r/A3.java");
    projectExplorer.launchRefactorMoveByKeyboard();
    refactor.waitMoveItemFormIsOpen();
    refactor.clickOnExpandIconTree(PROJECT_NAME);
    refactor.clickOnExpandIconTree("/src/main/java");
    refactor.chooseDestinationForItem("p1");
    refactor.clickOkButtonRefactorForm();
    loader.waitOnClosed();
    refactor.waitMoveItemFormIsClosed();
    loader.waitOnClosed();
    editor.waitTextIntoEditor(contentFromOutB);
    projectExplorer.openItemByPath(pathToPackageInChePrefix + "/p1/A3.java");
    editor.waitTextIntoEditor(contentFromOutA);
    projectExplorer.waitDisappearItemByPath(pathToPackageInChePrefix + "/r/r/A3.java");
    editor.closeFileByNameWithSaving("A3");
    editor.closeFileByNameWithSaving("B3");
  }

  @Test(priority = 4)
  public void checkMoveItem5() throws Exception {
    setFieldsForTest("test5");
    projectExplorer.openItemByPath(pathToPackageInChePrefix + "/r/A5.java");
    editor.waitActive();
    editor.waitTextIntoEditor(contentFromInA);
    projectExplorer.waitAndSelectItem(pathToPackageInChePrefix + "/r/A5.java");
    projectExplorer.launchRefactorMoveByKeyboard();
    refactor.waitMoveItemFormIsOpen();
    refactor.clickOnExpandIconTree(PROJECT_NAME);
    refactor.clickOnExpandIconTree("/src/main/java");
    refactor.chooseDestinationForItem("r");
    refactor.waitTextInErrorMessage("A file or folder cannot be moved to its own parent.");
    refactor.chooseDestinationForItem("r.r");
    refactor.clickOkButtonRefactorForm();
    refactor.waitMoveItemFormIsClosed();
    projectExplorer.openItemByPath(pathToPackageInChePrefix + "/r/r/A5.java");
    editor.waitTextIntoEditor(contentFromOutA);
    projectExplorer.waitDisappearItemByPath(pathToPackageInChePrefix + "/r/A5.java");
    editor.closeFileByNameWithSaving("A5");
  }

  @Test(priority = 5)
  public void checkMoveItem6() throws Exception {
    setFieldsForTest("test6");
    projectExplorer.openItemByPath(pathToPackageInChePrefix + "/r/A6.java");
    editor.waitActive();
    editor.waitTextIntoEditor(contentFromInA);
    projectExplorer.waitAndSelectItem(pathToPackageInChePrefix + "/r/A6.java");
    projectExplorer.launchRefactorMoveByKeyboard();
    refactor.waitMoveItemFormIsOpen();
    refactor.clickOnExpandIconTree(PROJECT_NAME);
    refactor.clickOnExpandIconTree("/src/main/java");
    refactor.chooseDestinationForItem("p1");
    refactor.clickOkButtonRefactorForm();
    refactor.waitMoveItemFormIsClosed();
    projectExplorer.openItemByPath(pathToPackageInChePrefix + "/p1/A6.java");
    editor.waitTextIntoEditor(contentFromOutA);
    projectExplorer.waitDisappearItemByPath(pathToPackageInChePrefix + "/r/A6.java");
    editor.closeFileByNameWithSaving("A6");
  }

  @Test(priority = 6)
  public void checkMoveItem7() throws Exception {
    setFieldsForTest("test7");
    projectExplorer.openItemByPath(pathToPackageInChePrefix + "/r/A7.java");
    editor.waitActive();
    editor.waitTextIntoEditor(contentFromInA);
    projectExplorer.waitAndSelectItem(pathToPackageInChePrefix + "/r/A7.java");
    menu.runCommand(
        TestMenuCommandsConstants.Assistant.ASSISTANT,
        TestMenuCommandsConstants.Assistant.Refactoring.REFACTORING,
        TestMenuCommandsConstants.Assistant.Refactoring.MOVE);

    refactor.waitMoveItemFormIsOpen();
    refactor.clickOnExpandIconTree(PROJECT_NAME);
    refactor.clickOnExpandIconTree("/src/main/java");
    refactor.chooseDestinationForItem("(default package)");
    refactor.waitTextInMoveForm("Java references will not be updated.");
    refactor.clickOkButtonRefactorForm();
    refactor.waitMoveItemFormIsClosed();
    projectExplorer.openItemByPath(pathToPackageInChePrefix + "/A7.java");
    editor.waitTextIntoEditor(contentFromOutA);
    projectExplorer.waitDisappearItemByPath(pathToPackageInChePrefix + "/r/A7.java");
    editor.closeFileByNameWithSaving("A7");
  }

  @Test(priority = 7)
  public void checkMoveItem8() throws Exception {
    setFieldsForTest("test8");
    projectExplorer.openItemByPath(pathToPackageInChePrefix + "/java/lang/reflect/Klass.java");
    editor.waitActive();
    editor.waitTextIntoEditor(contentFromInA);
    projectExplorer.waitAndSelectItem(pathToPackageInChePrefix + "/java/lang/reflect/Klass.java");
    projectExplorer.launchRefactorMoveByKeyboard();
    refactor.waitMoveItemFormIsOpen();
    refactor.clickOnExpandIconTree(PROJECT_NAME);
    refactor.clickOnExpandIconTree("/src/main/java");
    refactor.chooseDestinationForItem("p1");
    refactor.clickOkButtonRefactorForm();
    refactor.waitMoveItemFormIsClosed();
    projectExplorer.openItemByPath(pathToPackageInChePrefix + "/p1/Klass.java");
    editor.waitTextIntoEditor(contentFromOutA);
    projectExplorer.waitDisappearItemByPath(
        pathToPackageInChePrefix + "/java/lang/reflect/Klass.java");
    editor.closeFileByNameWithSaving("Klass");
  }

  @Test(priority = 8)
  public void checkMoveItem9() throws Exception {
    setFieldsForTest("test9");
    projectExplorer.openItemByPath(pathToPackageInChePrefix + "/r/A9.java");
    editor.waitActive();
    editor.waitTextIntoEditor(contentFromInA);
    projectExplorer.waitAndSelectItem(pathToPackageInChePrefix + "/r/A9.java");
    projectExplorer.launchRefactorMoveByKeyboard();
    refactor.waitMoveItemFormIsOpen();
    refactor.clickOnExpandIconTree(PROJECT_NAME);
    refactor.clickOnExpandIconTree("/src/main/java");
    refactor.chooseDestinationForItem("p1");
    refactor.clickOkButtonRefactorForm();
    refactor.waitMoveItemFormIsClosed();
    projectExplorer.openItemByPath(pathToPackageInChePrefix + "/p1/A9.java");
    editor.waitTextIntoEditor(contentFromOutA);
    projectExplorer.waitDisappearItemByPath(pathToPackageInChePrefix + "/r/A9.java");
    editor.closeFileByNameWithSaving("A9");
  }

  private void setFieldsForTest(String nameCurrentTest) throws Exception {
    URL resourcesIn = getClass().getResource(nameCurrentTest + "/in/A.java");
    URL resourcesOut = getClass().getResource(nameCurrentTest + "/out/A.java");

    contentFromInA = getTextFromFile(resourcesIn);
    contentFromOutA = getTextFromFile(resourcesOut);
  }

  private void setFieldsForTestB(String nameCurrentTest) throws Exception {
    URL resourcesIn = getClass().getResource(nameCurrentTest + "/in/B.java");
    URL resourcesOut = getClass().getResource(nameCurrentTest + "/out/B.java");

    contentFromInB = getTextFromFile(resourcesIn);
    contentFromOutB = getTextFromFile(resourcesOut);
  }

  private String getTextFromFile(URL url) throws Exception {
    String result = "";
    List<String> listWithAllLines =
        Files.readAllLines(Paths.get(url.toURI()), Charset.forName("UTF-8"));
    for (String buffer : listWithAllLines) {
      result += buffer + '\n';
    }

    return result;
  }
}
