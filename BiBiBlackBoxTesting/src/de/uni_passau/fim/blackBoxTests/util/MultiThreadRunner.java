package de.uni_passau.fim.blackBoxTests.util;

import org.openqa.selenium.WebDriver;

import de.uni_passau.fim.blackBoxTests.tests.TestSuiteThread;

public class MultiThreadRunner {

	public static void main(String[] args) {
		WebDriver webDriver1 = Driver.getNewDriver();
		WebDriver webDriver2 = Driver.getNewDriver();
		WebDriver webDriver3 = Driver.getNewDriver();
		WebDriver webDriver4 = Driver.getNewDriver();
		WebDriver webDriver5 = Driver.getNewDriver();
		WebDriver webDriver6 = Driver.getNewDriver();
		WebDriver webDriver7 = Driver.getNewDriver();
		WebDriver webDriver8 = Driver.getNewDriver();
		WebDriver webDriver9 = Driver.getNewDriver();
		WebDriver webDriver10 = Driver.getNewDriver();
		WebDriver webDriver11 = Driver.getNewDriver();
		TestSuiteThread suiteThread1 = new TestSuiteThread(webDriver1);
		TestSuiteThread suiteThread2 = new TestSuiteThread(webDriver2);
		TestSuiteThread suiteThread3 = new TestSuiteThread(webDriver3);
		TestSuiteThread suiteThread4 = new TestSuiteThread(webDriver4);
		TestSuiteThread suiteThread5 = new TestSuiteThread(webDriver5);
		TestSuiteThread suiteThread6 = new TestSuiteThread(webDriver6);
		TestSuiteThread suiteThread7 = new TestSuiteThread(webDriver7);
		TestSuiteThread suiteThread8 = new TestSuiteThread(webDriver8);
		TestSuiteThread suiteThread9 = new TestSuiteThread(webDriver9);
		TestSuiteThread suiteThread10 = new TestSuiteThread(webDriver10);
		TestSuiteThread suiteThread11 = new TestSuiteThread(webDriver11);
		suiteThread1.start();
		try {
			Thread.sleep(5000);
			suiteThread2.start();
			Thread.sleep(5000);
			suiteThread3.start();
			Thread.sleep(5000);
			suiteThread4.start();
			Thread.sleep(5000);
			suiteThread5.start();
			Thread.sleep(5000);
			suiteThread6.start();
			Thread.sleep(5000);
			suiteThread7.start();
			Thread.sleep(5000);
			suiteThread8.start();
			Thread.sleep(5000);
			suiteThread9.start();
			Thread.sleep(5000);
			suiteThread10.start();
			Thread.sleep(5000);
			suiteThread11.start();
		} catch (InterruptedException e) {
			System.out.println("InterruptedException!!");
			e.printStackTrace();
		}
	}

}
