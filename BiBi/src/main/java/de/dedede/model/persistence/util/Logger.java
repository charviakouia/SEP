package de.dedede.model.persistence.util;

import java.io.BufferedWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Properties;

/**
 * Defines a thread-safe logging utility, which is implemented with static 
 * methods for different logging levels. In particular, these are: 
 * Development, detailed, and severe. The logs are written to a file, whose
 * location is specified in the global application configurations.
 * 
 * @author Jonas Picker
 *
 */
public final class Logger { //TO-DO: ExceptionHandling, Testing

	private Logger() {}
	
	/**
	 * Initializes the Logger by creating a new .txt-File in the config-specified directory, if it isn't already present.
	 * 
	 * @return true if the file was created anew
	 */
	public static boolean logSetup() {
		Properties config = ConfigReader.getInstance().getSystemConfigurations();
		try {
			File logFile = new File(config.getProperty("LOG_DIRECTORY") + config.getProperty("LOG_FILENAME") + ".txt");
			return logFile.createNewFile();
		} catch (Exception e) {
			//handle Exception
			System.out.println("logSetup failed (IO or no write permission)");
			return false;
		}
	}

	/**
	 * Writes a severe message to the log. Severe messages are reserved
	 * for events, from which the system cannot recover without interrupting
	 * the application.
	 * 
	 * @param message The message which accompanies the log entry.
	 */
	public static synchronized void severe(String message) {
		Properties config = ConfigReader.getInstance().getSystemConfigurations();
		log(config, LogLevel.SEVERE, message);
	}

	/**
	 * Writes a detailed message to the log. Detailed messages are reserved
	 * for normal events.
	 * 
	 * @param message The message which accompanies the log entry.
	 */
	public static synchronized void detailed(String message) {
		Properties config = ConfigReader.getInstance().getSystemConfigurations();
		String level = config.getProperty("LOG_LEVEL");
		
		if (level.equalsIgnoreCase("DEVELOPMENT") || level.equalsIgnoreCase("DETAILED")) {
			log(config, LogLevel.DETAILED, message);
		}
	}

	/**
	 * Writes a development message to the log. Development messages are reserved
	 * for events, which could later be used to diagnose an error.
	 * 
	 * @param message The message which accompanies the log entry.
	 */
	public static synchronized void development(String message) {
		Properties config = ConfigReader.getInstance().getSystemConfigurations();
		
		if (config.getProperty("LOG_LEVEL").equalsIgnoreCase("DEVELOPMENT")) {
			log(config, LogLevel.DEVELOPMENT, message);
		}
	}

	private static void log(Properties config, LogLevel level, String message) {
		String consoleOutput = config.getProperty("LOG_CONSOLE");
		
		if (consoleOutput.equalsIgnoreCase("TRUE")) {
			System.out.println(level.toString() + ": " + message);
		}
		logToFile(config, level, message);
	}

	private static void logToFile(Properties config, LogLevel level, String message) {
		SimpleDateFormat timestampFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(config.getProperty("LOG_DIRECTORY") + config.getProperty("LOG_FILENAME") + ".txt", true));
			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			writer.append(level.toString() + ": " + timestampFormat.format(timestamp) + " ---> " + message + System.getProperty("line.separator"));
			writer.close();
		} catch (IOException e) {
			//handle Exception
		}
	}
	
	private static enum LogLevel {
		SEVERE, DETAILED, DEVELOPMENT
	}

}
