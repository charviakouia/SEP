package de.uni_passau.fim.blackBoxTests.tests;

import de.uni_passau.fim.blackBoxTests.util.Driver;
import de.uni_passau.fim.blackBoxTests.util.Pages;
import de.uni_passau.fim.blackBoxTests.util.Selenium;
import org.junit.*;
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

    private final String MEDIUM_NAME = "Programmieren lernen%s";
    private final String SIGNATURE = "17RE (1)%s";
    private final String AVAILABILITY = "Bereit zur Abholung";

    @Before
    public void setUp() {
        if (!isMultiThreaded) {
            driver = Driver.getDriver();
            waiter = Driver.getDriverWait();
            jsexec = Driver.getJavascriptExecutor();
        }
        Selenium.navigateTo(driver, Pages.HOME);
    }

    @After
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
        Selenium.typeInto(waiter, searchBarId, gen(MEDIUM_NAME), Keys.ENTER);

        // Check that the correct result exists
        Selenium.waitUntilLoaded();
        String titleLinkClass = "searchResultTitleEntry";
        WebElement linkElement = Selenium.getClassEntityContainingText(driver, titleLinkClass, gen(MEDIUM_NAME));
        assertNotNull(linkElement);

        // Navigate to medium details page
        String link = linkElement.getAttribute("href");
        driver.navigate().to(link);

        // Book a medium-copy
        waiter.until(ExpectedConditions.visibilityOfElementLocated(By.id("pickUpForm")));
        Selenium.typeInto(waiter, "pickUpForm:signatureInputField", gen(SIGNATURE));
        Selenium.clickOn(waiter, "pickUpForm:copyPickup");

        // Check that the medium-copy exists
        Selenium.waitUntilLoaded();
        String copyClass = "copyListSignatures";
        WebElement copyTableRow = Selenium.getClassEntityWithText(driver, copyClass, gen(SIGNATURE));
        assertNotNull(copyTableRow);

        // Check that the medium-copy has been booked
        String jsStmt = "return arguments[0].parentNode.parentNode.children[2];";
        WebElement availability = (WebElement) jsexec.executeScript(jsStmt, copyTableRow);
        Assert.assertEquals(AVAILABILITY, availability.getText());

    }

    private String gen(String str){
        return String.format(str, threadName);
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
