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

public class T02 extends TestBlueprint{
    
	public T02(String threadName, WebDriver webDriver, WebDriverWait webDriverWait) {
    	super(threadName);
    	driver = webDriver;
    	waiter = webDriverWait;
    	driver.get(BASE_URL + "/view/ffa/login.xhtml");
    }
	

	
    @Before
    public void setUp() {
        driver = Driver.getDriver();
        waiter = Driver.getDriverWait();
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
        } catch (Exception e) {
            fail("Element not found.");
        }
    }
    
    @After
    public void tearDown() {
    }
}