package de.uni_passau.fim.blackBoxTests.tests;

import static org.junit.Assert.assertTrue;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import de.uni_passau.fim.blackBoxTests.util.Driver;

public class T130 {

    private static WebDriver driver;
    private static WebDriverWait waiter;
    private static String threadName = "";
    private static boolean isMultiThreaded = false;

    @BeforeClass
    public static void setUp() {
    	if (!isMultiThreaded) {
    		driver = Driver.getDriver();
    		waiter = Driver.getDriverWait();
    	}
    }

    @AfterClass
    public void tearDown() {}

    @Test
    public void t130() {
        // Log out
        waiter.until(ExpectedConditions.visibilityOfElementLocated(By.id("accountDropDown"))).click();
        waiter.until(ExpectedConditions.visibilityOfElementLocated(By.id("form_log_out:button_log_out"))).click();

        // Log in
        waiter.until(ExpectedConditions.visibilityOfElementLocated(By.id("login_form:login_email_field"))).
                sendKeys("mitarbeiter.sep2021test" + threadName + "@gmail.com");
        driver.findElement(By.id("login_form:login_password_field")).sendKeys("sijAs13!!A" + Keys.ENTER);

        // Navigate to the list of copies that users have marked for pick up
        waiter.until(ExpectedConditions.visibilityOfElementLocated(By.id("header_staff_dropdown")));
        waiter.until(ExpectedConditions.visibilityOfElementLocated(By.id("header_staff_dropdown"))).click();
        waiter.until(ExpectedConditions.visibilityOfElementLocated(By.id("copiesReadyForPickUp")));
        waiter.until(ExpectedConditions.visibilityOfElementLocated(By.id("copiesReadyForPickUp"))).click();

        // Verify data in page
        assertTrue(driver.getPageSource().contains("17RE (1)" + threadName));
    }

	public WebDriver getDriver() {
		return driver;
	}

	public void setDriver(WebDriver driver) {
		T130.driver = driver;
	}

	public WebDriverWait getWaiter() {
		return waiter;
	}

	public void setWaiter(WebDriverWait waiter) {
		T130.waiter = waiter;
	}

	public String getThreadName() {
		return threadName;
	}

	public void setThreadName(String threadName) {
		T130.threadName = threadName;
	}

	public boolean isMultiThreaded() {
		return isMultiThreaded;
	}

	public void setMultiThreaded(boolean isMultiThreaded) {
		T130.isMultiThreaded = isMultiThreaded;
	}
    
    
}