/*
 * Copyright (c) 2012-2017 Red Hat, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Red Hat, Inc. - initial API and implementation
 */
package org.eclipse.che.selenium.pageobject.dashboard.workspaces;

import static java.lang.String.format;
import static org.eclipse.che.selenium.core.constant.TestTimeoutsConstants.ELEMENT_TIMEOUT_SEC;
import static org.eclipse.che.selenium.core.constant.TestTimeoutsConstants.LOADER_TIMEOUT_SEC;
import static org.eclipse.che.selenium.core.constant.TestTimeoutsConstants.LOAD_PAGE_TIMEOUT_SEC;
import static org.eclipse.che.selenium.core.constant.TestTimeoutsConstants.REDRAW_UI_ELEMENTS_TIMEOUT_SEC;
import static org.openqa.selenium.support.ui.ExpectedConditions.invisibilityOfElementLocated;
import static org.openqa.selenium.support.ui.ExpectedConditions.visibilityOf;
import static org.openqa.selenium.support.ui.ExpectedConditions.visibilityOfElementLocated;

import com.google.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.che.selenium.core.SeleniumWebDriver;
import org.eclipse.che.selenium.pageobject.dashboard.Dashboard;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;

public class Workspaces {
  private final SeleniumWebDriver seleniumWebDriver;
  private final WebDriverWait redrawUiElementsTimeout;
  private final Dashboard dashboard;

  @Inject
  public Workspaces(SeleniumWebDriver seleniumWebDriver, Dashboard dashboard) {
    this.seleniumWebDriver = seleniumWebDriver;
    this.redrawUiElementsTimeout =
        new WebDriverWait(seleniumWebDriver, REDRAW_UI_ELEMENTS_TIMEOUT_SEC);
    this.dashboard = dashboard;
    PageFactory.initElements(seleniumWebDriver, this);
  }

  private interface Locators {
    String WORKSPACES_LIST = "//ng-transclude[@class='che-list-content']";
    String TOOLBAR = "Workspaces";
    String DOCUMENTATION_LINK = "//a[@ng-href='/docs/devops/intro/index.html']";

    String ADD_WORKSPACE_BTN = "//che-button-primary[@che-button-title='Add Workspace']";
    String DELETE_WORKSPACE_BTN = "//che-button-primary[@che-button-title='Delete']";
    String DELETE_DIALOG_BUTTON = "//md-dialog[@role='dialog']//button/span[text()='Delete']";
    String BULK_CHECKBOX = "//md-checkbox[@aria-label='Workspace list']";

    String SEARCH_WORKSPACE_FIELD = "//input[@ng-placeholder='Search']";

    String WORKSPACE_ITEM_NAME = "//div[@id='ws-name-%s']";
    String WORKSPACE_ITEM_CHECKBOX = "//div[@id='ws-name-%s']//md-checkbox";

    String WORKSPACE_ITEM_RAM = "//div[@id='ws-name-%s']//span[@name='workspaceRamValue']";
    String WORKSPACE_ITEM_PROJECTS =
        "//div[@id='ws-name-%s']//span[@name='workspaceProjectsValue']";
    String WORKSPACE_ITEM_STACK = "//div[@id='ws-name-%s']//span[@name='workspaceStackName']";

    String WORKSPACE_ITEM_ACTIONS = "//div[@id='ws-name-%s']//*[@name='workspaceStopStartButton']";
    String WORKSPACE_ITEM_CONFIGURE_BUTTON =
        "//div[@id='ws-name-%s']//a[@name='configureWorkspaceButton']";
    String WORKSPACE_ITEM_ADD_PROJECT_BUTTON =
        "//div[@id='ws-name-%s']//span[@name='addProjectButton']";

    String WORKSPACE_LIST_HEADER = "//md-item[@class='noselect']//span";
  }

  @FindBy(xpath = Locators.WORKSPACES_LIST)
  WebElement listOfWorkspaces;

  @FindBy(xpath = Locators.ADD_WORKSPACE_BTN)
  WebElement addWorkspaceBtn;

  @FindBy(xpath = Locators.DELETE_WORKSPACE_BTN)
  WebElement deleleWorkspaceButton;

  @FindBy(xpath = Locators.DELETE_DIALOG_BUTTON)
  WebElement deleteBtn;

  @FindBy(xpath = Locators.SEARCH_WORKSPACE_FIELD)
  WebElement searchWorkspaceField;

  public void waitDocumentationLink() {
    redrawUiElementsTimeout.until(
        visibilityOfElementLocated(By.xpath(Locators.DOCUMENTATION_LINK)));
  }

  public void clickOnDocumentationLink() {
    redrawUiElementsTimeout
        .until(visibilityOfElementLocated(By.xpath(Locators.DOCUMENTATION_LINK)))
        .click();
  }

  public void waitSearchWorkspaceByNameField() {
    redrawUiElementsTimeout.until(visibilityOf(searchWorkspaceField));
  }

  public void typeToSearchInput(String value) {
    redrawUiElementsTimeout.until(visibilityOf(searchWorkspaceField)).clear();
    searchWorkspaceField.sendKeys(value);
  }

  // select workspaces by checkboxes
  public void selectWorkspaceByCheckbox(String workspaceName) {
    redrawUiElementsTimeout
        .until(
            visibilityOfElementLocated(
                By.xpath(format(Locators.WORKSPACE_ITEM_CHECKBOX, workspaceName))))
        .click();
  }

  public void waitBulkCheckbox() {
    redrawUiElementsTimeout.until(visibilityOfElementLocated(By.xpath(Locators.BULK_CHECKBOX)));
  }

  public void selectAllWorkspacesByBulk() {
    redrawUiElementsTimeout
        .until(visibilityOfElementLocated(By.xpath(Locators.BULK_CHECKBOX)))
        .click();
  }

  public boolean isWorkspaceChecked(String workspaceName) {
    String attrValue =
        redrawUiElementsTimeout
            .until(
                visibilityOfElementLocated(
                    By.xpath(format(Locators.WORKSPACE_ITEM_CHECKBOX, workspaceName))))
            .getAttribute("aria-checked");

    return Boolean.parseBoolean(attrValue);
  }

  public String getWorkspaceRamValue(String workspaceName) {
    return redrawUiElementsTimeout
        .until(
            visibilityOfElementLocated(
                By.xpath(format(Locators.WORKSPACE_ITEM_RAM, workspaceName))))
        .getText();
  }

  public String getWorkspaceProjectsValue(String workspaceName) {
    return redrawUiElementsTimeout
        .until(
            visibilityOfElementLocated(
                By.xpath(format(Locators.WORKSPACE_ITEM_PROJECTS, workspaceName))))
        .getText();
  }

  public String getWorkspaceStackName(String workspaceName) {
    return redrawUiElementsTimeout
        .until(
            visibilityOfElementLocated(
                By.xpath(format(Locators.WORKSPACE_ITEM_STACK, workspaceName))))
        .getText();
  }

  public void clickOnWorkspaceActionsButton(String workspaceName) {
    redrawUiElementsTimeout
        .until(
            visibilityOfElementLocated(
                By.xpath(format(Locators.WORKSPACE_ITEM_ACTIONS, workspaceName))))
        .click();
  }

  public void clickOnWorkspaceConfigureButton(String workspaceName) {
    redrawUiElementsTimeout
        .until(
            visibilityOfElementLocated(
                By.xpath(format(Locators.WORKSPACE_ITEM_CONFIGURE_BUTTON, workspaceName))))
        .click();
  }

  public void clickOnWorkspaceAddProjectButton(String workspaceName) {
    redrawUiElementsTimeout
        .until(
            visibilityOfElementLocated(
                By.xpath(format(Locators.WORKSPACE_ITEM_ADD_PROJECT_BUTTON, workspaceName))))
        .click();
  }

  public void waitListWorkspacesOnDashboard() {
    new WebDriverWait(seleniumWebDriver, LOAD_PAGE_TIMEOUT_SEC)
        .until(visibilityOf(listOfWorkspaces));
  }

  public String getTextFromListWorkspaces() {
    return listOfWorkspaces.getText();
  }

  public void waitExpTextFromListWsOnDashboard(String expText) {
    new WebDriverWait(seleniumWebDriver, REDRAW_UI_ELEMENTS_TIMEOUT_SEC)
        .until((WebDriver driver) -> getTextFromListWorkspaces().contains(expText));
  }

  public void selectWorkspaceItemName(String wsName) {
    new WebDriverWait(seleniumWebDriver, REDRAW_UI_ELEMENTS_TIMEOUT_SEC)
        .until(visibilityOfElementLocated(By.xpath(format(Locators.WORKSPACE_ITEM_NAME, wsName))))
        .click();
  }

  // TODO revork next two methods
  public void waitWorkspaceIsPresent(String nameWorkspace) {
    new WebDriverWait(seleniumWebDriver, ELEMENT_TIMEOUT_SEC)
        .until(
            visibilityOfElementLocated(
                By.xpath(format(Locators.WORKSPACE_ITEM_NAME, nameWorkspace))));
  }

  /** wait the workspace is not present on dashboard */
  public void waitWorkspaceIsNotPresent(String nameWorkspace) {
    new WebDriverWait(seleniumWebDriver, ELEMENT_TIMEOUT_SEC)
        .until(
            invisibilityOfElementLocated(
                By.xpath(format(Locators.WORKSPACE_ITEM_NAME, nameWorkspace))));
  }

  /** Wait toolbar name is present on dashboard */
  public void waitToolbarTitleName() {
    new WebDriverWait(seleniumWebDriver, LOADER_TIMEOUT_SEC)
        .until(visibilityOfElementLocated(By.id(Locators.TOOLBAR)));
  }

  // Click on the Add Workspace button
  public void clickOnAddWorkspaceBtn() {
    dashboard.waitNotificationIsClosed();
    new WebDriverWait(seleniumWebDriver, REDRAW_UI_ELEMENTS_TIMEOUT_SEC)
        .until(visibilityOf(addWorkspaceBtn))
        .click();
  }

  public void waitAddWorkspaceButton() {
    new WebDriverWait(seleniumWebDriver, LOAD_PAGE_TIMEOUT_SEC)
        .until(visibilityOf(addWorkspaceBtn));
  }

  public void clickOnDeleteWorkspacesBtn() {
    dashboard.waitNotificationIsClosed();
    redrawUiElementsTimeout.until(visibilityOf(deleleWorkspaceButton)).click();
  }

  /** Click on the delete/remove button in the dialog window */
  public void clickOnDeleteButtonInDialogWindow() {
    new WebDriverWait(seleniumWebDriver, REDRAW_UI_ELEMENTS_TIMEOUT_SEC)
        .until(visibilityOf(deleteBtn))
        .click();
  }

  public ArrayList<String> getWorkspaceListHeaders() {
    ArrayList<String> titles = new ArrayList<>();
    List<WebElement> headers =
        seleniumWebDriver.findElements(By.xpath(Locators.WORKSPACE_LIST_HEADER));
    headers.forEach(
        header -> {
          titles.add(header.getText());
        });

    return titles;
  }
}
