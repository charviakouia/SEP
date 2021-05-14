package dedede.model.persistence.util;

public final class Logger {

	private Logger() {
		throw new IllegalStateException();
	}

	public static void severe(String message) {
		log(LogLevel.SEVERE, message);
	}

	public static void detailed(String message) {
		log(LogLevel.DETAILED, message);
	}

	public static void development(String message) {
		log(LogLevel.DEVELOPMENT, message);
	}

	private static void log(LogLevel level, String message) {
	}

}
