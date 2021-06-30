package de.uni_passau.fim.blackBoxTests.tests;

import de.uni_passau.fim.blackBoxTests.test_suite.Driver;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class T60 {
	
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
		//navigates to category-browser
		waiter.until(ExpectedConditions.visibilityOfElementLocated(By.id("category-browser")));
		waiter.until(ExpectedConditions.visibilityOfElementLocated(By.id("category-browser"))).click();

		//navigate to SampleParentCategory
		waiter.until(ExpectedConditions.visibilityOfElementLocated(By.id("j_id2:link_category_name_1")));
		waiter.until(ExpectedConditions.visibilityOfElementLocated(By.id("j_id2:link_category_name_1"))).click();

		//navigate to category creator
		waiter.until(ExpectedConditions.visibilityOfElementLocated(By.id("form_category_controls:button_create_category")));
		waiter.until(ExpectedConditions.visibilityOfElementLocated(By.id("form_category_controls:button_create_category"))).click();

		//sets attributes
		driver.findElement(By.id("form:categoryName")).sendKeys("Informatik");
		driver.findElement(By.id("form:categoryDescription")).sendKeys("Alle Medien zu Informatik");

		//create
		waiter.until(ExpectedConditions.visibilityOfElementLocated(By.id("form:button_save")));
		waiter.until(ExpectedConditions.visibilityOfElementLocated(By.id("form:button_save"))).click();
		try { TimeUnit.SECONDS.sleep(4); } catch (InterruptedException e){}
        try {
			//checks a result
			assertTrue(driver.getPageSource().contains("Die Kategorie ist erfolgreich erstellt."));
        } catch (Exception e) {
        	fail("A positive message not found.");
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
