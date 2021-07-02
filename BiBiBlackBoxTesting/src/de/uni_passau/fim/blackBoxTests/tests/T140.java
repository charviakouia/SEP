package de.uni_passau.fim.blackBoxTests.tests;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
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
		waiter.until(ExpectedConditions.visibilityOfElementLocated(By.id("header_staff_dropdown")));
		driver.findElement(By.id("header_staff_dropdown")).click();
		waiter.until(ExpectedConditions.visibilityOfElementLocated(By.linkText("Ausleihe")));
		driver.findElement(By.linkText("Ausleihe")).click();
		waiter.until(ExpectedConditions.visibilityOfElementLocated(By.id("form_lending:lending_mail_field")));
		driver.findElement(By.id("form_lending:lending_mail_field")).click();
<<<<<<< HEAD
		driver.findElement(By.id("form_lending:lending_mail_field"))
				.sendKeys("nutzer.sep2021test" + threadName + "@gmail.com");
		driver.findElement(By.id("form_lending:j_idt52:0:lending_signature_field")).click();
		driver.findElement(By.id("form_lending:j_idt52:0:lending_signature_field"))
				.sendKeys("17RE (1)" + threadName);
=======
		driver.findElement(By.id("form_lending:lending_mail_field")).sendKeys("nutzer.sep2021test" + threadName + "@gmail.com");
<<<<<<< HEAD
		try {
			TimeUnit.MILLISECONDS.sleep(500);
		} catch (InterruptedException e) {
		}
		driver.findElement(By.id("form_lending:j_idt51:0:lending_signature_field")).click();
		try {
			TimeUnit.MILLISECONDS.sleep(500);
		} catch (InterruptedException e) {
		}
		driver.findElement(By.id("form_lending:j_idt51:0:lending_signature_field")).sendKeys("17RE (1)" + threadName);
		try {
			TimeUnit.MILLISECONDS.sleep(500);
		} catch (InterruptedException e) {
		}
=======
		
		driver.findElement(By.id("form_lending:j_idt52:0:lending_signature_field")).click();
		driver.findElement(By.id("form_lending:j_idt52:0:lending_signature_field")).sendKeys("17RE (1)" + threadName);
>>>>>>> origin/master
>>>>>>> origin/ivansBranch
		driver.findElement(By.id("form_lending:button_direct_lend_copies")).click();
		try {
			TimeUnit.SECONDS.sleep(2);
		} catch (InterruptedException e) {
		}
<<<<<<< HEAD

=======
<<<<<<< HEAD

		assertTrue(driver.getPageSource()
				.contains("1 Exemplar(e) an nutzer.sep2021test" + threadName + "@gmail.com verliehen."));
=======
>>>>>>> origin/ivansBranch
		assertTrue(driver.getPageSource().contains("1 Exemplar(e) an nutzer.sep2021test" + threadName + "@gmail.com verliehen."));
		System.out.println("Test T140 succeeded (thread %s)".formatted(threadName));
>>>>>>> origin/master
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
