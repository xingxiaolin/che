package org.eclipse.che.selenium.factory;

import com.google.inject.Inject;

import org.eclipse.che.selenium.pageobject.dashboard.Dashboard;
import org.eclipse.che.selenium.pageobject.dashboard.DashboardFactory;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;


public class QuickCheckRHChe {
    @Inject
    private Dashboard dashboard;
    @Inject private DashboardFactory dashboardFactory;

    @BeforeClass
    public void setUp() throws Exception {

    }

        @Test
    public void checkFactoryProcessing() throws Exception {
        dashboard.open();
            dashboardFactory.clickOnOpenFactory();
            dashboardFactory.selectFactoryOnNavBar();
            dashboardFactory.waitAllFactoriesPage();

    }

}
