package de.uni_passau.fim.blackBoxTests.tests;

import de.uni_passau.fim.blackBoxTests.util.Driver;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import static de.uni_passau.fim.blackBoxTests.util.UrlPrefix.BASE_URL;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class T02 {
	
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
        driver.get(BASE_URL + "/view/ffa/login.xhtml");
    }

    @Test
    public void doTest() {

        //Hier melde ich mich
        waiter.until(ExpectedConditions.visibilityOfElementLocated(By.id("login_form:login_email_field")));
        waiter.until(ExpectedConditions.visibilityOfElementLocated(By.id("login_form:login_email_field"))).clear();
        waiter.until(ExpectedConditions.visibilityOfElementLocated(By.id("login_form:login_email_field")))
                .sendKeys("pravdin@fim.uni-passau.de");
        waiter.until(ExpectedConditions.visibilityOfElementLocated(By.id("login_form:login_password_field")));
        waiter.until(ExpectedConditions.visibilityOfElementLocated(By.id("login_form:login_password_field"))).clear();
        waiter.until(ExpectedConditions.visibilityOfElementLocated(By.id("login_form:login_password_field")))
                .sendKeys("Password1" + Keys.ENTER);

        //Hier navigiere ich mich zur Profil-Seite
        waiter.until(ExpectedConditions.visibilityOfElementLocated(By.id("accountDropDown")));
        waiter.until(ExpectedConditions.visibilityOfElementLocated(By.id("accountDropDown"))).click();
        waiter.until(ExpectedConditions.visibilityOfElementLocated(By.id("prfl")));
        waiter.until(ExpectedConditions.visibilityOfElementLocated(By.id("prfl"))).click();

        //Hier will ich checken, ob meine Vorname gezeigt wird
        try {
            driver.findElement(By.id("form_profile:frstname"));
            assertTrue(true);
            System.out.println("Test T02 succeeded (thread %s)".formatted(threadName));
        } catch (Exception e) {
            fail("Element not found.");
        }
    }

    @AfterClass
    public static void tearDown() {}
    
    public WebDriver getDriver() {
		return driver;
	}
    
	public boolean isMultiThreaded() {
		return isMultiThreaded;
	}

	public void setMultiThreaded(boolean isMultiThreaded) {
		T02.isMultiThreaded = isMultiThreaded;
	}


	public void setDriver(WebDriver driver) {
		T02.driver = driver;
	}

	public WebDriverWait getWaiter() {
		return waiter;
	}

	public void setWaiter(WebDriverWait waiter) {
		T02.waiter = waiter;
	}

	public String getThreadName() {
		return threadName;
	}

	public void setThreadName(String threadName) {
		T02.threadName = threadName;
	}

}
