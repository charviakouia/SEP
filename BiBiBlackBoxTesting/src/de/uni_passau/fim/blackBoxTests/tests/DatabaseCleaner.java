package de.uni_passau.fim.blackBoxTests.tests;

import static org.junit.Assert.fail;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import de.uni_passau.fim.blackBoxTests.util.Driver;
import de.uni_passau.fim.blackBoxTests.util.UrlPrefix;

public class DatabaseCleaner {

    private static WebDriver driver;
    private static WebDriverWait waiter;
    private static boolean isMultiThreaded = false;
    private static String threadName = "";

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
        driver.findElement(By.id("form_medium_search:input_general_search_term")).sendKeys("Programmieren lernen" + threadName, Keys.ENTER);

        // Check that the correct result exists
        try { TimeUnit.SECONDS.sleep(5); } catch (InterruptedException e){}
        List<WebElement> elementList = driver.findElements(By.className("searchResultTitleEntry"));
        WebElement correctElement = null;
        for (WebElement element : elementList){
            ExpectedCondition<Boolean> condition =
                    ExpectedConditions.textToBePresentInElement(element, "Programmieren lernen" + threadName);
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
                .sendKeys("mitarbeiter.sep2021test" + threadName + "@gmail.com" + Keys.ENTER);

        // Navigate to the staff account
        waiter.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("[id$=emailLink]"))).click();

        // Delete the staff account
        waiter.until(ExpectedConditions.visibilityOfElementLocated(By.id("form_profile:delAccount")));
        waiter.until(ExpectedConditions.visibilityOfElementLocated(By.id("form_profile:delAccount"))).click();

        // Navigate to user-search
        driver.get(UrlPrefix.BASE_URL + "/view/admin/user-search.xhtml");

        // Search the user account
        waiter.until(ExpectedConditions.visibilityOfElementLocated(By.id("form_user_search:input_user_search_term")))
                .sendKeys("nutzer.sep2021test" + threadName + "@gmail.com" + Keys.ENTER);

        // Navigate to the user account
        waiter.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("[id$=emailLink]"))).click();

        // Delete the user account
        waiter.until(ExpectedConditions.visibilityOfElementLocated(By.id("form_profile:delAccount")));
        waiter.until(ExpectedConditions.visibilityOfElementLocated(By.id("form_profile:delAccount"))).click();


    }

	public static WebDriver getDriver() {
		return driver;
	}

	public static void setDriver(WebDriver driver) {
		DatabaseCleaner.driver = driver;
	}

	public static WebDriverWait getWaiter() {
		return waiter;
	}

	public static void setWaiter(WebDriverWait waiter) {
		DatabaseCleaner.waiter = waiter;
	}

	public static boolean isMultiThreaded() {
		return isMultiThreaded;
	}

	public static void setMultiThreaded(boolean isMultiThreaded) {
		DatabaseCleaner.isMultiThreaded = isMultiThreaded;
	}

	public static String getThreadName() {
		return threadName;
	}

	public static void setThreadName(String threadName) {
		DatabaseCleaner.threadName = threadName;
	}

    
}