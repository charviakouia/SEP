package de.uni_passau.fim.blackBoxTests.tests;

import de.uni_passau.fim.blackBoxTests.test_suite.Driver;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.List;

import static org.junit.Assert.fail;

public class T100 {

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
    public void t100() {

        // Navigate to registration page, input registration data, and save
        waiter.until(ExpectedConditions.visibilityOfElementLocated(By.id("registrationLink"))).click();
        driver.findElement(By.id("registrationForm:firstName")).sendKeys("Bob");
        driver.findElement(By.id("registrationForm:lastName")).sendKeys("Mustermann");
        driver.findElement(By.id("registrationForm:password")).sendKeys("sdfHs4!a");
        driver.findElement(By.id("registrationForm:confirmedPassword")).sendKeys("sdfHs4!a");
        driver.findElement(By.id("registrationForm:email")).sendKeys("nutzer.sep2021test@gmail.com");
        driver.findElement(By.id("registrationForm:zip")).sendKeys("94032");
        driver.findElement(By.id("registrationForm:city")).sendKeys("Passau");
        driver.findElement(By.id("registrationForm:street")).sendKeys("Innstraße");
        driver.findElement(By.id("registrationForm:streetNumber")).sendKeys("40");
        driver.findElement(By.id("registrationForm:saveButton")).click();

        // Navigate to email confirmation page
        waiter.until(ExpectedConditions.visibilityOfElementLocated(By.id("form_profile")));
        String link = driver.findElement(By.id("messageForm:neutralMessage")).getText();
        driver.navigate().to(link);

        // Verify data in profile page
        String displayedFirstName = waiter.until(ExpectedConditions.visibilityOfElementLocated(By.id("form_profile:frstname"))).getAttribute("value");
        Assert.assertEquals("Bob", displayedFirstName);
        String displayedEmail = waiter.until(ExpectedConditions.visibilityOfElementLocated(By.id("form_profile:email"))).getAttribute("value");
        Assert.assertEquals("nutzer.sep2021test@gmail.com", displayedEmail);

    }

}
