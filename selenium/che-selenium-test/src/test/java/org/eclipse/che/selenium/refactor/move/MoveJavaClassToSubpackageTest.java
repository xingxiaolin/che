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
import java.nio.file.Paths;
import org.eclipse.che.commons.lang.NameGenerator;
import org.eclipse.che.selenium.core.client.TestProjectServiceClient;
import org.eclipse.che.selenium.core.project.ProjectTemplates;
import org.eclipse.che.selenium.core.workspace.TestWorkspace;
import org.eclipse.che.selenium.pageobject.Ide;
import org.eclipse.che.selenium.pageobject.Loader;
import org.eclipse.che.selenium.pageobject.ProjectExplorer;
import org.eclipse.che.selenium.pageobject.Refactor;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/** @author Aleksandr Shmaraev */
public class MoveJavaClassToSubpackageTest {

  private static final String PROJECT_NAME =
      NameGenerator.generate("MoveJavaClassToSubpackageProject-", 4);

  @Inject private TestWorkspace workspace;
  @Inject private Ide ide;
  @Inject private ProjectExplorer projectExplorer;
  @Inject private Loader loader;
  @Inject private Refactor refactor;
  @Inject private TestProjectServiceClient testProjectServiceClient;

  @BeforeClass
  public void prepare() throws Exception {
    URL resource = getClass().getResource("/projects/default-spring-project");
    testProjectServiceClient.importProject(
        workspace.getId(),
        Paths.get(resource.toURI()),
        PROJECT_NAME,
        ProjectTemplates.MAVEN_SPRING);
    ide.open(workspace);
  }

  @Test
  public void checkProjectTreeAfterMoveJavaFile() {
    projectExplorer.waitProjectExplorer();
    projectExplorer.waitItem(PROJECT_NAME);
    projectExplorer.quickExpandWithJavaScript();
    projectExplorer.waitAndSelectItem(
        PROJECT_NAME + "/src/main/java/org/eclipse/qa/examples/AppController.java");
    projectExplorer.launchRefactorMoveByKeyboard();
    refactor.waitMoveItemFormIsOpen();
    refactor.clickOnExpandIconTree(PROJECT_NAME);
    refactor.clickOnExpandIconTree("/src/main/java");
    refactor.chooseDestinationForItem("org.eclipse.qa");
    refactor.clickOkButtonRefactorForm();
    refactor.waitMoveItemFormIsClosed();
    loader.waitOnClosed();
    projectExplorer.waitItem(PROJECT_NAME + "/src/main/java/org/eclipse/qa/AppController.java");
  }
}
