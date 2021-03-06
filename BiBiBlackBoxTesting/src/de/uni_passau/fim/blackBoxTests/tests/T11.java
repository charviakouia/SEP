package de.uni_passau.fim.blackBoxTests.tests;

import static de.uni_passau.fim.blackBoxTests.util.UrlPrefix.BASE_URL;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import de.uni_passau.fim.blackBoxTests.util.Driver;
import de.uni_passau.fim.blackBoxTests.util.Selenium;

/**
 * Tests admin configuration page.
 * 
 * @author Jonas Picker
 */
public class T11 {
	
	private WebDriver driver;
	private WebDriverWait waiter;
	private String threadName = "";
	private boolean isMultiThreaded = false;
	
	/**
	 * Goes to the start page.
	 */
    @Before
    public void setUp() {
    	if (!isMultiThreaded) {
    		driver = Driver.getDriver();
    		waiter = Driver.getDriverWait();
    	}
        driver.get(BASE_URL);
    }
    
    /**
     * Navigates to settings, sets return period to 1 minute and checks if it
     * was set correctly after reload.
     */
    @Test
    public void doTest() {
        waiter.until(ExpectedConditions.visibilityOfElementLocated(By.id("header_admin_dropdown"))).click();
        waiter.until(ExpectedConditions.visibilityOfElementLocated(By.id("header_admin_configurations"))).click();
        Selenium.waitUntilLoaded();
        waiter.until(ExpectedConditions.visibilityOfElementLocated(By.id("adminForm:administration_return_period"))).clear();
        waiter.until(ExpectedConditions.visibilityOfElementLocated(By.id("adminForm:administration_return_period"))).sendKeys("6.944444444444445E-4" + Keys.ENTER);
        String value = waiter.until(ExpectedConditions.visibilityOfElementLocated(By.id("adminForm:administration_return_period"))).getAttribute("value");
        assertTrue(value.equals("6.944444444444445E-4"));
        System.out.println("Test T11 succeeded (thread %s)".formatted(threadName));
    }
    
    /**
     * No cleanup needed.
     */
    @After
    public void tearDown() {}
    
    //getters and settes below are needed for multithreaded execution.
    public WebDriver getDriver() {
		return driver;
	}
    
	public boolean isMultiThreaded() {
		return isMultiThreaded;
	}

	public void setMultiThreaded(boolean isMultiThreaded) {
		this.isMultiThreaded = isMultiThreaded;
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

	public String getThreadName() {
		return threadName;
	}

	public void setThreadName(String threadName) {
		this.threadName = threadName;
	}
}
