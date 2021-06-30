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

import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.fail;

public class T120 {

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
    public void t120() {

        // Navigate to advanced search and perform search
        waiter.until(ExpectedConditions.visibilityOfElementLocated(By.id("advancedSearchLink"))).click();
        driver.findElement(By.id("form_medium_search:input_general_search_term")).sendKeys("Programmieren lernen", Keys.ENTER);

        // Check that the correct result exists
        try { TimeUnit.SECONDS.sleep(5); } catch (InterruptedException e){}
        List<WebElement> elementList = driver.findElements(By.className("searchResultTitleEntry"));
        WebElement correctElement = null;
        for (WebElement element : elementList){
            ExpectedCondition<Boolean> condition =
                    ExpectedConditions.textToBePresentInElement(element, "Programmieren lernen");
            try {
                waiter.until(condition);
                correctElement = element;
                break;
            } catch (TimeoutException ignored){}
        }
        if (correctElement == null){ fail("No correct table entry found"); }

        // Navigate to medium details page
        String link = correctElement.getAttribute("href");
        driver.navigate().to(link);

        // Book a medium-copy
        waiter.until(ExpectedConditions.visibilityOfElementLocated(By.id("pickUpForm")));
        driver.findElement(By.id("pickUpForm:signatureInputField")).sendKeys("17RE (1)");
        driver.findElement(By.id("pickUpForm:copyPickup")).click();

        // Check that the medium-copy has been booked
        try { TimeUnit.SECONDS.sleep(5); } catch (InterruptedException e){}
        List<WebElement> elementList1 = driver.findElements(By.className("copyListSignatures"));
        WebElement correctElement1 = null;
        for (WebElement element : elementList1){
            String text = element.getAttribute("value");
            if (text.equals("17RE (1)")){
                correctElement1 = element;
                break;
            }
        }
        if (correctElement1 == null){ fail("No correct table entry found"); }
        WebElement availability = (WebElement) ((JavascriptExecutor) driver).executeScript("return arguments[0].parentNode.parentNode.children[2];", correctElement1);
        Assert.assertEquals("Bereit zur Abholung", availability.getText());

    }

}
