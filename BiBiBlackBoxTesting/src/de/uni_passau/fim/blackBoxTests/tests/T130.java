package de.uni_passau.fim.blackBoxTests.tests;

import de.uni_passau.fim.blackBoxTests.test_suite.Driver;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertTrue;

public class T130 {

    private static WebDriver driver;
    private static WebDriverWait waiter;

    @BeforeClass
    public static void setUp() {
        driver = Driver.getDriver();
        waiter = Driver.getDriverWait();
    }

    @AfterClass
    public static void tearDown() {}

    @Test
    public void t130() {
        // Log out
        waiter.until(ExpectedConditions.visibilityOfElementLocated(By.id("accountDropDown"))).click();
        waiter.until(ExpectedConditions.visibilityOfElementLocated(By.id("form_log_out:button_log_out"))).click();

        // Log in
        waiter.until(ExpectedConditions.visibilityOfElementLocated(By.id("login_form:login_email_field"))).
                sendKeys("mitarbeiter.sep2021test@gmail.com");
        driver.findElement(By.id("login_form:login_password_field")).sendKeys("sijAs13!!A" + Keys.ENTER);

        // Navigate to the list of copies that users have marked for pick up
        waiter.until(ExpectedConditions.visibilityOfElementLocated(By.id("header_staff_dropdown")));
        waiter.until(ExpectedConditions.visibilityOfElementLocated(By.id("header_staff_dropdown"))).click();
        waiter.until(ExpectedConditions.visibilityOfElementLocated(By.id("copiesReadyForPickUp")));
        waiter.until(ExpectedConditions.visibilityOfElementLocated(By.id("copiesReadyForPickUp"))).click();

        // Verify data in page
        assertTrue(driver.getPageSource().contains("17RE (1)"));
    }

}
