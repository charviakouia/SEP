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

    private static WebDriver driver;
    private static WebDriverWait waiter;
    private static String threadName = "";
    private static boolean isMultiThreaded = false;

    private static final String SEARCH_STR = "JSF: Durch";
    private static final String MEDIUM_TITLE = "JSF: Durch Nacht zum Licht.";

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

    public static String getThreadName() {
        return threadName;
    }

    public static void setThreadName(String threadName) {
        T90.threadName = threadName;
    }

    public static boolean isMultiThreaded() {
        return isMultiThreaded;
    }

    public static void setMultiThreaded(boolean isMultiThreaded) {
        T90.isMultiThreaded = isMultiThreaded;
    }

	public static WebDriver getDriver() {
		return driver;
	}

	public static void setDriver(WebDriver driver) {
		T90.driver = driver;
	}

	public static WebDriverWait getWaiter() {
		return waiter;
	}

	public static void setWaiter(WebDriverWait waiter) {
		T90.waiter = waiter;
	}
    
    

}
