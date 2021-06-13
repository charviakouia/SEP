package loggerTest;

import de.dedede.model.persistence.exceptions.InvalidLogFileException;
import de.dedede.model.persistence.util.ConfigReader;
import de.dedede.model.persistence.util.Logger;
import tests.LendAndReturnCopyTest;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
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

public class LoggerTest {
	
	@BeforeAll
	public static void setUp() throws InvalidLogFileException {
		InputStream is = 
				LoggerTest.class.getResourceAsStream("..\\BiBi\\src\\main"
						+ "\\webapp\\WEB-INF\\config.properties");
    	ConfigReader.getInstance().setUpConfigReader(is);
    	Logger.logSetup();
	}
   	
	@Test
	public static void testLog() throws IOException {
		String testMessage = "This is a test message.";
		Logger.severe(testMessage);
		ConfigReader instance = ConfigReader.getInstance();
		FileReader reader = new FileReader(instance.getKey("LOG_DIRECTORY") 
				+ instance.getKey("LOG_FILENAME") + ".txt");
		BufferedReader bf = new BufferedReader(reader);
		StringBuilder content = new StringBuilder();
	    String line;
	    while ((line = bf.readLine()) != null) {
	        content.append(line);
	    }
	    bf.close();
	    reader.close();
	    Assertions.assertTrue(content.toString().contains(testMessage));
	}

}
