package de.uni_passau.fim.blackBoxTests.tests;

import static org.junit.Assert.assertTrue;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import de.uni_passau.fim.blackBoxTests.util.Driver;
import de.uni_passau.fim.blackBoxTests.util.Pages;
import de.uni_passau.fim.blackBoxTests.util.Selenium;

/**
 * Blackbox-test class for the registration functionality. Requires that the given email does not yet appear
 * in the data store.
 *
 * @author Ivan Charviakou
 */
public class T100 {

    private WebDriver driver;
    private WebDriverWait waiter;
    private String threadName = "";
    private boolean isMultiThreaded = false;

    private final String FIRST_NAME = "Bob" + threadName;
    private final String LAST_NAME = "Mustermann" + threadName;
    private final String PASSWORD = "sdfHs4!a";
    private final String EMAIL = "nutzer.sep2021test" + threadName + "@gmail.com";
    private final String ZIP = "94032";
    private final String CITY = "Passau";
    private final String STREET = "Innstraße";
    private final String STREET_NUMBER = "40";

    @BeforeClass
    public void setUp() {
        if (!isMultiThreaded) {
            driver = Driver.getDriver();
            waiter = Driver.getDriverWait();
        }
        Selenium.navigateTo(driver, Pages.HOME);
    }

    @AfterClass
    public void tearDown() {}

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

    public String getThreadName() {
        return threadName;
    }

    public void setThreadName(String threadName) {
        this.threadName = threadName;
    }

    public boolean isMultiThreaded() {
        return isMultiThreaded;
    }

    public void setMultiThreaded(boolean isMultiThreaded) {
        this.isMultiThreaded = isMultiThreaded;
    }

	public WebDriver getDriver() {
		return driver;
	}

	public void setDriver(WebDriver driver) {
		this.driver = driver;
	}

	public WebDriverWait getWaiter() {
		return waiter;
	}

	public void setWaiter(WebDriverWait waiter) {
		this.waiter = waiter;
	}

    
}
