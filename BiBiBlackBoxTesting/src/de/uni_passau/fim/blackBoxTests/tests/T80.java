package de.uni_passau.fim.blackBoxTests.tests;

import de.uni_passau.fim.blackBoxTests.util.Driver;
import de.uni_passau.fim.blackBoxTests.util.Pages;
import de.uni_passau.fim.blackBoxTests.util.Selenium;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import static org.junit.Assert.assertTrue;

/**
 * Blackbox-test class for the log-out and site-notice functionalities. Requires that a user has already
 * been logged in.
 *
 * @author Ivan Charviakou
 */
public class T80 {

    private static WebDriver driver;
    private static WebDriverWait waiter;
    private static String threadName;
    private static boolean isMultiThreaded = false;

    private static final String SITE_NOTICE_TEXT = "Innstraße";

    @BeforeClass
    public static void setUp() {
        if (!isMultiThreaded) {
            driver = Driver.getDriver();
            waiter = Driver.getDriverWait();
        }
        Selenium.navigateTo(driver, Pages.HOME);
    }

    @AfterClass
    public static void tearDown() {}

    /**
     * Performs a log-out.
     */
    @Test
    public void t80a() {

        // Log out
        Selenium.clickOn(waiter, "accountDropDown");
        Selenium.clickOn(waiter, "form_log_out:button_log_out");

    }

    /**
     * Navigates to the site notice page and compares the content text to a given string.
     */
    @Test
    public void t80b() {

        // Navigate to site notice
        Selenium.clickOn(waiter, "site_notice_link");

        // Check if site notice page contains text
        assertTrue(Selenium.contentOfIdContains(waiter, "site_notice_output_text", SITE_NOTICE_TEXT));

    }

    public static String getThreadName() {
        return threadName;
    }

    public static void setThreadName(String threadName) {
        T80.threadName = threadName;
    }

    public static boolean isMultiThreaded() {
        return isMultiThreaded;
    }

    public static void setMultiThreaded(boolean isMultiThreaded) {
        T80.isMultiThreaded = isMultiThreaded;
    }

}
