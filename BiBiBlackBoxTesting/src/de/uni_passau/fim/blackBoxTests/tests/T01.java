package de.uni_passau.fim.blackBoxTests.tests;

import static de.uni_passau.fim.blackBoxTests.util.UrlPrefix.BASE_URL;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import de.uni_passau.fim.blackBoxTests.util.Driver;

public class T01 {
	
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
        driver.get(BASE_URL);
    }
    
    @Test
    public void doTest() {
        //Ist ein Title "BiBi"?
        waiter.until(ExpectedConditions.titleContains("BiBi"));

        //Navigation zur Anmeldung
        waiter.until(ExpectedConditions.visibilityOfElementLocated(By.id("signIn")));
        waiter.until(ExpectedConditions.visibilityOfElementLocated(By.id("signIn"))).click();

        //Sehe ich einen Anmeldung-Button?
        try {
            driver.findElement(By.id("login_form:login_login_button"));
            assertTrue(true);
        } catch (Exception e) {
            fail("Element not found.");
        }
    }

    @AfterClass
    public static void tearDown() {}
    
	public WebDriver getDriver() {
		return driver;
	}

	public void setDriver(WebDriver driver) {
		T01.driver = driver;
	}

	public WebDriverWait getWaiter() {
		return waiter;
	}

	public void setWaiter(WebDriverWait waiter) {
		T01.waiter = waiter;
	}

	public String getThreadName() {
		return threadName;
	}

	public void setThreadName(String threadName) {
		T01.threadName = threadName;
	}
	
	public boolean isMultiThreaded() {
		return isMultiThreaded;
	}

	public void setMultiThreaded(boolean isMultiThreaded) {
		T01.isMultiThreaded = isMultiThreaded;
	}

}
