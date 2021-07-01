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
 * Blackbox-test class for the password-reset functionality. Requires that the given user is registered and logged in.
 *
 * @author Ivan Charviakou
 */
public class T110 {

    private static WebDriver driver;
    private static WebDriverWait waiter;
    private static String threadName;
    private static boolean isMultiThreaded = false;

    private static final String EMAIL = "nutzer.sep2021test@gmail.com";
    private static final String PASSWORD = "sdfHs4!a";
    private static final String FIRST_NAME = "Bob";

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
     * Performs a password-reset using the preset data values in the password-reset page, and ensures that
     * the correct user is logged in by comparing displayed and submitted user data.
     */
    @Test
    public void t110() {

        // Log out
        Selenium.clickOn(waiter, "accountDropDown");
        Selenium.clickOn(waiter, "form_log_out:button_log_out");

        // Initiate password reset
        Selenium.typeInto(waiter, "login_form:login_email_field", EMAIL);
        Selenium.clickOn(waiter, "login_form:login_reset_password_button");

        // Navigate to password reset page
        Selenium.waitUntilLoaded();
        Selenium.navigateToLinkInId(driver, "messageForm:neutralMessage");

        // Reset password
        Selenium.typeInto(waiter, "passwordResetForm:password", PASSWORD);
        Selenium.typeInto(waiter, "passwordResetForm:repeatPassword", PASSWORD);
        Selenium.clickOn(waiter, "passwordResetForm:submitButton");

        // Verify data in profile page
        assertTrue(Selenium.contentOfIdEqualTo(waiter, "form_profile:frstname", "value", FIRST_NAME));

    }

    public static String getThreadName() {
        return threadName;
    }

    public static void setThreadName(String threadName) {
        T110.threadName = threadName;
    }

    public static boolean isMultiThreaded() {
        return isMultiThreaded;
    }

    public static void setMultiThreaded(boolean isMultiThreaded) {
        T110.isMultiThreaded = isMultiThreaded;
    }

}
