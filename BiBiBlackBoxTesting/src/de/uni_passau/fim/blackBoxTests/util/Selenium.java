package de.uni_passau.fim.blackBoxTests.util;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static de.uni_passau.fim.blackBoxTests.util.UrlPrefix.BASE_URL;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class Selenium {

    public static void navigateTo(WebDriver driver, Pages page){
        driver.navigate().to(BASE_URL + page.getContextUrl());
    }

    public static void navigateToLinkInId(WebDriver driver, String id){
        String link = driver.findElement(By.id(id)).getText();
        driver.navigate().to(link);
    }

    public static void clickOn(WebDriver driver, String id){
        driver.findElement(By.id(id)).click();
    }

    public static void clickOn(WebDriverWait waiter, String id){
        waiter.until(ExpectedConditions.visibilityOfElementLocated(By.id(id))).click();
    }

    public static boolean contentOfIdContains(WebDriver driver, String id, String target){
        String text = driver.findElement(By.id(id)).getText();
        text = (text == null ? "" : text);
        return text.contains(target);
    }

    public static boolean contentOfIdContains(WebDriverWait waiter, String id, String target){
        String text = waiter.until(ExpectedConditions.visibilityOfElementLocated(By.id(id))).getText();
        text = (text == null ? "" : text);
        return text.contains(target);
    }

    public static boolean contentOfIdContains(WebDriver driver, String id, String attribute, String target){
        String text = driver.findElement(By.id(id)).getAttribute(attribute);
        text = (text == null ? "" : text);
        return text.contains(target);
    }

    public static boolean contentOfIdContains(WebDriverWait waiter, String id, String attribute, String target){
        String text = waiter.until(ExpectedConditions.visibilityOfElementLocated(By.id(id))).getAttribute(attribute);
        text = (text == null ? "" : text);
        return text.contains(target);
    }

    public static boolean contentOfIdEqualTo(WebDriver driver, String id, String target){
        String text = driver.findElement(By.id(id)).getText();
        text = (text == null ? "" : text);
        return text.equals(target);
    }

    public static boolean contentOfIdEqualTo(WebDriverWait waiter, String id, String target){
        String text = waiter.until(ExpectedConditions.visibilityOfElementLocated(By.id(id))).getText();
        text = (text == null ? "" : text);
        return text.equals(target);
    }

    public static boolean contentOfIdEqualTo(WebDriver driver, String id, String attribute, String target){
        String text = driver.findElement(By.id(id)).getAttribute(attribute);
        text = (text == null ? "" : text);
        return text.equals(target);
    }

    public static boolean contentOfIdEqualTo(WebDriverWait waiter, String id, String attribute, String target){
        String text = waiter.until(ExpectedConditions.visibilityOfElementLocated(By.id(id))).getAttribute(attribute);
        text = (text == null ? "" : text);
        return text.equals(target);
    }

    public static void typeInto(WebDriver driver, String id, CharSequence... sequences){
        driver.findElement(By.id(id)).sendKeys(sequences);
    }

    public static void typeInto(WebDriverWait waiter, String id, CharSequence... sequences){
        waiter.until(ExpectedConditions.visibilityOfElementLocated(By.id(id))).sendKeys(sequences);
    }

    public static boolean classEntityContainsText(WebDriver driver, String className, String attribute,
            String target){
        List<WebElement> elementList = driver.findElements(By.className(className));
        for (WebElement element : elementList){
            String text = element.getAttribute(attribute);
            text = (text == null ? "" : text);
            if (text.contains(target)){
                return true;
            }
        }
        return false;
    }

    public static boolean classEntityContainsText(WebDriver driver, String className, String target){
        List<WebElement> elementList = driver.findElements(By.className(className));
        for (WebElement element : elementList){
            String text = element.getText();
            text = (text == null ? "" : text);
            if (text.contains(target)){
                return true;
            }
        }
        return false;
    }

    public static WebElement getClassEntityContainingText(WebDriver driver, String className, String attribute,
            String target){
        List<WebElement> elementList = driver.findElements(By.className(className));
        for (WebElement element : elementList){
            String text = element.getAttribute(attribute);
            text = (text == null ? "" : text);
            if (text.contains(target)){
                return element;
            }
        }
        return null;
    }

    public static WebElement getClassEntityContainingText(WebDriver driver, String className, String target){
        List<WebElement> elementList = driver.findElements(By.className(className));
        for (WebElement element : elementList){
            String text = element.getText();
            text = (text == null ? "" : text);
            if (text.contains(target)){
                return element;
            }
        }
        return null;
    }

    public static WebElement getClassEntityWithText(WebDriver driver, String className, String attribute,
            String target){
        List<WebElement> elementList = driver.findElements(By.className(className));
        for (WebElement element : elementList){
            String text = element.getAttribute(attribute);
            text = (text == null ? "" : text);
            if (text.equals(target)){
                return element;
            }
        }
        return null;
    }

    public static WebElement getClassEntityWithText(WebDriver driver, String className, String target){
        List<WebElement> elementList = driver.findElements(By.className(className));
        for (WebElement element : elementList){
            String text = element.getText();
            text = (text == null ? "" : text);
            if (text.equals(target)){
                return element;
            }
        }
        return null;
    }

    public static void waitUntilLoaded(WebDriverWait waiter, Pages page){
        waiter.until(ExpectedConditions.visibilityOfElementLocated(By.id(page.getUniqueElementId())));
    }

    public static void waitUntilLoaded(){
        try { TimeUnit.SECONDS.sleep(5); } catch (InterruptedException ignored){}
    }

    public static void waitUntilLoaded(WebDriverWait waiter, String id){
        waiter.until(ExpectedConditions.visibilityOfElementLocated(By.id(id)));
    }

}
