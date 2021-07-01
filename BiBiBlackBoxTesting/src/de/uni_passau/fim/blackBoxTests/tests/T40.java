package de.uni_passau.fim.blackBoxTests.tests;

import de.uni_passau.fim.blackBoxTests.util.Driver;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import static org.junit.Assert.*;

public class T40 {
	
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
        try {
			//navigates to medium creator
			waiter.until(ExpectedConditions.visibilityOfElementLocated(By.id("header_staff_dropdown")));
			waiter.until(ExpectedConditions.visibilityOfElementLocated(By.id("header_staff_dropdown"))).click();
			waiter.until(ExpectedConditions.visibilityOfElementLocated(By.id("medium-creator")));
			waiter.until(ExpectedConditions.visibilityOfElementLocated(By.id("medium-creator"))).click();

			//sets a medium's attributes
			driver.findElement(By.id("mediumForm:mediumTitle")).sendKeys("Programmieren lernen" + threadName);
			driver.findElement(By.id("mediumForm:author1")).sendKeys("Mustermann" + threadName);
			driver.findElement(By.id("mediumForm:mediumType")).sendKeys("Buch" + threadName);
			driver.findElement(By.id("mediumForm:mediumEdition")).sendKeys("1.0" + threadName);
			driver.findElement(By.id("mediumForm:mediumPublisher")).sendKeys("Springer" + threadName);
			driver.findElement(By.id("mediumForm:releaseYear")).clear();
			driver.findElement(By.id("mediumForm:releaseYear")).sendKeys("2020" + threadName);
			driver.findElement(By.id("mediumForm:releaseYear")).sendKeys("2020" + threadName);
			driver.findElement(By.id("mediumForm:isbn")).sendKeys("17RE" + threadName);
			driver.findElement(By.id("mediumForm:copyLocation")).sendKeys("FIM" + threadName);
			driver.findElement(By.id("mediumForm:copySignature")).sendKeys("17RE (1)" + threadName);

			//create
			waiter.until(ExpectedConditions.visibilityOfElementLocated(By.id("mediumForm:medium-create-btn")));
			waiter.until(ExpectedConditions.visibilityOfElementLocated(By.id("mediumForm:medium-create-btn"))).click();

			//checks a result
			assertTrue(driver.getPageSource().contains("Medium erfolgreich erstellt"));
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
