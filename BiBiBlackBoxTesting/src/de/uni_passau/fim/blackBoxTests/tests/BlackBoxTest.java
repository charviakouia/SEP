package de.uni_passau.fim.blackBoxTests.tests;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

public interface BlackBoxTest {

    void setUp();

    void tearDown();

    boolean isMultiThreaded();

    void setMultiThreaded(boolean isMultiThreaded);

    WebDriver getDriver();

    void setDriver(WebDriver driver);

    WebDriverWait getWaiter();

    void setWaiter(WebDriverWait waiter);

    String getThreadName();

    void setThreadName(String threadName);

}
