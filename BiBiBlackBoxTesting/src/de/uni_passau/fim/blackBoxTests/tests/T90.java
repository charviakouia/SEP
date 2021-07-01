package de.uni_passau.fim.blackBoxTests.tests;

import static org.junit.Assert.assertTrue;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import de.uni_passau.fim.blackBoxTests.util.Driver;
import de.uni_passau.fim.blackBoxTests.util.Pages;
import de.uni_passau.fim.blackBoxTests.util.Selenium;

/**
 * Blackbox-test class for the medium-search functionality. Requires that the tested medium already exists
 * in the data store.
 *
 * @author Ivan Charviakou
 */
public class T90 {

    private WebDriver driver;
    private WebDriverWait waiter;
    private String threadName = "";
    private boolean isMultiThreaded = false;

    private static final String SEARCH_STR = "JSF: Durch";
    private static final String MEDIUM_TITLE = "JSF: Durch Nacht zum Licht.";

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
     * Performs a medium-search using preset keywords and attempts to match the full medium title to
     * an entry in the search result list.
     */
    @Test
    public void t90() {

        // Input text and submit
        String searchBarId = "form_medium_search_header:input_medium_search_term_header";
        Selenium.typeInto(waiter, searchBarId, SEARCH_STR, Keys.RETURN);

        // Check that the correct result entry exists
        Selenium.waitUntilLoaded();
        assertTrue(Selenium.classEntityContainsText(driver, "searchResultTitleEntry", MEDIUM_TITLE));

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
