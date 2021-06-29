package de.uni_passau.fim.blackBoxTests.tests;

import de.uni_passau.fim.blackBoxTests.test_suite.Driver;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import static de.uni_passau.fim.blackBoxTests.test_suite.UrlPrefix.BASE_URL;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class T09 {

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
			waiter.until(ExpectedConditions.visibilityOfElementLocated(By.id("login_form:login_email_field")));
			waiter.until(ExpectedConditions.visibilityOfElementLocated(By.id("login_form:login_email_field"))).clear();
			waiter.until(ExpectedConditions.visibilityOfElementLocated(By.id("login_form:login_email_field")))
					.sendKeys("admin.sep2021.test@gmail.com");
			waiter.until(ExpectedConditions.visibilityOfElementLocated(By.id("login_form:login_password_field")));
			waiter.until(ExpectedConditions.visibilityOfElementLocated(By.id("login_form:login_password_field"))).clear();
			waiter.until(ExpectedConditions.visibilityOfElementLocated(By.id("login_form:login_password_field")))
					.sendKeys("wrongPassword1" + Keys.ENTER);
			assertTrue(waiter.until(ExpectedConditions.titleContains("Anmeldung")));
			waiter.until(ExpectedConditions.visibilityOfElementLocated(By.id("login_form:login_password_message")));
			assertTrue(true);
		} catch (Exception e) {
			fail("Error message not found");
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
