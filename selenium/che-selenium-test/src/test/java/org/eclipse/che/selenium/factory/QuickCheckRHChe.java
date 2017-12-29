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
package org.eclipse.che.selenium.factory;

import com.google.inject.Inject;
import org.eclipse.che.selenium.core.SeleniumWebDriver;
import org.eclipse.che.selenium.core.constant.TestTimeoutsConstants;
import org.eclipse.che.selenium.core.user.TestUser;
import org.eclipse.che.selenium.pageobject.dashboard.Dashboard;
import org.eclipse.che.selenium.pageobject.dashboard.DashboardFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class QuickCheckRHChe {
  @Inject private Dashboard dashboard;
  @Inject private DashboardFactory dashboardFactory;
  @Inject private SeleniumWebDriver seleniumWebDriver;
  @Inject private TestUser user;

  @BeforeClass
  public void setUp() throws Exception {
    WebDriverWait redrawWait =
        new WebDriverWait(seleniumWebDriver, TestTimeoutsConstants.REDRAW_UI_ELEMENTS_TIMEOUT_SEC);
    redrawWait
        .until(ExpectedConditions.visibilityOfElementLocated(By.id("username")))
        .sendKeys(user.getEmail());
    redrawWait
        .until(ExpectedConditions.visibilityOfElementLocated(By.id("password")))
        .sendKeys(user.getPassword());
    redrawWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("kc-login"))).click();
  }

  @Test
  public void checkFactoryProcessing() throws Exception {
    dashboardFactory.clickOnOpenFactory();
    dashboardFactory.selectFactoryOnNavBar();
    dashboardFactory.waitAllFactoriesPage();
  }
}
