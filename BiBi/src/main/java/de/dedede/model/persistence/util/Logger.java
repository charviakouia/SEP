package de.dedede.model.persistence.util;

/**
 * Defines a thread-safe logging utility, which is implemented with static 
 * methods for different logging levels. In particular, these are: 
 * Development, detailed, and severe. The logs are written to a file, whose
 * location is specified in the global application configurations.
 *
 */
public final class Logger {

	private Logger() {}

	/**
	 * Writes a severe message to the log. Severe messages are reserved
	 * for events, from which the system cannot recover without interrupting
	 * the application.
	 * 
	 * @param message The message which accompanies the log entry.
	 */
	public static void severe(String message) {
		log(LogLevel.SEVERE, message);
	}

	/**
	 * Writes a detailed message to the log. Detailed messages are reserved
	 * for normal events.
	 * 
	 * @param message The message which accompanies the log entry.
	 */
	public static void detailed(String message) {
		log(LogLevel.DETAILED, message);
	}

	/**
	 * Writes a development message to the log. Development messages are reserved
	 * for events, which could later be used to diagnose an error.
	 * 
	 * @param message The message which accompanies the log entry.
	 */
	public static void development(String message) {
		log(LogLevel.DEVELOPMENT, message);
	}

	private static void log(LogLevel level, String message) {
	}
	
	public static enum LogLevel {
		SEVERE, DETAILED, DEVELOPMENT
	}

}
