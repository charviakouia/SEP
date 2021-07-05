package de.uni_passau.fim.blackBoxTests.tests;

import static de.uni_passau.fim.blackBoxTests.util.UrlPrefix.BASE_URL;
import static org.junit.Assert.assertTrue;

import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import de.uni_passau.fim.blackBoxTests.util.Driver;

public class T160 {

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
		try {
			TimeUnit.MINUTES.sleep(1);
		} catch (InterruptedException e) {
		}
		waiter.until(ExpectedConditions.visibilityOfElementLocated(By.id("header_staff_dropdown")));
		driver.findElement(By.id("header_staff_dropdown")).click();
		waiter.until(ExpectedConditions.visibilityOfElementLocated(By.linkText("Rückgabe")));
		driver.findElement(By.linkText("Rückgabe")).click();
		waiter.until(ExpectedConditions.visibilityOfElementLocated(By.id("form_return_form:returnForm_mail_field")));
		driver.findElement(By.id("form_return_form:returnForm_mail_field")).click();
		driver.findElement(By.id("form_return_form:returnForm_mail_field")).sendKeys("nutzer.sep2021test" + threadName + "@gmail.com");
		driver.findElement(By.id("form_return_form:j_idt53:0:returnForm_signature_field")).click();
		driver.findElement(By.id("form_return_form:j_idt53:0:returnForm_signature_field")).sendKeys("17RE (1)" + threadName);
		driver.findElement(By.id("form_return_form:button_return_copies")).click();
		try {
			TimeUnit.SECONDS.sleep(4);
		} catch (InterruptedException e) {
		}

		assertTrue(driver.getPageSource().contains("1 Exemplare von nutzer.sep2021test" + threadName + "@gmail.com zurückgegeben."));
		System.out.println("Test T160 succeeded (thread %s)".formatted(threadName));
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
