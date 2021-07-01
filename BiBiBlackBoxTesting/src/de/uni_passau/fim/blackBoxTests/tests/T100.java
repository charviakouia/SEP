package de.uni_passau.fim.blackBoxTests.tests;

import de.uni_passau.fim.blackBoxTests.util.Driver;
import de.uni_passau.fim.blackBoxTests.util.Pages;
import de.uni_passau.fim.blackBoxTests.util.Selenium;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.WebDriverWait;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Blackbox-test class for the registration functionality. Requires that the given email does not yet appear
 * in the data store.
 *
 * @author Ivan Charviakou
 */
public class T100 {

    private static WebDriver driver;
    private static WebDriverWait waiter;
    private static String threadName;
    private static boolean isMultiThreaded = false;

    private static final String FIRST_NAME = "Bob";
    private static final String LAST_NAME = "Mustermann";
    private static final String PASSWORD = "sdfHs4!a";
    private static final String EMAIL = "nutzer.sep2021test@gmail.com";
    private static final String ZIP = "94032";
    private static final String CITY = "Passau";
    private static final String STREET = "Innstraße";
    private static final String STREET_NUMBER = "40";

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
     * Performs a registration using the preset data values, navigates to the profile page, and compares the
     * appearing values to the inputted values.
     */
    @Test
    public void t100() {

        // Navigate to registration page, input registration data, and save
        Selenium.clickOn(waiter, "registrationLink");
        Selenium.typeInto(waiter, "registrationForm:firstName", FIRST_NAME);
        Selenium.typeInto(waiter, "registrationForm:lastName", LAST_NAME);
        Selenium.typeInto(waiter, "registrationForm:password", PASSWORD);
        Selenium.typeInto(waiter, "registrationForm:confirmedPassword", PASSWORD);
        Selenium.typeInto(waiter, "registrationForm:email", EMAIL);
        Selenium.typeInto(waiter, "registrationForm:zip", ZIP);
        Selenium.typeInto(waiter, "registrationForm:city", CITY);
        Selenium.typeInto(waiter, "registrationForm:street", STREET);
        Selenium.typeInto(waiter, "registrationForm:streetNumber", STREET_NUMBER);
        Selenium.clickOn(waiter, "registrationForm:registration_save_button");

        // Navigate to email confirmation page
        Selenium.waitUntilLoaded(waiter, "messageForm:neutralMessage");
        Selenium.navigateToLinkInId(driver, "messageForm:neutralMessage");

        // Verify data in profile page
        assertTrue(Selenium.contentOfIdEqualTo(waiter, "form_profile:frstname", "value", FIRST_NAME));
        assertTrue(Selenium.contentOfIdEqualTo(waiter, "form_profile:email", "value", EMAIL));

    }

    public static String getThreadName() {
        return threadName;
    }

    public static void setThreadName(String threadName) {
        T100.threadName = threadName;
    }

    public static boolean isMultiThreaded() {
        return isMultiThreaded;
    }

    public static void setMultiThreaded(boolean isMultiThreaded) {
        T100.isMultiThreaded = isMultiThreaded;
    }

}
