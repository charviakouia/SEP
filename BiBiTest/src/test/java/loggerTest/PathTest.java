package loggerTest;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.Properties;
import java.util.Scanner;

import de.dedede.model.persistence.exceptions.InvalidLogFileException;
import de.dedede.model.persistence.util.ConfigReader;
import de.dedede.model.persistence.util.Logger;

public class PathTest {
	
	private static String sep = File.separator;
	private static String up = "..";
	
	private static String path =  up + sep + "BiBi" + sep + "src" 
			+ sep + "main" + sep + "webapp" + sep + "WEB-INF" 
			+ sep + "config.txt";
	
	public static void main(String[] args) {
		InputStream is = 
				PathTest.class.getClassLoader().getResourceAsStream(path);
    	ConfigReader.getInstance().setUpConfigReader(is);
    	try {
			Logger.logSetup();
		} catch (InvalidLogFileException e) {
			System.out.println("InvalidLogFileException");
		}
    	System.out.println(ConfigReader.getInstance().getKey("LOG_DIRECTORY"));
    	return;
	}
}
