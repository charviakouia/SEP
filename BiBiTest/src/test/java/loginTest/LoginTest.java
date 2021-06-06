package test.java.loginTest;

import de.dedede.model.logic.managed_beans.Login;
import de.dedede.model.persistence.exceptions.MaxConnectionsException;
import de.dedede.model.persistence.util.ConfigReader;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

public class LoginTest {
    private final static String SUCCESSFUL_LOGIN = "/view/account/profile.xhtml";
    private final  static String EMAIL_NOMATCH = "Login failed: No user for email";
    private final static  String   EMAIL_TEST= "mhn.nhm@hotmail.com";
    private final static String PASSWORD="madi";

    @Test
    public void loginSuccessfulTest() {
        Login loginManager = new Login();
        loginManager.setEmail("mhn.nhm@hotmail.com");
        loginManager.setPassword("madi");
        try {
            String doLogin = loginManager.logIn();
            assertEquals(doLogin, SUCCESSFUL_LOGIN);
        } catch (MaxConnectionsException e) {
        }
    }
    @Test
    public void loginEmailNoMatchTest() {
        Login loginManager = new Login();
        loginManager.setEmail(EMAIL_TEST);
        loginManager.setPassword(PASSWORD);
        ConfigReader configMock = Mockito.mock(ConfigReader.class);
        Properties testProperties = new Properties();
        InputStream stream =
                this.getClass().getClassLoader().getResourceAsStream("config.txt");
        try {
            testProperties.load(stream);
            stream.close();
        } catch (IOException e) {}
        Properties config = testProperties;
        Mockito.when(ConfigReader.getInstance()).thenReturn(configMock);
        Mockito.when(configMock.getSystemConfigurations()).thenReturn(config);
        String absolutePath = config.getProperty("LOG_DIRECTORY") +
                config.getProperty("LOG_FILENAME") + ".txt";
        String result="";
        try {
            String doLogin = loginManager.logIn();
            try {
                FileReader reader = new FileReader(absolutePath);
                BufferedReader bfr = new BufferedReader(reader);
                String line = "";
                while( (line = bfr.readLine()) != null) {
                    result = line;
                }
                bfr.close();
            } catch (Exception ignored) {}
        } catch (MaxConnectionsException e) {
        }
        assertTrue(result.contains(EMAIL_NOMATCH));
    }
    @Test
    public void LoginWrongCredentialsTest() {
        Login loginManager = new Login();
        loginManager.setEmail("mhn.nhm@hotmail.com");
        loginManager.setPassword("madiiiii");
        try {
            String doLogin = loginManager.logIn();
            assertNull(doLogin);
        } catch (MaxConnectionsException e) {
        }
    }
}
