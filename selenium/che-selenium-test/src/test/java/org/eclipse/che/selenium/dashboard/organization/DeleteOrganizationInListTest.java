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
package org.eclipse.che.selenium.dashboard.organization;

import static org.eclipse.che.selenium.pageobject.dashboard.NavigationBar.MenuItem.ORGANIZATIONS;
import static org.eclipse.che.selenium.pageobject.dashboard.organization.OrganizationListPage.OrganizationListHeader.NAME;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import org.eclipse.che.selenium.core.TestGroup;
import org.eclipse.che.selenium.core.client.TestOrganizationServiceClient;
import org.eclipse.che.selenium.core.organization.InjectTestOrganization;
import org.eclipse.che.selenium.core.organization.TestOrganization;
import org.eclipse.che.selenium.core.user.AdminTestUser;
import org.eclipse.che.selenium.pageobject.dashboard.ConfirmDialog;
import org.eclipse.che.selenium.pageobject.dashboard.Dashboard;
import org.eclipse.che.selenium.pageobject.dashboard.NavigationBar;
import org.eclipse.che.selenium.pageobject.dashboard.organization.OrganizationListPage;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * Test validates deleting of organizations from the list of organizations.
 *
 * @author Ann Shumilova
 */
@Test(groups = {TestGroup.MULTIUSER})
public class DeleteOrganizationInListTest {
  private int initialOrgNumber;

  @InjectTestOrganization private TestOrganization org1;
  @InjectTestOrganization private TestOrganization org2;
  @InjectTestOrganization private TestOrganization org3;
  @InjectTestOrganization private TestOrganization org4;

  @Inject
  @Named("admin")
  private TestOrganizationServiceClient testOrganizationServiceClient;

  @Inject private OrganizationListPage organizationListPage;
  @Inject private NavigationBar navigationBar;
  @Inject private ConfirmDialog confirmDialog;
  @Inject private AdminTestUser adminTestUser;
  @Inject private Dashboard dashboard;

  @BeforeClass
  public void setUp() throws Exception {
    initialOrgNumber = testOrganizationServiceClient.getAllRoot().size();
    dashboard.open(adminTestUser.getName(), adminTestUser.getPassword());
  }

  public void testOrganizationDeletionFromList() {
    // Open the Organization list and check that created organization exist
    navigationBar.waitNavigationBar();
    navigationBar.clickOnMenu(ORGANIZATIONS);
    organizationListPage.waitForOrganizationsToolbar();
    organizationListPage.waitForOrganizationsList();
    assertEquals(navigationBar.getMenuCounterValue(ORGANIZATIONS), initialOrgNumber);

    try {
      assertEquals(organizationListPage.getOrganizationListItemCount(), initialOrgNumber);
    } catch (AssertionError a) {
      // remove try-catch block after https://github.com/eclipse/che/issues/7279 has been resolved
      fail("Known issue https://github.com/eclipse/che/issues/7279", a);
    }

    assertTrue(organizationListPage.getValues(NAME).contains(org1.getName()));

    // Tests the Delete organization dialog
    organizationListPage.clickOnDeleteButton(org1.getName());
    confirmDialog.waitOpened();
    assertEquals(confirmDialog.getTitle(), "Delete organization");
    assertEquals(
        confirmDialog.getMessage(),
        String.format("Would you like to delete organization '%s'?", org1.getName()));
    assertEquals(confirmDialog.getConfirmButtonTitle(), "Delete");
    assertEquals(confirmDialog.getCancelButtonTitle(), "Close");
    confirmDialog.closeDialog();
    confirmDialog.waitClosed();

    try {
      assertEquals(organizationListPage.getOrganizationListItemCount(), initialOrgNumber);
    } catch (AssertionError a) {
      // remove try-catch block after https://github.com/eclipse/che/issues/7279 has been resolved
      fail("Known issue https://github.com/eclipse/che/issues/7279", a);
    }

    organizationListPage.clickOnDeleteButton(org1.getName());
    confirmDialog.waitOpened();
    confirmDialog.clickCancel();
    confirmDialog.waitClosed();
    assertEquals(organizationListPage.getOrganizationListItemCount(), initialOrgNumber);

    // Delete all organizations from the Organization list
    deleteOrganization(org1.getName(), initialOrgNumber - 1);
    deleteOrganization(org2.getName(), initialOrgNumber - 2);
    deleteOrganization(org3.getName(), initialOrgNumber - 3);
    deleteOrganization(org4.getName(), initialOrgNumber - 4);
  }

  private void deleteOrganization(String orgName, int remainedOrgNumber) {
    organizationListPage.clickOnDeleteButton(orgName);
    confirmDialog.waitOpened();
    confirmDialog.clickConfirm();
    confirmDialog.waitClosed();
    organizationListPage.waitForOrganizationsList();

    // Test that organization deleted
    try {
      assertEquals(organizationListPage.getOrganizationListItemCount(), remainedOrgNumber);
    } catch (AssertionError a) {
      // remove try-catch block after https://github.com/eclipse/che/issues/7279 has been resolved
      fail("Known issue https://github.com/eclipse/che/issues/7279", a);
    }

    assertEquals(navigationBar.getMenuCounterValue(ORGANIZATIONS), remainedOrgNumber);
    assertFalse(organizationListPage.getValues(NAME).contains(orgName));
  }
}
