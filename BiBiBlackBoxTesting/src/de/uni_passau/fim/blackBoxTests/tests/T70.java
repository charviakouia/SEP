package de.uni_passau.fim.blackBoxTests.tests;

import de.uni_passau.fim.blackBoxTests.util.Driver;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class T70 {
	
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
    }
    
    @Test
    public void doTest() {
		//search
		driver.findElement(By.id("form_medium_search_header:input_medium_search_term_header"))
				.sendKeys("Programmieren lernen" + threadName + Keys.ENTER);

		//navigate
		waiter.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("[id$=mediumLink]")));
		waiter.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("[id$=mediumLink]"))).click();

		//edit a category of the medium
		driver.findElement(By.id("form_mediumAttributes_forUsers:category")).clear();
		driver.findElement(By.id("form_mediumAttributes_forUsers:category"))
				.sendKeys("Informatik" + threadName);

		//save
		waiter.until(ExpectedConditions.visibilityOfElementLocated(By.id("form_mediumAttributes_forUsers:saveMedium")));
		waiter.until(ExpectedConditions.visibilityOfElementLocated(By.id("form_mediumAttributes_forUsers:saveMedium"))).click();

        try {
			//checks a result
			assertTrue(driver.getPageSource().contains("Das Medium ist erfolgreich aktualisiert worden."));

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
