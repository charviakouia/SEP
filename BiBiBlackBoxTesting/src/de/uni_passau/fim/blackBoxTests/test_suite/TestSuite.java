package de.uni_passau.fim.blackBoxTests.test_suite;

import static de.uni_passau.fim.blackBoxTests.test_suite.UrlPrefix.BASE_URL;

import de.uni_passau.fim.blackBoxTests.tests.T02;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
import org.openqa.selenium.WebDriver;

import de.uni_passau.fim.blackBoxTests.tests.T01;

@RunWith(Suite.class)
@SuiteClasses({
    T01.class, T02.class
})
public class TestSuite {

    @BeforeClass
    public static void setUp() {
        WebDriver driver = Driver.getDriver();
        driver.get(BASE_URL);
        // driver.manage().windowS().setSize(new Dimension(968, 827));
    }

    @AfterClass
    public static void tearDown() {
        Driver.shutdown();
    }

}