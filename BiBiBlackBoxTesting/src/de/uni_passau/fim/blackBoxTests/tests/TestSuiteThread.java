package de.uni_passau.fim.blackBoxTests.tests;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import static de.uni_passau.fim.blackBoxTests.util.Driver.TIMEOUT_WAIT;

public class TestSuiteThread extends Thread {
	
	private WebDriver suiteDriver;
	private WebDriverWait suiteWaiter;
	
	public TestSuiteThread(WebDriver driver) {
		suiteDriver = driver;
		suiteWaiter = new WebDriverWait(driver, TIMEOUT_WAIT);
	}
	
	@Override
	public void run() {
		T01 test1 = new T01();
		T02 test2 = new T02();
		test1.setDriver(suiteDriver);
		test1.setWaiter(suiteWaiter);
		test2.setDriver(suiteDriver);
		test2.setWaiter(suiteWaiter);
		test1.setMultiThreaded(true);
		test2.setMultiThreaded(true);
		test1.setThreadName(Thread.currentThread().getName());
		test2.setThreadName(Thread.currentThread().getName());
		test1.setUp();
		test2.setUp();
		try {
			Thread.sleep(1000);
			test1.doTest();
			Thread.sleep(1000);
			test2.doTest();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			System.out.println("Closing web driver for suite with thread name: " + Thread.currentThread().getName());
			suiteDriver.close();
		}
		System.out.println("Finished executing test suite with thread name: " + Thread.currentThread().getName());
		
	}

}
