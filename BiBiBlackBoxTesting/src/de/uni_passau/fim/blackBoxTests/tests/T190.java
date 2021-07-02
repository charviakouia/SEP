package de.uni_passau.fim.blackBoxTests.tests;

import static de.uni_passau.fim.blackBoxTests.util.UrlPrefix.BASE_URL;
import static org.junit.Assert.assertTrue;

import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import de.uni_passau.fim.blackBoxTests.util.Driver;

public class T190 {

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
		driver.findElement(By.id("site_notice_link")).click();
		try {
			TimeUnit.SECONDS.sleep(1);
		} catch (InterruptedException e) {
		}
		driver.findElement(By.id("site_notice_form:site_notice_text_area")).click();
		driver.findElement(By.id("site_notice_form:site_notice_text_area"))
				.sendKeys("Team 1\\n\\nUni Passau\\nInnstraße 33\\n94032 Passau\\nGermany");
		driver.findElement(By.id("site_notice_form:site_notice_save_changes")).click();
		try {
			TimeUnit.SECONDS.sleep(2);
		} catch (InterruptedException e) {
		}

		assertTrue(driver.getPageSource().contains("Germany"));
		System.out.println("Test T190 succeeded (thread %s)".formatted(threadName));
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
