package loggerTest;

import de.dedede.model.persistence.util.ConfigReader;
import de.dedede.model.persistence.util.Logger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.BufferedReader;
import java.io.File;
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
public class LoggerTest {
   	
	@Test
	public void testSevere() {
		ConfigReader configMock = Mockito.mock(ConfigReader.class);
		Properties testProperties = new Properties();
		String sep = File.pathSeparator;
		String up = "..";
		InputStream stream = 
			this.getClass().getClassLoader().getResourceAsStream(up + sep 
					+ up + sep + up + sep + up + sep + up + sep + "src" 
					+ sep + "main" + sep + "webapp" + sep + "WEB-INF" 
					+ sep + "config.txt");
		try {
			testProperties.load(stream);
			stream.close();
		} catch (IOException ignored) {}		
		Mockito.when(ConfigReader.getInstance()).thenReturn(configMock);
		Mockito.when(configMock.getKey("LOG_DIRECTORY")).thenReturn(testProperties.getProperty("LOG_DIRECTORY"));
		Mockito.when(configMock.getKey("LOG_FILENAME")).thenReturn(testProperties.getProperty("LOG_FILENAME"));
		
		String absolutePath = ConfigReader.getInstance().getKey("LOG_DIRECTORY") + 
				ConfigReader.getInstance().getKey("LOG_FILENAME") + ".txt";
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
