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

    private WebDriver driver;
    private WebDriverWait waiter;
    private String threadName = "";
    private boolean isMultiThreaded = false;

    private final String EMAIL = "nutzer.sep2021test%s@gmail.com";
    private final String PASSWORD = "sdfHs4!a";
    private final String FIRST_NAME = "Bob%s";

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
     * Performs a password-reset using the preset data values in the password-reset page, and ensures that
     * the correct user is logged in by comparing displayed and submitted user data.
     */
    @Test
    public void t110() {

        // Log out
        Selenium.clickOn(waiter, "accountDropDown");
        Selenium.clickOn(waiter, "form_log_out:button_log_out");

        // Initiate password reset
        Selenium.typeInto(waiter, "login_form:login_email_field", gen(EMAIL));
        Selenium.clickOn(waiter, "login_form:login_reset_password_button");

        // Navigate to password reset page
        Selenium.waitUntilLoaded();
        Selenium.navigateToLinkInId(driver, "messageForm:neutralMessage");

        // Reset password
        Selenium.typeInto(waiter, "passwordResetForm:password", PASSWORD);
        Selenium.typeInto(waiter, "passwordResetForm:repeatPassword", PASSWORD);
        Selenium.clickOn(waiter, "passwordResetForm:submitButton");

        // Verify data in profile page
        assertTrue(Selenium.contentOfIdEqualTo(waiter, "form_profile:frstname", "value", gen(FIRST_NAME)));

    }

    private String gen(String str){
        return String.format(str, threadName);
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
