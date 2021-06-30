package de.uni_passau.fim.blackBoxTests.tests;

import de.uni_passau.fim.blackBoxTests.test_suite.Driver;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import static de.uni_passau.fim.blackBoxTests.test_suite.UrlPrefix.BASE_URL;
import static org.junit.Assert.*;

public class T30 {
	
	private WebDriver driver;
	private WebDriverWait waiter;
	private String threadName;
	private boolean isMultiThreaded = false;
		    
    @Before
    public void setUp() {
    	if (!isMultiThreaded) {
    		driver = Driver.getDriver();
    		waiter = Driver.getDriverWait();
    	}
    }
    
    @Test
    public void doTest() {
        try {
			//navigates to profile
			waiter.until(ExpectedConditions.visibilityOfElementLocated(By.id("accountDropDown")));
			waiter.until(ExpectedConditions.visibilityOfElementLocated(By.id("accountDropDown"))).click();
			waiter.until(ExpectedConditions.visibilityOfElementLocated(By.id("prfl")));
			waiter.until(ExpectedConditions.visibilityOfElementLocated(By.id("prfl"))).click();

			//sets a new surname
			driver.findElement(By.id("form_profile:lastname")).clear();
			driver.findElement(By.id("form_profile:lastname")).sendKeys("Müller"
					+ Keys.ENTER);

			//checks a result
			assertEquals("Müller", driver.findElement(By.id("form_profile:lastname"))
					.getAttribute("value"));
        } catch (Exception e) {
        	fail("A new surname not found.");
        }
    }
    
    @After
    public void tearDown() {}
    
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
