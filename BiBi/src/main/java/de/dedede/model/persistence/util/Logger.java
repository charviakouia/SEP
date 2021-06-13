package de.dedede.model.persistence.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

import de.dedede.model.persistence.exceptions.InvalidLogFileException;

/**
 * Defines a thread-safe logging utility, which is implemented with static 
 * methods for different logging levels. In particular, these are: 
 * Development, detailed, and severe. The logs are written to a file, whose
 * location is specified in the global application configurations.
 * 
 * @author Jonas Picker
 *
 */
public final class Logger { 

	private Logger() {}
	
	/**
	 * Initializes the Logger by creating a new .txt-File in the 
	 * config-specified directory, if it isn't already present.
	 * 
	 * @return true if the file was created anew
	 * @throws InvalidLogFileException if the Log File coulnd't be read
	 */
	public static boolean logSetup() throws InvalidLogFileException {
		boolean newFileCreated = false;
		ConfigReader configReader = ConfigReader.getInstance();
		try {
			File logFile = new File(configReader.getKey("LOG_DIRECTORY") + 
					configReader.getKey("LOG_FILENAME") + ".txt");
			newFileCreated = logFile.createNewFile();							
		} catch (IOException e) {
			System.out.println("log-Setup failed with IOException.");			//Tomcat log?
		} catch (SecurityException se) {
			System.out.println("log-Setup failed due to denied"
					+ " write permissions.");									
			throw new InvalidLogFileException("Insufficient access "
					+ "permissions to specified log-file path.", se);
		}
		
		return newFileCreated;
	}

	/**
	 * Writes a severe message to the log. Severe messages are reserved
	 * for events, from which the system cannot recover without interrupting
	 * the application.
	 * 
	 * @param message The message which accompanies the log entry.
	 */
	public static synchronized void severe(String message) {
		log(LogLevel.SEVERE, message);
	}

	/**
	 * Writes a detailed message to the log. Detailed messages are reserved
	 * for normal events.
	 * 
	 * @param message The message which accompanies the log entry.
	 */
	public static synchronized void detailed(String message) {
		ConfigReader config = ConfigReader.getInstance();
		String level = config.getKey("LOG_LEVEL", "SEVERE");
		
		if (level.equalsIgnoreCase("DEVELOPMENT") 
				|| level.equalsIgnoreCase("DETAILED")) {
			log(LogLevel.DETAILED, message);
		}
	}

	/**
	 * Writes a development message to the log. Development messages 
	 * are reserved for events, which could later be used to diagnose an error.
	 * 
	 * @param message The message which accompanies the log entry.
	 */
	public static synchronized void development(String message) {
		ConfigReader config = ConfigReader.getInstance();
				
		String logLevel = config.getKey("LOG_LEVEL", "SEVERE");
		if (logLevel.equalsIgnoreCase("DEVELOPMENT")) {
			log(LogLevel.DEVELOPMENT, message);
		}
	}

	private static void log(LogLevel level, String message) {
		ConfigReader config = ConfigReader.getInstance();
		String consoleOutput = config.getKey("LOG_CONSOLE", "FALSE");
		
		if (consoleOutput.equalsIgnoreCase("TRUE")) {
			System.out.println(level.toString() + ": " + message);
		}
		logToFile(level, message);
	}

	private static void logToFile(LogLevel level, String message) {
		SimpleDateFormat timestampFormat = new SimpleDateFormat("yyyy-MM-dd "
				+ "HH:mm:ss");
		ConfigReader config = ConfigReader.getInstance();
		try {
			String logDirectory = config.getKey("LOG_DIRECTORY");
			String fileName = config.getKey("LOG_FILENAME", "BiBiLog");
			FileWriter fw = new FileWriter(logDirectory + fileName 
					+ ".txt", true);
			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			String messageFormat = level.toString() + ": " + 
			timestampFormat.format(timestamp) + " ---> " + message + 
			System.getProperty("line.separator");
			fw.write(messageFormat);
			fw.close();
		} catch (IOException e) {
			System.out.println("IOException while trying to log to file.");
		}
	}
	
	private static enum LogLevel {
		SEVERE, DETAILED, DEVELOPMENT
	}

}
