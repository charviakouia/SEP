package loggerTest;

import de.dedede.model.persistence.util.ConfigReader;
import de.dedede.model.persistence.util.Logger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
// import org.mockito.Mockito;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * The tests for BiBi.dedede.de.model.persistance.util.Logger are held here.
 *
 * @author Jonas Picker
 *
 */
/*
public class LoggerTest {
   	
	@Test
	public void testSevere() {
		ConfigReader configMock = Mockito.mock(ConfigReader.class);
		Properties testProperties = new Properties();
		String sep = File.separator;
		String up = "..";

		try {
			InputStream stream = new FileInputStream(up + sep + "BiBi" + sep + "src" 
					+ sep + "main" + sep + "webapp" + sep + "WEB-INF" 
					+ sep + "config.properties");
			testProperties.load(stream);
			stream.close();
		} catch (Exception ignored) {}		
		Mockito.when(configMock.getKey("LOG_DIRECTORY")).thenReturn(testProperties.getProperty("LOG_DIRECTORY"));
		Mockito.when(configMock.getKey("LOG_FILENAME")).thenReturn(testProperties.getProperty("LOG_FILENAME"));
		
		String absolutePath = configMock.getKey("LOG_DIRECTORY") + 
				configMock.getKey("LOG_FILENAME") + ".txt";
		String testMessage = "All required tables seem to be present.";
		String result = "";
		try {
			FileReader reader = new FileReader(absolutePath);
			BufferedReader bfr = new BufferedReader(reader);
			StringBuilder sb = new StringBuilder();
			String line = "";
			while( (line = bfr.readLine()) != null) {
				sb.append(line);
			}
			bfr.close();
			result = sb.toString();
		} catch (Exception ignored) {}
		
		Assertions.assertTrue(result.contains(testMessage));
	}
}
*/