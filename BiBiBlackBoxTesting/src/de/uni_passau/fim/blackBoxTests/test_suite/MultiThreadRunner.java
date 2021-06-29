package de.uni_passau.fim.blackBoxTests.test_suite;

import org.openqa.selenium.WebDriver;

import de.uni_passau.fim.blackBoxTests.tests.TestSuiteThread;

public class MultiThreadRunner {

	public static void main(String args[]) {
		WebDriver webDriver1 = Driver.getNewDriver();
		WebDriver webDriver2 = Driver.getNewDriver();
		TestSuiteThread suiteThread1 = new TestSuiteThread(webDriver1);
		TestSuiteThread suiteThread2 = new TestSuiteThread(webDriver2);
		suiteThread1.start();
		suiteThread2.start();
	}

}
