package de.uni_passau.fim.blackBoxTests.tests;

import de.uni_passau.fim.blackBoxTests.test_suite.Driver;
import de.uni_passau.fim.blackBoxTests.test_suite.UrlPrefix;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class T131 {

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
    public void t131() {
        // Navigate to lending
        driver.get(UrlPrefix.BASE_URL + "/view/staff/lending.xhtml");

        waiter.until(ExpectedConditions.visibilityOfElementLocated(By.id("form_lending:lending_mail_field"))).
                clear();
        waiter.until(ExpectedConditions.visibilityOfElementLocated(By.id("form_lending:lending_mail_field"))).
                sendKeys("nutzer.sep2021test@gmail.com");
        try { TimeUnit.SECONDS.sleep(5); } catch (InterruptedException e){}
        waiter.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("[id$=lending_signature_field]")))
                .sendKeys("17RE (1)");
        try { TimeUnit.SECONDS.sleep(5); } catch (InterruptedException e){}
        waiter.until(ExpectedConditions.visibilityOfElementLocated(By.id("form_lending:button_direct_lend_copies")))
                .click();
        try { TimeUnit.SECONDS.sleep(5); } catch (InterruptedException e){}
        // Verify data in page
        assertTrue(driver.getPageSource().contains("1 Exemplar(e) an nutzer.sep2021test@gmail.com verliehen."));
    }

}
