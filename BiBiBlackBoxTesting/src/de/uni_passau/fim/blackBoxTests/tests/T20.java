package de.uni_passau.fim.blackBoxTests.tests;

import static de.uni_passau.fim.blackBoxTests.test_suite.UrlPrefix.BASE_URL;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import de.uni_passau.fim.blackBoxTests.test_suite.Driver;

public class T20 {
	
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
        driver.get(BASE_URL);
    }
    
    @Test
    public void doTest() {
    	waiter.until(ExpectedConditions.visibilityOfElementLocated(By.id("header_admin_dropdown"))).click();
    	driver.findElement(By.id("header_admin_configurations")).click();
        driver.findElement(By.id("administration_registration_link")).click();
        driver.findElement(By.id("registrationForm:firstName")).sendKeys("Tom");
        driver.findElement(By.id("registrationForm:lastName")).sendKeys("Mustermann");
        driver.findElement(By.id("registrationForm:password")).sendKeys("sijAs13!!A");
        driver.findElement(By.id("registrationForm:confirmedPassword")).sendKeys("sijAs13!!A");
        driver.findElement(By.id("registrationForm:email")).sendKeys("mitarbeiter.sep2021test@gmail.com");
        driver.findElement(By.id("registrationForm:zip")).sendKeys("94032");
        driver.findElement(By.id("registrationForm:street")).sendKeys("Innstraße");
        driver.findElement(By.id("registrationForm:city")).sendKeys("Passau");
        WebElement dropDownListBox = driver.findElement(By.id("registrationForm:roleChoice"));
        Select clickThis = new Select(dropDownListBox);
        clickThis.selectByValue("STAFF");
        driver.findElement(By.id("registrationForm:streetNumber")).sendKeys("33");
        driver.findElement(By.id("registrationForm:registration_save_button")).click();
        waiter.until(ExpectedConditions.visibilityOfElementLocated(By.id("accountDropDown"))).click();
        driver.findElement(By.id("form_log_out:button_log_out")).click();
        waiter.until(ExpectedConditions.visibilityOfElementLocated(By.id("login_form:login_email_field"))).sendKeys("mitarbeiter.sep2021test@gmail.com");
        driver.findElement(By.id("login_form:login_password_field")).sendKeys("sijAs13!!A" + Keys.ENTER);
        try {
        	waiter.until(ExpectedConditions.visibilityOfElementLocated(By.id("header_staff_dropdown")));
        	assertTrue(true);
        } catch (Exception e) {
        	fail("Staff dropdown menue not found after login.");
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
