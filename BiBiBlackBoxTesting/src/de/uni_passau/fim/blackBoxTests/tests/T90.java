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

public class T90 {

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
    public void t90() {

        // Input text and submit
        waiter.until(ExpectedConditions.visibilityOfElementLocated(By.id("form_medium_search_header")));
        driver.findElement(By.id("form_medium_search_header:input_medium_search_term_header"))
                .sendKeys("JSF: Durch", Keys.RETURN);

        // Check that the correct result exists
        waiter.until(ExpectedConditions.visibilityOfElementLocated(By.id("form_medium_search")));
        List<WebElement> elementList = driver.findElements(By.className("searchResultTitleEntry"));
        boolean entryExists = false;
        for (WebElement element : elementList){
            ExpectedCondition<Boolean> condition =
                    ExpectedConditions.textToBePresentInElement(element, "JSF: Durch Nacht zum Licht.");
            try {
                waiter.until(condition);
                entryExists = true;
                break;
            } catch (TimeoutException ignored){}
        }
        if (!entryExists){ fail("No correct table entry found"); }

    }

}
