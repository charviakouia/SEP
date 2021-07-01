package de.uni_passau.fim.blackBoxTests.tests;

import de.uni_passau.fim.blackBoxTests.util.Driver;
import de.uni_passau.fim.blackBoxTests.util.Pages;
import de.uni_passau.fim.blackBoxTests.util.Selenium;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import static org.junit.Assert.*;

/**
 * Blackbox-test class for the copy-booking functionality. Requires that the given user is registered and logged in.
 *
 * @author Ivan Charviakou
 */
public class T120 {

    private WebDriver driver;
    private WebDriverWait waiter;
    private JavascriptExecutor jsexec;
    private String threadName = "";
    private boolean isMultiThreaded = false;

    private final String MEDIUM_NAME = "Programmieren lernen" + threadName;
    private final String SIGNATURE = "17RE (1)" + threadName;
    private final String AVAILABILITY = "Bereit zur Abholung";

    @BeforeClass
    public void setUp() {
        if (!isMultiThreaded) {
            driver = Driver.getDriver();
            waiter = Driver.getDriverWait();
            jsexec = Driver.getJavascriptExecutor();
        }
        Selenium.navigateTo(driver, Pages.HOME);
    }

    @AfterClass
    public void tearDown() {}

    /**
     * Performs a medium search using the preset data values, books a copy, and verifies that the copy has been
     * successfully booked.
     */
    @Test
    public void t120() {

        // Navigate to advanced search and perform search
        Selenium.clickOn(waiter, "advancedSearchLink");
        String searchBarId = "form_medium_search:input_general_search_term";
        Selenium.typeInto(waiter, searchBarId, MEDIUM_NAME, Keys.ENTER);

        // Check that the correct result exists
        Selenium.waitUntilLoaded();
        String titleLinkClass = "searchResultTitleEntry";
        WebElement linkElement = Selenium.getClassEntityContainingText(driver, titleLinkClass, MEDIUM_NAME);
        assertNotNull(linkElement);

        // Navigate to medium details page
        String link = linkElement.getAttribute("href");
        driver.navigate().to(link);

        // Book a medium-copy
        waiter.until(ExpectedConditions.visibilityOfElementLocated(By.id("pickUpForm")));
        Selenium.typeInto(waiter, "pickUpForm:signatureInputField", SIGNATURE);
        Selenium.clickOn(waiter, "pickUpForm:copyPickup");

        // Check that the medium-copy exists
        Selenium.waitUntilLoaded();
        String copyClass = "copyListSignatures";
        WebElement copyTableRow = Selenium.getClassEntityWithText(driver, copyClass, SIGNATURE);
        assertNotNull(copyTableRow);

        // Check that the medium-copy has been booked
        String jsStmt = "return arguments[0].parentNode.parentNode.children[2];";
        WebElement availability = (WebElement) jsexec.executeScript(jsStmt, copyTableRow);
        Assert.assertEquals(AVAILABILITY, availability.getText());

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

	public JavascriptExecutor getJsexec() {
		return jsexec;
	}

	public void setJsexec(JavascriptExecutor jsexec) {
		this.jsexec = jsexec;
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
    
   

    
}
