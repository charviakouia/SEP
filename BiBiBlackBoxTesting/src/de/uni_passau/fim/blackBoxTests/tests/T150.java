package de.uni_passau.fim.blackBoxTests.tests;

import static de.uni_passau.fim.blackBoxTests.util.UrlPrefix.BASE_URL;
import static org.junit.Assert.assertTrue;

import java.util.concurrent.TimeUnit;

import de.uni_passau.fim.blackBoxTests.util.Selenium;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import de.uni_passau.fim.blackBoxTests.util.Driver;

public class T150 {
	
	private WebDriver driver;
	private WebDriverWait waiter;
	private String threadName = "";
	private boolean isMultiThreaded = false;

	@Before
	public void setUp() {
		if (!isMultiThreaded) {
			driver = Driver.getDriver();
			waiter = Driver.getDriverWait();
		}
		driver.get(BASE_URL);
	}

	@Test
	public void doTest() {

		// Wait for the medium to become overdue
		try { TimeUnit.MINUTES.sleep(2); } catch (InterruptedException ignored){}

		// Navigate to violations page
		driver.findElement(By.id("header_staff_dropdown")).click();
		try { TimeUnit.SECONDS.sleep(1); } catch (InterruptedException e) { }
	    driver.findElement(By.linkText("Verstöße")).click();
	    try { TimeUnit.SECONDS.sleep(3); } catch (InterruptedException e) { }

	    // Check that correct user appears
	    assertTrue(driver.getPageSource().contains("nutzer.sep2021test" + threadName + "@gmail.com"));
	}

	@After
	public void tearDown() {
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
