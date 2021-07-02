package de.uni_passau.fim.blackBoxTests.tests;

import static de.uni_passau.fim.blackBoxTests.util.Driver.TIMEOUT_WAIT;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

public class TestSuiteThread extends Thread {
	
	private WebDriver suiteDriver;
	private WebDriverWait suiteWaiter;
	private JavascriptExecutor exec;
	
	public TestSuiteThread(WebDriver driver) {
		suiteDriver = driver;
		suiteWaiter = new WebDriverWait(driver, TIMEOUT_WAIT);
		exec = (JavascriptExecutor) driver; 
	}
	
	@Override
	public void run() {
		String threadName = Thread.currentThread().getName();
		T08 test1 = new T08();
		T09 test2 = new T09();
		T10 test3 = new T10();
		T11 test4 = new T11();
		T20 test5 = new T20();
		T30 test6 = new T30();
		T40 test7 = new T40();
		T50 test8 = new T50();
		T60 test9 = new T60();
		T70 test10 = new T70();
		T80 test11 = new T80();
		T90 test12 = new T90();
		T100 test13 = new T100();
		T110 test14 = new T110();
		T120 test15 = new T120();
		T130 test16 = new T130();
		final var test17 = new T140();
		final var test18 = new T150();
		final var test19 = new T160();
		final var test20 = new T170();
		final var test21 = new T190();
		DatabaseCleaner cleaner = new DatabaseCleaner();
		test1.setMultiThreaded(true);
		test2.setMultiThreaded(true);
		test3.setMultiThreaded(true);
		test4.setMultiThreaded(true);
		test5.setMultiThreaded(true);
		test6.setMultiThreaded(true);
		test7.setMultiThreaded(true);
		test8.setMultiThreaded(true);
		test9.setMultiThreaded(true);
		test10.setMultiThreaded(true);
		test11.setMultiThreaded(true);
		test12.setMultiThreaded(true);
		test13.setMultiThreaded(true);
		test14.setMultiThreaded(true);
		test15.setMultiThreaded(true);
		test16.setMultiThreaded(true);
		test17.setMultiThreaded(true);
		test18.setMultiThreaded(true);
		test19.setMultiThreaded(true);
		test20.setMultiThreaded(true);
		test21.setMultiThreaded(true);
		cleaner.setMultiThreaded(true);
		test1.setThreadName(threadName);
		test2.setThreadName(threadName);
		test3.setThreadName(threadName);
		test4.setThreadName(threadName);
		test5.setThreadName(threadName);
		test6.setThreadName(threadName);
		test7.setThreadName(threadName);
		test8.setThreadName(threadName);
		test9.setThreadName(threadName);
		test10.setThreadName(threadName);
		test11.setThreadName(threadName);
		test12.setThreadName(threadName);
		test13.setThreadName(threadName);
		test14.setThreadName(threadName);
		test15.setThreadName(threadName);
		test16.setThreadName(threadName);
		test17.setThreadName(threadName);
		test18.setThreadName(threadName);
		test19.setThreadName(threadName);
		test20.setThreadName(threadName);
		test21.setThreadName(threadName);
		cleaner.setThreadName(threadName);
		test1.setDriver(suiteDriver);
		test2.setDriver(suiteDriver);
		test3.setDriver(suiteDriver);
		test4.setDriver(suiteDriver);
		test5.setDriver(suiteDriver);
		test6.setDriver(suiteDriver);
		test7.setDriver(suiteDriver);
		test8.setDriver(suiteDriver);
		test9.setDriver(suiteDriver);
		test10.setDriver(suiteDriver);
		test11.setDriver(suiteDriver);
		test12.setDriver(suiteDriver);
		test13.setDriver(suiteDriver);
		test14.setDriver(suiteDriver);
		test15.setDriver(suiteDriver);
		test16.setDriver(suiteDriver);
		test17.setDriver(suiteDriver);
		test18.setDriver(suiteDriver);
		test19.setDriver(suiteDriver);
		test20.setDriver(suiteDriver);
		test21.setDriver(suiteDriver);
		cleaner.setDriver(suiteDriver);
		test1.setWaiter(suiteWaiter);
		test2.setWaiter(suiteWaiter);
		test3.setWaiter(suiteWaiter);
		test4.setWaiter(suiteWaiter);
		test5.setWaiter(suiteWaiter);
		test6.setWaiter(suiteWaiter);
		test7.setWaiter(suiteWaiter);
		test8.setWaiter(suiteWaiter);
		test9.setWaiter(suiteWaiter);
		test10.setWaiter(suiteWaiter);
		test11.setWaiter(suiteWaiter);
		test12.setWaiter(suiteWaiter);
		test13.setWaiter(suiteWaiter);
		test14.setWaiter(suiteWaiter);
		test15.setWaiter(suiteWaiter);
		test15.setJsexec(exec);
		test16.setWaiter(suiteWaiter);
		test17.setWaiter(suiteWaiter);
		test18.setWaiter(suiteWaiter);
		test19.setWaiter(suiteWaiter);
		test20.setWaiter(suiteWaiter);
		test21.setWaiter(suiteWaiter);
		cleaner.setWaiter(suiteWaiter);
		try {
			Thread.sleep(500);
			long starttime = System.currentTimeMillis();
			System.out.println("TestSuite Thread with name: " + threadName + "started executing at: " + starttime);
			test1.setUp();
			test1.doTest();
			test2.setUp();
			test2.doTest();
			test3.setUp();
			test3.doTest();
			test4.setUp();
			test4.doTest();
			test5.setUp();
			test5.doTest();
			test6.setUp();
			test6.doTest();
			test7.setUp();
			test7.doTest();
			test8.setUp();
			test8.doTest();
			test9.setUp();
			test9.doTest();
			test10.setUp();
			test10.doTest();
			test11.setUp();
			test11.t80a();
			test11.t80b();
			test12.setUp();
			test12.t90();
			test13.setUp();
			test13.t100();
			test14.setUp();
			test14.t110();
			test15.setUp();
			test15.t120();
			test16.setUp();
			test16.t130();
			test17.setUp();
			test17.doTest();
			test18.setUp();
			test18.doTest();
			test19.setUp();
			test19.doTest();
			test20.setUp();
			test20.doTest();
			test21.setUp();
			test21.doTest();
			cleaner.cleanDB();
			long stoptime = System.currentTimeMillis();
			System.out.println("Finished executing test suite with thread name: " + threadName + " at time: " + stoptime);
		} catch (InterruptedException e) {
			e.printStackTrace();
			System.out.println("Thread with name: " + threadName + " was interrupted from its sleep.");
		} finally {
			suiteDriver.close();
		}
		
		
	}

}
