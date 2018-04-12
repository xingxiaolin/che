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
package org.eclipse.che.selenium.git;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import org.eclipse.che.commons.lang.NameGenerator;
import org.eclipse.che.selenium.core.TestGroup;
import org.eclipse.che.selenium.core.client.TestUserPreferencesServiceClient;
import org.eclipse.che.selenium.core.constant.TestGitConstants;
import org.eclipse.che.selenium.core.constant.TestMenuCommandsConstants;
import org.eclipse.che.selenium.core.user.TestUser;
import org.eclipse.che.selenium.core.workspace.TestWorkspace;
import org.eclipse.che.selenium.pageobject.AskDialog;
import org.eclipse.che.selenium.pageobject.AskForValueDialog;
import org.eclipse.che.selenium.pageobject.CodenvyEditor;
import org.eclipse.che.selenium.pageobject.Ide;
import org.eclipse.che.selenium.pageobject.Loader;
import org.eclipse.che.selenium.pageobject.Menu;
import org.eclipse.che.selenium.pageobject.ProjectExplorer;
import org.eclipse.che.selenium.pageobject.WarningDialog;
import org.eclipse.che.selenium.pageobject.Wizard;
import org.eclipse.che.selenium.pageobject.git.Git;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/** @author Aleksandr Shmaraev */
@Test(groups = TestGroup.GITHUB)
public class GitCompareTest {
  private static final String PROJECT_NAME = NameGenerator.generate("GitCompare_", 4);
  private static final String LEFT_COMPARE_ST = "Line 2 : Column 1";
  private static final String TEXT_GROUP =
      "src/main/java/com/codenvy/example/spring\n"
          + "A.java\n"
          + "GreetingController.java\n"
          + "HelloWorld.java";

  @Inject private TestWorkspace ws;
  @Inject private Ide ide;
  @Inject private TestUser productUser;

  @Inject
  @Named("github.username")
  private String gitHubUsername;

  @Inject private ProjectExplorer projectExplorer;
  @Inject private Menu menu;
  @Inject private Git git;
  @Inject private Loader loader;
  @Inject private CodenvyEditor editor;
  @Inject private AskForValueDialog askForValueDialog;
  @Inject private AskDialog askDialog;
  @Inject private WarningDialog warningDialog;
  @Inject private TestUserPreferencesServiceClient testUserPreferencesServiceClient;

  @BeforeClass
  public void prepare() throws Exception {
    testUserPreferencesServiceClient.addGitCommitter(gitHubUsername, productUser.getEmail());

    ide.open(ws);
    projectExplorer.waitProjectExplorer();

    String repoUrl = "https://github.com/" + gitHubUsername + "/spring-project-for-compare.git";
    git.importJavaApp(repoUrl, PROJECT_NAME, Wizard.TypeProject.MAVEN);
    createBranch();
  }

  @Test
  public void checkGitCompareTest() {
    // expand the project and do changes in the Java class
    projectExplorer.waitProjectExplorer();
    projectExplorer.waitItem(PROJECT_NAME);
    projectExplorer.quickExpandWithJavaScript();
    projectExplorer.openItemByPath(
        PROJECT_NAME + "/src/main/java/com/codenvy/example/spring/GreetingController.java");
    editor.setCursorToLine(2);
    editor.typeTextIntoEditor("// <<< checking compare content >>>");
    editor.waitTextIntoEditor("// <<< checking compare content >>>");
    projectExplorer.openItemByPath(
        PROJECT_NAME + "/src/main/java/com/codenvy/example/spring/HelloWorld.java");
    editor.setCursorToLine(2);
    editor.typeTextIntoEditor("// <<< checking compare content >>>");
    editor.waitTextIntoEditor("// <<< checking compare content >>>");

    // check the 'git compare' with the latest repository version
    projectExplorer.waitAndSelectItem(PROJECT_NAME);
    menu.runCommand(
        TestMenuCommandsConstants.Git.GIT,
        TestMenuCommandsConstants.Git.Compare.COMPARE_TOP,
        TestMenuCommandsConstants.Git.Compare.COMPARE_LATEST_VER);
    loader.waitOnClosed();
    git.waitGroupGitCompareIsOpen();
    git.selectFileInChangedFilesTreePanel("GreetingController.java");
    git.clickOnGroupCompareButton();
    git.waitGitCompareFormIsOpen();
    git.waitExpTextIntoCompareLeftEditor("// <<< checking compare content >>>");
    git.waitTextNotPresentIntoCompareRightEditor("// <<< checking compare content >>>");
    git.closeGitCompareForm();
    git.closeGroupGitCompareForm();

    // check git compare after adding and deleting java class
    projectExplorer.waitAndSelectItem(PROJECT_NAME + "/src/main/java/com/codenvy/example/spring");
    menu.runCommand(
        TestMenuCommandsConstants.Project.PROJECT,
        TestMenuCommandsConstants.Project.New.NEW,
        TestMenuCommandsConstants.Project.New.JAVA_CLASS);
    loader.waitOnClosed();
    askForValueDialog.waitNewJavaClassOpen();
    askForValueDialog.typeTextInFieldName("A");
    askForValueDialog.clickOkBtnNewJavaClass();
    askForValueDialog.waitNewJavaClassClose();
    projectExplorer.waitItem(PROJECT_NAME + "/src/main/java/com/codenvy/example/spring/A.java");
    projectExplorer.waitAndSelectItem(PROJECT_NAME);
    menu.runCommand(TestMenuCommandsConstants.Git.GIT, TestMenuCommandsConstants.Git.ADD_TO_INDEX);
    git.waitAddToIndexFormToOpen();
    git.confirmAddToIndexForm();
    git.waitGitStatusBarWithMess(TestGitConstants.GIT_ADD_TO_INDEX_SUCCESS);
    projectExplorer.waitAndSelectItem(PROJECT_NAME);
    menu.runCommand(
        TestMenuCommandsConstants.Git.GIT,
        TestMenuCommandsConstants.Git.Compare.COMPARE_TOP,
        TestMenuCommandsConstants.Git.Compare.COMPARE_LATEST_VER);
    loader.waitOnClosed();
    git.waitGroupGitCompareIsOpen();
    git.waitExpTextInGroupGitCompare(TEXT_GROUP);
    git.closeGroupGitCompareForm();
    projectExplorer.waitAndSelectItem(
        PROJECT_NAME + "/src/main/java/com/codenvy/example/spring/HelloWorld.java");
    menu.runCommand(TestMenuCommandsConstants.Edit.EDIT, TestMenuCommandsConstants.Edit.DELETE);
    loader.waitOnClosed();
    acceptDialogWithText("Delete file \"HelloWorld.java\"?");
    loader.waitOnClosed();
    projectExplorer.waitAndSelectItem(PROJECT_NAME);
    menu.runCommand(
        TestMenuCommandsConstants.Git.GIT,
        TestMenuCommandsConstants.Git.Compare.COMPARE_TOP,
        TestMenuCommandsConstants.Git.Compare.COMPARE_LATEST_VER);
    loader.waitOnClosed();
    git.waitGroupGitCompareIsOpen();
    git.waitExpTextInGroupGitCompare(TEXT_GROUP);
    git.selectFileInChangedFilesTreePanel("A.java");
    git.clickOnGroupCompareButton();
    git.waitGitCompareFormIsOpen();
    git.waitExpTextIntoCompareLeftEditor("public class A");
    git.setFocusOnLeftGitCompareEditor();
    git.setCursorToLine(2, LEFT_COMPARE_ST);
    git.typeTextIntoGitCompareEditor("//***che***codenvy***");
    git.waitExpTextIntoCompareLeftEditor("//***che***codenvy***");
    git.clickOnGitCompareCloseButton();
    askDialog.confirmAndWaitClosed();
    git.waitGitCompareFormIsClosed();
    git.closeGroupGitCompareForm();
    projectExplorer.openItemByPath(
        PROJECT_NAME + "/src/main/java/com/codenvy/example/spring/A.java");
    editor.waitActive();
    editor.waitTextIntoEditor("//***che***codenvy***");

    // check the 'git compare' after commit
    projectExplorer.waitAndSelectItem(PROJECT_NAME);
    menu.runCommand(TestMenuCommandsConstants.Git.GIT, TestMenuCommandsConstants.Git.ADD_TO_INDEX);
    git.waitGitStatusBarWithMess(TestGitConstants.GIT_ADD_TO_INDEX_SUCCESS);
    menu.runCommand(TestMenuCommandsConstants.Git.GIT, TestMenuCommandsConstants.Git.COMMIT);
    git.waitAndRunCommit("Update Java class");
    git.waitGitStatusBarWithMess(TestGitConstants.COMMIT_MESSAGE_SUCCESS);
    loader.waitOnClosed();
    projectExplorer.waitAndSelectItem(
        PROJECT_NAME + "/src/main/java/com/codenvy/example/spring/GreetingController.java");
    menu.runCommand(
        TestMenuCommandsConstants.Git.GIT,
        TestMenuCommandsConstants.Git.Compare.COMPARE_TOP,
        TestMenuCommandsConstants.Git.Compare.COMPARE_LATEST_VER);
    loader.waitOnClosed();
    warningDialog.waitWaitWarnDialogWindowWithSpecifiedTextMess(
        "There are no changes in the selected item.");
    warningDialog.clickOkBtn();

    // check the 'git compare' for another local branch
    projectExplorer.waitProjectExplorer();
    menu.runCommand(
        TestMenuCommandsConstants.Git.GIT,
        TestMenuCommandsConstants.Git.Compare.COMPARE_TOP,
        TestMenuCommandsConstants.Git.Compare.COMPARE_WITH_BRANCH);
    git.waitGitCompareBranchFormIsOpen();
    git.selectBranchIntoGitCompareBranchForm("newbranch");
    git.clickOnCompareBranchFormButton();
    loader.waitOnClosed();
    git.waitGitCompareFormIsOpen();
    git.waitExpTextIntoCompareLeftEditor("// <<< checking compare content >>>");
    git.waitTextNotPresentIntoCompareRightEditor("// <<< checking compare content >>>");
    git.closeGitCompareForm();

    // check the 'git compare' for remote branch
    projectExplorer.waitProjectExplorer();
    menu.runCommand(
        TestMenuCommandsConstants.Git.GIT,
        TestMenuCommandsConstants.Git.Compare.COMPARE_TOP,
        TestMenuCommandsConstants.Git.Compare.COMPARE_WITH_BRANCH);
    git.waitGitCompareBranchFormIsOpen();
    git.selectBranchIntoGitCompareBranchForm("origin/master");
    git.clickOnCompareBranchFormButton();
    loader.waitOnClosed();
    git.waitGitCompareFormIsOpen();
    git.waitExpTextIntoCompareLeftEditor("// <<< checking compare content >>>");
    git.waitTextNotPresentIntoCompareRightEditor("// <<< checking compare content >>>");
    git.closeGitCompareForm();

    // check the 'git compare' for revision
    projectExplorer.waitProjectExplorer();
    projectExplorer.waitAndSelectItem(
        PROJECT_NAME + "/src/main/java/com/codenvy/example/spring/GreetingController.java");
    menu.runCommand(
        TestMenuCommandsConstants.Git.GIT,
        TestMenuCommandsConstants.Git.Compare.COMPARE_TOP,
        TestMenuCommandsConstants.Git.Compare.COMPARE_WITH_REVISION);
    git.waitGitCompareRevisionFormIsOpen();
    git.clickOnCloseRevisionButton();
    menu.runCommand(
        TestMenuCommandsConstants.Git.GIT,
        TestMenuCommandsConstants.Git.Compare.COMPARE_TOP,
        TestMenuCommandsConstants.Git.Compare.COMPARE_WITH_REVISION);
    git.waitGitCompareRevisionFormIsOpen();
    git.selectRevisionIntoCompareRevisionForm(1);
    git.clickOnRevisionCompareButton();
    git.waitGitCompareFormIsOpen();
    git.waitExpTextIntoCompareLeftEditor("// <<< checking compare content >>>");
    git.waitTextNotPresentIntoCompareRightEditor("// <<< checking compare content >>>");
    git.closeGitCompareForm();
    git.waitGitCompareRevisionFormIsOpen();
    git.clickOnCloseRevisionButton();
  }

  private void createBranch() {
    projectExplorer.waitAndSelectItem(PROJECT_NAME);
    menu.runCommand(TestMenuCommandsConstants.Git.GIT, TestMenuCommandsConstants.Git.BRANCHES);
    git.waitBranchInTheList("master");
    git.waitDisappearBranchName("newbranch");
    git.waitEnabledAndClickCreateBtn();
    git.typeAndWaitNewBranchName("newbranch");
    git.waitBranchInTheList("master");
    git.waitBranchInTheList("newbranch");
    git.closeBranchesForm();
  }

  private void acceptDialogWithText(String expectedText) {
    askDialog.waitFormToOpen();
    askDialog.containsText(expectedText);
    askDialog.clickOkBtn();
    loader.waitOnClosed();
  }
}
