package de.uni_passau.fim.blackBoxTests.tests;

import static org.junit.Assert.fail;

import java.util.List;
import java.util.concurrent.TimeUnit;

import de.uni_passau.fim.blackBoxTests.util.Selenium;
import org.junit.*;
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

    private WebDriver driver;
    private WebDriverWait waiter;
    private boolean isMultiThreaded = false;
    private String threadName = "";

    @Before
    public void setUp() {
        driver = Driver.getDriver();
        waiter = Driver.getDriverWait();
    }

    @After
    public void tearDown() {}

    @Test
    public void cleanDB() {

        try {
            // Log out
            waiter.until(ExpectedConditions.visibilityOfElementLocated(By.id("accountDropDown"))).click();
            waiter.until(ExpectedConditions.visibilityOfElementLocated(By.id("form_log_out:button_log_out"))).click();
        } catch (Exception ignored) {}

        // Navigate to login
        driver.get(UrlPrefix.BASE_URL + "/view/ffa/login.xhtml");

        // Sign in as admin
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
        driver.findElement(By.id("form_medium_search:input_general_search_term"))
                .sendKeys("Programmieren lernen" + threadName, Keys.ENTER);

        // Get medium, 'Programmieren Lernen' to delete
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
        if (correctElement != null){

            // Navigate to medium details page
            String link = correctElement.getAttribute("href");
            driver.navigate().to(link);
            try { TimeUnit.SECONDS.sleep(4); } catch (InterruptedException e){}

            // Delete the medium
            waiter.until(ExpectedConditions
                    .visibilityOfElementLocated(By.id("form_mediumAttributes_forUsers:delMedium"))).click();

        }

        // Navigate to category-browser
        driver.get(UrlPrefix.BASE_URL + "/view/opac/category-browser.xhtml");

        // TODO: Fix
        // Find parent category
        Selenium.waitUntilLoaded();
        List<WebElement> categories = driver.findElements(By.cssSelector(".accordion-button"));
        for (WebElement category : categories){
            if (category.getText().contains("SampleParentCategory")){
                category.click();
                break;
            }
        }

        // Get and delete the category 'Informatik'
        Selenium.waitUntilLoaded();
        List<WebElement> categoryLinks = driver.findElements(By.cssSelector("a"));
        for (WebElement link : categoryLinks){
            if (link.getText().contains("Informatik")){
                link.click();
                waiter.until(ExpectedConditions.visibilityOfElementLocated(By
                        .id("form_category_controls:link_delete_category"))).click();
                waiter.until(ExpectedConditions.visibilityOfElementLocated(By
                        .id("form_category_controls:button_confirm_category_deletion"))).click();
                Selenium.waitUntilLoaded();
            }
        }

        try {
            // Navigate to user-search
            driver.get(UrlPrefix.BASE_URL + "/view/admin/user-search.xhtml");

            // Search the staff account
            waiter.until(ExpectedConditions.visibilityOfElementLocated(
                    By.id("form_user_search:input_user_search_term")))
                    .sendKeys("mitarbeiter.sep2021test" + threadName + "@gmail.com" + Keys.ENTER);

            // Navigate to the staff account
            waiter.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("[id$=emailLink]"))).click();

            // Delete the staff account
            waiter.until(ExpectedConditions.visibilityOfElementLocated(By.id("form_profile:delAccount")));
            waiter.until(ExpectedConditions.visibilityOfElementLocated(By.id("form_profile:delAccount"))).click();
        } catch (Exception ignored){ }

        try {
            // Navigate to user-search
            driver.get(UrlPrefix.BASE_URL + "/view/admin/user-search.xhtml");

            // Search the user account
            waiter.until(ExpectedConditions.visibilityOfElementLocated(
                    By.id("form_user_search:input_user_search_term")))
                    .sendKeys("nutzer.sep2021test" + threadName + "@gmail.com" + Keys.ENTER);

            // Navigate to the user account
            waiter.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("[id$=emailLink]"))).click();

            // Delete the user account
            waiter.until(ExpectedConditions.visibilityOfElementLocated(By.id("form_profile:delAccount")));
            waiter.until(ExpectedConditions.visibilityOfElementLocated(By.id("form_profile:delAccount"))).click();
        } catch (Exception ignored){ }

    }

	public WebDriver getDriver() {
		return driver;
	}

	public void setDriver(WebDriver driver) {
		this.driver = driver;
	}

	public WebDriverWait getWaiter() {
		return waiter;
	}

	public void setWaiter(WebDriverWait waiter) {
		this.waiter = waiter;
	}

	public boolean isMultiThreaded() {
		return isMultiThreaded;
	}

	public void setMultiThreaded(boolean isMultiThreaded) {
		this.isMultiThreaded = isMultiThreaded;
	}

	public String getThreadName() {
		return threadName;
	}

	public void setThreadName(String threadName) {
		this.threadName = threadName;
	}
    
    
    
}