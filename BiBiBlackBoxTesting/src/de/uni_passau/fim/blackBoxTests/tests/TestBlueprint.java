package de.uni_passau.fim.blackBoxTests.tests;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

public class TestBlueprint {
	
    protected WebDriver driver;
    protected WebDriverWait waiter;
    protected String threadName;
    
    public TestBlueprint() {}
	
	public TestBlueprint(String threadName) {
		this.threadName = threadName;
	}
		
	public void doTest() {}
		
	public void execute() {
		System.out.println("Started test class " + this.getClass().getSimpleName() + " with in thread with name:" + threadName);
		doTest();
		System.out.println("Ended test class " + this.getClass().getSimpleName() + " in thread with name:" + threadName);
	}
}
