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
package org.eclipse.che.selenium.pageobject;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.eclipse.che.selenium.core.SeleniumWebDriver;
import org.openqa.selenium.Alert;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

@Singleton
public class PopupDialogsBrowser {

  private final SeleniumWebDriver seleniumWebDriver;

  @Inject
  public PopupDialogsBrowser(SeleniumWebDriver seleniumWebDriver) {
    this.seleniumWebDriver = seleniumWebDriver;
    PageFactory.initElements(seleniumWebDriver, this);
  }

  /**
   * check state pop up browser window
   *
   * @return
   */
  public boolean isAlertPresent() {
    try {
      Alert alert = seleniumWebDriver.switchTo().alert();
      return !alert.getText().isEmpty();
    } catch (Exception e) {
      return false;
    }
  }

  /** click on accept button on pop up browser window */
  public void acceptAlert() {
    Alert alert = seleniumWebDriver.switchTo().alert();
    alert.accept();
  }

  /** Wait pop up browser window is closed */
  public void waitAlertClose() {
    new WebDriverWait(seleniumWebDriver, 20)
        .until(ExpectedConditions.not(ExpectedConditions.alertIsPresent()));
  }
}
