package de.uni_passau.fim.blackBoxTests.test_suite;

import java.util.Objects;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.GeckoDriverService;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * Driver class for getting a {@link WebDriver}.
 */
public final class Driver {

    /**
     * Timeout for the WebDriverWait in seconds.
     */
    public static final int TIMEOUT_WAIT = 10;

    private static WebDriver driver = null;
    private static WebDriverWait driverWait = null;

    private Driver() {
        throw new UnsupportedOperationException(
            "Prevent utillity class instantiation.");
    }

    /**
     * If currently no driver is set, ceates a new instance of it, stores it and
     * returns it. If a driver is currently present, it just will be returned.
     *
     * @return A {@link WebDriver} for testing with selenium.
     */
    public static WebDriver getDriver() {
        if (Objects.isNull(driver)) {
            switch (OsUtil.getMyOS()) {
                case WINDOWS -> System.setProperty("webdriver.gecko.driver", "geckodriver.exe");
                case LINUX -> System.setProperty("webdriver.gecko.driver", "geckodriver");
                case MAC -> System.setProperty("webdriver.gecko.driver", "geckodriverMacOs");
            }
            driver = new FirefoxDriver(GeckoDriverService.createDefaultService());
        }
        return driver;
    }
    
    public static FirefoxDriver getNewDriver() {
        switch (OsUtil.getMyOS()) {
            case WINDOWS -> System.setProperty("webdriver.gecko.driver","geckodriver.exe");
            case LINUX -> System.setProperty("webdriver.gecko.driver","geckodriver");
            case MAC -> System.setProperty("webdriver.gecko.driver","geckodriverMacOs");
        }
        return new FirefoxDriver(GeckoDriverService.createDefaultService());
    }

    /**
     * If currently no waiting driver is set, creates a new instance of it,
     * stores it and returns it. If a driver is currently present, it just will
     * be returned.
     *
     * @return A {@link WebDriverWait} for testing with selenium and waiting for
     *         elements to load.
     */
    public static WebDriverWait getDriverWait() {
        if (Objects.isNull(driverWait)) {
            driverWait = new WebDriverWait(getDriver(), TIMEOUT_WAIT);
        }
        return driverWait;
    }
    
    /**
     * Gets a {@link JavascriptExecutor} from the {@link WebDriver}.
     *
     * @return A {@link JavascriptExecutor}.
     */
    public static JavascriptExecutor getJavascriptExecutor() {
        return (JavascriptExecutor) getDriver();
    }

    /**
     * Shutdowns the driver.
     */
    public static void shutdown() {
        if (Objects.nonNull(driver)) {
            driver.quit();
        }
    }


    
}
