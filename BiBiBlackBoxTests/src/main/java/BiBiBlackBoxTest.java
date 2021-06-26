import io.github.bonigarcia.wdm.WebDriverManager;
import org.graphwalker.core.machine.ExecutionContext;
import org.graphwalker.java.annotation.AfterExecution;
import org.graphwalker.java.annotation.BeforeExecution;
import org.graphwalker.java.annotation.GraphWalker;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import static org.junit.Assert.assertNotNull;

@GraphWalker(start = "e_startBrowser", value = "random(edge_coverage(100))")
public class BiBiBlackBoxTest extends ExecutionContext implements BiBiGraph {

    private WebDriver driver = null;
    private WebDriverWait waiter = null;

    @BeforeExecution
    public void setup() {
        WebDriverManager.firefoxdriver().setup();
    }

    @AfterExecution
    public void cleanup() {
        if( driver != null) {
            driver.quit();
        }
    }

    @Override
    public void v_Profile() {
        waiter.until(ExpectedConditions.textToBe(By.id("form_profile:frstname"),
                "Langstrumpf"));
    }

    @Override
    public void e_enterEmailAndPasswd() {
        waiter.until(ExpectedConditions.visibilityOfElementLocated(By.id("login_form:login_email_field")));
        waiter.until(ExpectedConditions.visibilityOfElementLocated(By.id("login_form:login_email_field"))).clear();
        waiter.until(ExpectedConditions.visibilityOfElementLocated(By.id("login_form:login_email_field")))
                .sendKeys("pravdin@fim.uni-passau.de");
        waiter.until(ExpectedConditions.visibilityOfElementLocated(By.id("login_form:login_password_field")));
        waiter.until(ExpectedConditions.visibilityOfElementLocated(By.id("login_form:login_password_field"))).clear();
        waiter.until(ExpectedConditions.visibilityOfElementLocated(By.id("login_form:login_password_field")))
                .sendKeys("Password1" + Keys.ENTER);
    }

    @Override
    public void e_enterEmailAndPasswordWrong() {
        waiter.until(ExpectedConditions.visibilityOfElementLocated(By.id("login_form:login_email_field")));
        waiter.until(ExpectedConditions.visibilityOfElementLocated(By.id("login_form:login_email_field"))).clear();
        waiter.until(ExpectedConditions.visibilityOfElementLocated(By.id("login_form:login_email_field")))
                .sendKeys("wrongEmail@gmail.com");
        waiter.until(ExpectedConditions.visibilityOfElementLocated(By.id("login_form:login_password_field")));
        waiter.until(ExpectedConditions.visibilityOfElementLocated(By.id("login_form:login_password_field"))).clear();
        waiter.until(ExpectedConditions.visibilityOfElementLocated(By.id("login_form:login_password_field")))
                .sendKeys("wrongPassword1" + Keys.ENTER);
    }

    @Override
    public void e_enterBaseURL() {
        driver.get("http://localhost:8080/BiBi/");
    }

    @Override
    public void v_BrowserStarted() {
        assertNotNull(driver);
    }

    @Override
    public void e_clickSignOut() {
        waiter.until(ExpectedConditions.visibilityOfElementLocated(By.className("nav-item dropdown")));
        waiter.until(ExpectedConditions.visibilityOfElementLocated(By.className("nav-item dropdown"))).click();
        waiter.until(ExpectedConditions.visibilityOfElementLocated(By.className("form_log_out:button_log_out")));
        waiter.until(ExpectedConditions.visibilityOfElementLocated(By.className("form_log_out:button_log_out"))).click();

    }

    @Override
    public void e_clickProfile() {
        waiter.until(ExpectedConditions.visibilityOfElementLocated(By.className("nav-item dropdown")));
        waiter.until(ExpectedConditions.visibilityOfElementLocated(By.className("nav-item dropdown"))).click();
        waiter.until(ExpectedConditions.visibilityOfElementLocated(By.className("dropdown-item")));
        waiter.until(ExpectedConditions.visibilityOfElementLocated(By.className("dropdown-item"))).click();
    }

    @Override
    public void v_MediumSearch() {
        waiter.until(ExpectedConditions.textToBe(By.id("navbar_content"),
                "Account"));
    }

    @Override
    public void v_homePage() {
        waiter.until(ExpectedConditions.visibilityOfElementLocated(By.id("form_medium_search")));
    }

    @Override
    public void e_clickSignIn() {
        waiter.until(ExpectedConditions.visibilityOfElementLocated(By.id("signIn")));
        waiter.until(ExpectedConditions.visibilityOfElementLocated(By.id("signIn"))).click();
    }

    @Override
    public void e_enterNewZip() {
        waiter.until(ExpectedConditions.visibilityOfElementLocated(By.id("form_profile:zipCode")));
        waiter.until(ExpectedConditions.visibilityOfElementLocated(By.id("form_profile:zipCode"))).clear();
        waiter.until(ExpectedConditions.visibilityOfElementLocated(By.id("form_profile:zipCode")))
                .sendKeys("94032");
        waiter.until(ExpectedConditions.visibilityOfElementLocated(By.id("form_profile:button_save")));
        waiter.until(ExpectedConditions.visibilityOfElementLocated(By.id("form_profile:button_save"))).click();

    }

    @Override
    public void v_SignInErrorMessage() {
        waiter.until(ExpectedConditions.textToBe(By.id("login_form:login_email_message"),
                "Unter dieser E-Mail Adresse ist kein Nutzer im System registriert."));
    }

    @Override
    public void e_startBrowser() {
        driver = new FirefoxDriver();
        assertNotNull(driver);
        waiter = new WebDriverWait(driver, 10);
    }

    @Override
    public void v_SignIn() {
        waiter.until(ExpectedConditions.visibilityOfElementLocated(By.id("login_form:login_login_button")));
    }

    @Override
    public void v_ProfileWithNewZip() {
        waiter.until(ExpectedConditions.textToBe(By.id("form_profile:zipCode"),
                "94032"));
    }
}
