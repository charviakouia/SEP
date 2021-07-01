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
		String threadName = Thread.currentThread().getName();
		test1.setThreadName(threadName);
		test2.setThreadName(threadName);
		test1.setUp();
		test2.setUp();
		try {
			Thread.sleep(1000);
			long starttime = System.currentTimeMillis();
			System.out.println("TestSuite Thread with name: " + threadName + "started executing at: " + starttime);
			
			test1.doTest();
			test2.doTest();
			
			long stoptime = System.currentTimeMillis();
			System.out.println("Finished executing test suite with thread name: " + threadName + " at time: " + stoptime);
		} catch (InterruptedException e) {
			e.printStackTrace();
			System.out.println("Thread with name: " + threadName + " was interrupted from its sleep.");
		} finally {
			System.out.println("Closing web driver for suite with thread name: " + threadName);
			suiteDriver.close();
		}
		
		
	}

}
