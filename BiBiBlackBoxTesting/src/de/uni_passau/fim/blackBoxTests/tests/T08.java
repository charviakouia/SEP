package de.uni_passau.fim.blackBoxTests.tests;

import de.uni_passau.fim.blackBoxTests.util.Driver;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import static de.uni_passau.fim.blackBoxTests.util.UrlPrefix.BASE_URL;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class T08 {

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
		driver.get(BASE_URL + "view/admin/administration.xhtml");
        try {
        	assertTrue(waiter.until(ExpectedConditions.titleContains("Anmeldung")));
			assertTrue(driver.getPageSource().contains("Loggen Sie sich zuerst ein " +
					"oder registrieren Sie einen neuen Account!"));
        } catch (Exception e) {
        	fail("Login not found in header");
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
