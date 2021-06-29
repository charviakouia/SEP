package de.uni_passau.fim.blackBoxTests.tests;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import static de.uni_passau.fim.blackBoxTests.test_suite.Driver.TIMEOUT_WAIT;

public class TestSuiteThread extends Thread {
	
	private WebDriver suiteDriver;
	private WebDriverWait suiteWaiter;
	
	public TestSuiteThread(WebDriver driver) {
		suiteDriver = driver;
		suiteWaiter = new WebDriverWait(driver, TIMEOUT_WAIT);
	}
	
	@Override
	public void run() {
		T01 test1 = new T01(Thread.currentThread().getName(), suiteDriver, suiteWaiter);
		T02 test2 = new T02(Thread.currentThread().getName(), suiteDriver, suiteWaiter);
		try {
			Thread.sleep(1000);
			test1.execute();
			Thread.sleep(1000);
			test2.execute();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			System.out.println("Closing web driver for suite with thread name: " + Thread.currentThread().getName());
		}
		System.out.println("Finished executing test suite with thread name: " + Thread.currentThread().getName());
		
	}

}
