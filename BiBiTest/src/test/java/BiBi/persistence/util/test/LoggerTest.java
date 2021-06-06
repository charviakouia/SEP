package BiBi.persistence.util.test;

import de.dedede.model.persistence.util.ConfigReader;
import de.dedede.model.persistence.util.Logger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

// import org.junit.Before;


/**
 * The tests for BiBi.dedede.de.model.persistance.util.Logger are held here.
 *
 * @author Molto Homo
 *
 */
public class LoggerTest {

	@Test
	public void testSevere() {
		ConfigReader configMock = Mockito.mock(ConfigReader.class);
		Properties testProperties = new Properties();
		InputStream stream = 
			getClass().getClassLoader().getResourceAsStream("config.txt");
		try {
			testProperties.load(stream);
			stream.close();
		} catch (IOException e) {}		
		Properties config = testProperties;
		Mockito.when(ConfigReader.getInstance()).thenReturn(configMock);
		Mockito.when(configMock.getSystemConfigurations()).thenReturn(config);
		String absolutePath = config.getProperty("LOG_DIRECTORY") + 
				config.getProperty("LOG_FILENAME") + ".txt";
		String testMessage = "this is a test message";
		Logger.severe(testMessage);
		String result = "";
		try {
			FileReader reader = new FileReader(absolutePath);
			BufferedReader bfr = new BufferedReader(reader);
			String line = "";
			while( (line = bfr.readLine()) != null) {
				result = line; //get last line
			}
			bfr.close();
		} catch (Exception ignored) {}
		
		Assertions.assertTrue(result.contains(testMessage));
	}
}
