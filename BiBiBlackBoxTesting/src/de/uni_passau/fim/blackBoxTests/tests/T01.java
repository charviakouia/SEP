package de.uni_passau.fim.blackBoxTests.tests;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import de.uni_passau.fim.blackBoxTests.test_suite.Driver;

public class T01 {
    
    private WebDriver driver;
    private WebDriverWait waiter;

    @Before
    public void setUp() {
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
    
    @After
    public void tearDown() {
    }
}
