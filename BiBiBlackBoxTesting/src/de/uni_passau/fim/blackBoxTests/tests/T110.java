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

import java.time.Duration;
import java.util.concurrent.TimeUnit;

public class T110 {

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
    public void t110() {

        // Log out
        waiter.until(ExpectedConditions.visibilityOfElementLocated(By.id("accountDropDown"))).click();
        waiter.until(ExpectedConditions.visibilityOfElementLocated(By.id("form_log_out:button_log_out"))).click();

        // Initiate password reset
        waiter.until(ExpectedConditions.visibilityOfElementLocated(By.id("login_form")));
        driver.findElement(By.id("login_form:login_email_field")).sendKeys("nutzer.sep2021test@gmail.com");
        driver.findElement(By.id("login_form:login_reset_password_button")).click();

        // Navigate to password reset page
        try { TimeUnit.SECONDS.sleep(5); } catch (InterruptedException e){}
        String link = driver.findElement(By.id("messageForm:neutralMessage")).getText();
        driver.navigate().to(link);

        // Reset password
        driver.findElement(By.id("passwordResetForm:password")).sendKeys("djnASdd1d!");
        driver.findElement(By.id("passwordResetForm:repeatPassword")).sendKeys("djnASdd1d!");
        driver.findElement(By.id("passwordResetForm:submitButton")).click();

        // Verify data in profile page
        String displayedFirstName = waiter.until(ExpectedConditions.visibilityOfElementLocated(By.id("form_profile:frstname"))).getAttribute("value");
        Assert.assertEquals("Bob", displayedFirstName);

    }

}
