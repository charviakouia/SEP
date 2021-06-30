package de.uni_passau.fim.blackBoxTests.tests;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import de.uni_passau.fim.blackBoxTests.test_suite.Driver;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class T01 {
    
    private static WebDriver driver;
    private static WebDriverWait waiter;

    @BeforeClass
    public static void setUp() {
        driver = Driver.getDriver();
        waiter = Driver.getDriverWait();
    }

    @Test
    public void t10() {
        //Ist ein Title "BiBi"?
        waiter.until(ExpectedConditions.titleContains("BiBi"));

        //Navigation zur Anmeldung
        waiter.until(ExpectedConditions.visibilityOfElementLocated(By.id("signIn")));
        waiter.until(ExpectedConditions.visibilityOfElementLocated(By.id("signIn"))).click();

        //Sehe ich einen Anmeldung-Button?
        try {
            driver.findElement(By.id("login_form:login_login_button"));
            assertTrue(true);
        } catch (Exception e) {
            fail("Element not found.");
        }
    }
    
    @AfterClass
    public static void tearDown() {}

}
