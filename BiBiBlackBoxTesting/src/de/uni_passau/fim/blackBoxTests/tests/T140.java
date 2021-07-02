package de.uni_passau.fim.blackBoxTests.tests;

import de.uni_passau.fim.blackBoxTests.util.UrlPrefix;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import de.uni_passau.fim.blackBoxTests.util.Driver;
import static de.uni_passau.fim.blackBoxTests.util.UrlPrefix.BASE_URL;
import static org.junit.Assert.assertTrue;
import java.util.concurrent.TimeUnit;

public class T140 {

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
			TimeUnit.SECONDS.sleep(3);
		} catch (InterruptedException e) {
		}

		driver.get(UrlPrefix.BASE_URL + "/view/staff/lending.xhtml");
		try {
			TimeUnit.SECONDS.sleep(1);
		} catch (InterruptedException e) {
		}
		try {
			TimeUnit.SECONDS.sleep(3);
		} catch (InterruptedException e) {
		}
		driver.findElement(By.id("form_lending:lending_mail_field")).click();
		driver.findElement(By.id("form_lending:lending_mail_field")).sendKeys("nutzer.sep2021test@gmail.com" + Keys.ENTER);
		try {
			TimeUnit.SECONDS.sleep(4);
		} catch (InterruptedException e) {
		}
		driver.findElement(By.id("form_lending:button_direct_lend_copies")).click();
		try {
			TimeUnit.SECONDS.sleep(4);
		} catch (InterruptedException e) {
		}
		assertTrue(driver.getPageSource().contains("verliehen"));
	}

	@After
	public void tearDown() {}

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
