package de.uni_passau.fim.blackBoxTests.tests;

import de.uni_passau.fim.blackBoxTests.test_suite.Driver;
import de.uni_passau.fim.blackBoxTests.test_suite.UrlPrefix;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.fail;

public class DatabaseCleaner {

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
    public void cleanDB() {

        try {
            // Log out
            waiter.until(ExpectedConditions.visibilityOfElementLocated(By.id("accountDropDown"))).click();
            waiter.until(ExpectedConditions.visibilityOfElementLocated(By.id("form_log_out:button_log_out"))).click();
        } catch (Exception e) {
            // ignored
        }

        // Navigate to login
        driver.get(UrlPrefix.BASE_URL + "/view/ffa/login.xhtml");

        // Sign in
        waiter.until(ExpectedConditions.visibilityOfElementLocated(By.id("login_form:login_email_field")));
        waiter.until(ExpectedConditions.visibilityOfElementLocated(By.id("login_form:login_email_field"))).clear();
        waiter.until(ExpectedConditions.visibilityOfElementLocated(By.id("login_form:login_email_field")))
                .sendKeys("admin.sep2021.test@gmail.com");
        waiter.until(ExpectedConditions.visibilityOfElementLocated(By.id("login_form:login_password_field")));
        waiter.until(ExpectedConditions.visibilityOfElementLocated(By.id("login_form:login_password_field"))).clear();
        waiter.until(ExpectedConditions.visibilityOfElementLocated(By.id("login_form:login_password_field")))
                .sendKeys("xlA24!bGhm" + Keys.ENTER);

        try { TimeUnit.SECONDS.sleep(3); } catch (InterruptedException e){}
        // Navigate to advanced search and perform search
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
        try { TimeUnit.SECONDS.sleep(4); } catch (InterruptedException e){}

        // Delete the medium
        waiter.until(ExpectedConditions.visibilityOfElementLocated(By.id("form_mediumAttributes_forUsers:delMedium")))
                .click();

        // Navigate to category-browser
//        driver.get(UrlPrefix.BASE_URL + "/view/opac/category-browser.xhtml");

        // Delete the category 'Informatik'
//        waiter.until(ExpectedConditions.visibilityOfElementLocated(By.id("j_id2")));
//        waiter.until(ExpectedConditions.visibilityOfElementLocated(By.id("j_id2"))).click();
//        waiter.until(ExpectedConditions.visibilityOfElementLocated(By.id("j_id4:link_category_name_737179246")));
//        waiter.until(ExpectedConditions.visibilityOfElementLocated(By.id("j_id4:link_category_name_737179246"))).click();
//        waiter.until(ExpectedConditions.visibilityOfElementLocated(By.id("form_category_controls:link_delete_category")));
//        waiter.until(ExpectedConditions.visibilityOfElementLocated(By.id("form_category_controls:link_delete_category"))).click();

        // Navigate to user-search
        driver.get(UrlPrefix.BASE_URL + "/view/admin/user-search.xhtml");

        // Search the staff account
        waiter.until(ExpectedConditions.visibilityOfElementLocated(By.id("form_user_search:input_user_search_term")))
                .sendKeys("mitarbeiter.sep2021test@gmail.com" + Keys.ENTER);

        // Navigate to the staff account
        waiter.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("[id$=emailLink]"))).click();

        // Delete the staff account
        waiter.until(ExpectedConditions.visibilityOfElementLocated(By.id("form_profile:delAccount")));
        waiter.until(ExpectedConditions.visibilityOfElementLocated(By.id("form_profile:delAccount"))).click();

        // Navigate to user-search
        driver.get(UrlPrefix.BASE_URL + "/view/admin/user-search.xhtml");

        // Search the user account
        waiter.until(ExpectedConditions.visibilityOfElementLocated(By.id("form_user_search:input_user_search_term")))
                .sendKeys("nutzer.sep2021test@gmail.com" + Keys.ENTER);

        // Navigate to the user account
        waiter.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("[id$=emailLink]"))).click();

        // Delete the user account
        waiter.until(ExpectedConditions.visibilityOfElementLocated(By.id("form_profile:delAccount")));
        waiter.until(ExpectedConditions.visibilityOfElementLocated(By.id("form_profile:delAccount"))).click();


    }

}