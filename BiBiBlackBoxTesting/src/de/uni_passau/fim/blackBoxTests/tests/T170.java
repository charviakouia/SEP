package de.uni_passau.fim.blackBoxTests.tests;

import static de.uni_passau.fim.blackBoxTests.util.UrlPrefix.BASE_URL;
import static org.junit.Assert.assertTrue;

import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import de.uni_passau.fim.blackBoxTests.util.Driver;

public class T170 {

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
<<<<<<< HEAD
	    driver.findElement(By.id("accountDropDown")).click();
	    try {
			TimeUnit.SECONDS.sleep(1);
		} catch (InterruptedException e) {
		}
	    driver.findElement(By.id("form_log_out:button_log_out")).click();
	    try {
			TimeUnit.SECONDS.sleep(2);
		} catch (InterruptedException e) {
		}
	    driver.findElement(By.id("signIn")).click();
	    try {
			TimeUnit.SECONDS.sleep(1);
		} catch (InterruptedException e) {
		}
	    driver.findElement(By.id("login_form:login_email_field")).click();
	    driver.findElement(By.id("login_form:login_email_field")).sendKeys("liehr@fim.uni-passau.de");
	    driver.findElement(By.id("login_form:login_password_field")).sendKeys("Hallo0");
	    driver.findElement(By.id("login_form:login_login_button")).click();
	    try {
			TimeUnit.SECONDS.sleep(1);
		} catch (InterruptedException e) {
		}
	    driver.findElement(By.id("header_admin_dropdown")).click();
	    driver.findElement(By.id("header_admin_user_search")).click();
	    driver.findElement(By.id("form_user_search:input_user_search_term")).click();
	    driver.findElement(By.id("form_user_search:input_user_search_term")).sendKeys("Bob" + threadName);
	    driver.findElement(By.id("form_user_search:input_user_search_term")).sendKeys(Keys.ENTER);

	    try { TimeUnit.SECONDS.sleep(3); } catch (InterruptedException e) {}
	    assertTrue(driver.getPageSource().contains("nutzer.sep2021test" + threadName + "@gmail.com"));

=======
		waiter.until(ExpectedConditions.visibilityOfElementLocated(By.id("accountDropDown")));
		driver.findElement(By.id("accountDropDown")).click();
		waiter.until(ExpectedConditions.visibilityOfElementLocated(By.id("form_log_out:button_log_out")));
		driver.findElement(By.id("form_log_out:button_log_out")).click();
		waiter.until(ExpectedConditions.visibilityOfElementLocated(By.id("signIn")));
		driver.findElement(By.id("signIn")).click();

		waiter.until(ExpectedConditions.visibilityOfElementLocated(By.id("login_form:login_email_field")));
		driver.findElement(By.id("login_form:login_email_field")).click();
		driver.findElement(By.id("login_form:login_email_field")).sendKeys("liehr@fim.uni-passau.de");
		driver.findElement(By.id("login_form:login_password_field")).sendKeys("Hallo0");
		driver.findElement(By.id("login_form:login_login_button")).click();
		waiter.until(ExpectedConditions.visibilityOfElementLocated(By.id("header_admin_dropdown")));
		driver.findElement(By.id("header_admin_dropdown")).click();
		driver.findElement(By.id("header_admin_user_search")).click();
		driver.findElement(By.id("form_user_search:input_user_search_term")).click();
		driver.findElement(By.id("form_user_search:input_user_search_term"))
				.sendKeys("Bob" + threadName + " Mustermann" + threadName);
		driver.findElement(By.id("form_user_search:input_user_search_term")).sendKeys(Keys.ENTER);
		try {
			TimeUnit.SECONDS.sleep(3);
		} catch (InterruptedException e) {
		}
		assertTrue(driver.getPageSource().contains("nutzer.sep2021test" + threadName + "@gmail.com"));
		System.out.println("Test T170 succeeded (thread %s)".formatted(threadName));
>>>>>>> origin/master
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
