package de.uni_passau.fim.blackBoxTests.util;

import de.uni_passau.fim.blackBoxTests.tests.DatabaseCleaner;
import de.uni_passau.fim.blackBoxTests.tests.TestSuiteThread;
import org.openqa.selenium.WebDriver;

public class SingleThreadRunner {

    private static String threadName = "";

    public static void main(String[] args){

        try {
            WebDriver webDriver = Driver.getNewDriver();
            new Extention(webDriver).run();
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            Driver.getDriver();
            DatabaseCleaner.setThreadName(threadName);
            DatabaseCleaner.setUp();
            DatabaseCleaner dbc = new DatabaseCleaner();
            dbc.cleanDB();
        }

    }

    private static class Extention extends TestSuiteThread {

        public Extention(WebDriver driver) {
            super(driver);
        }

        @Override
        public void run() {
            threadName = Thread.currentThread().getName();
            super.run();
        }
    }

}
