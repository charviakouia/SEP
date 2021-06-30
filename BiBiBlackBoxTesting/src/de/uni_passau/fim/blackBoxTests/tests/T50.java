package de.uni_passau.fim.blackBoxTests.tests;

import de.uni_passau.fim.blackBoxTests.test_suite.Driver;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import static de.uni_passau.fim.blackBoxTests.test_suite.UrlPrefix.BASE_URL;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class T50 {
	
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
			//sets a new copy's attributes
			driver.findElement(By.id("createCopy:newCopyLocation")).sendKeys("FIM");
			driver.findElement(By.id("createCopy:newCopySignature")).sendKeys("17RE (2)");
		} catch (Exception e) {
			fail("Attributen wurden erfolglos angegeben.");
		}
        try {
			//create
			waiter.until(ExpectedConditions.visibilityOfElementLocated(By.id("createCopy:createCopyButton")));
			waiter.until(ExpectedConditions.visibilityOfElementLocated(By.id("createCopy:createCopyButton"))).click();
		} catch (Exception e) {
			fail("Create-Button wurde nicht gefunden.");
		}
        try {
			//checks a result
			assertTrue(driver.getPageSource().contains("Das Exemplar wurde erfolgreich erstellt."));
        } catch (Exception e) {
        	fail("A new signature not found.");
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
