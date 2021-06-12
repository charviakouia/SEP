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

import de.dedede.model.persistence.util.ConfigReader;

public class PathTest {
	
	private static String sep = File.separator;
	private static String up = "..";
	
	private static String path = up + sep 
			+ up + sep + up + sep + up + sep + up + sep + "BiBi" + sep + "src" 
			+ sep + "main" + sep + "webapp" + sep + "WEB-INF" 
			+ sep + "config.txt";
	
	public static void main(String[] a) {
		Properties testProperties = new Properties();
		try {
			InputStream stream = new FileInputStream(up + sep + "BiBi" + sep + "src" 
					+ sep + "main" + sep + "webapp" + sep + "WEB-INF" 
					+ sep + "config.properties");
			testProperties.load(stream);
			stream.close();
		} catch (Exception ignored) {System.err.println(ignored);}
		File configFile = new File(testProperties.getProperty("LOG_DIRECTORY") + 
				testProperties.getProperty("LOG_FILENAME") + ".txt");
		boolean created = false;
		try {
			created = configFile.createNewFile();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(created);
		System.out.println(configFile.getAbsolutePath());
		String testMessage = "All required tables seem to be present.";
		String result = "";
		try {
			Scanner scanner = new Scanner(configFile);
			StringBuilder sb = new StringBuilder();
			String line = "";
			while( scanner.hasNext()) {
				sb.append(line);
				System.out.println(line);
			}
			scanner.close();
			result = sb.toString();
			System.out.println(result.contains(testMessage));
		} catch (Exception ignored) {
			System.err.println(ignored);
		}
	}
}
