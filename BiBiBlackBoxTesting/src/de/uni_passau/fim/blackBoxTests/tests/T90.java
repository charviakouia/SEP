package de.uni_passau.fim.blackBoxTests.tests;

import static org.junit.Assert.assertTrue;

import org.junit.*;
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

    private static final String SEARCH_STR = "nen%s";
    private static final String MEDIUM_TITLE = "Programmieren lernen";

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
     * Performs a medium-search using preset keywords and attempts to match the full medium title to
     * an entry in the search result list.
     */
    @Test
    public void t90() {

        // Input text and submit
        String searchBarId = "form_medium_search_header:input_medium_search_term_header";
        Selenium.typeInto(waiter, searchBarId, gen(SEARCH_STR), Keys.RETURN);

        // Check that the correct result entry exists
        Selenium.waitUntilLoaded();
        assertTrue(Selenium.classEntityContainsText(driver, "searchResultTitleEntry", gen(MEDIUM_TITLE)));

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
