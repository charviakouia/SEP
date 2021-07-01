package de.uni_passau.fim.blackBoxTests.tests;

import de.uni_passau.fim.blackBoxTests.util.Driver;
import de.uni_passau.fim.blackBoxTests.util.Pages;
import de.uni_passau.fim.blackBoxTests.util.Selenium;
import org.junit.*;
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

    private WebDriver driver;
    private WebDriverWait waiter;
    private String threadName = "";
    private boolean isMultiThreaded = false;

    private static final String SITE_NOTICE_TEXT = "Innstraße";

    @Before
    public void setUp() {
        if (!isMultiThreaded) {
            driver = Driver.getDriver();
            waiter = Driver.getDriverWait();
        }
        Selenium.navigateTo(driver, Pages.HOME);
    }

    @After
    public void tearDown() {}

    /**
     * Performs a log-out and navigates to the site notice page. There, the test
     * compares the content text to a given string.
     */
    @Test
    public void t80() {

        // Log out
        Selenium.clickOn(waiter, "accountDropDown");
        Selenium.clickOn(waiter, "form_log_out:button_log_out");

        // Navigate to site notice
        Selenium.clickOn(waiter, "site_notice_link");

        // Check if site notice page contains text
        assertTrue(Selenium.contentOfIdContains(waiter, "site_notice_output_text", SITE_NOTICE_TEXT));

    }

    public String getThreadName() {
        return this.threadName;
    }

    public void setThreadName(String threadName) {
        this.threadName = threadName;
    }

    public boolean isMultiThreaded() {
        return this.isMultiThreaded;
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
