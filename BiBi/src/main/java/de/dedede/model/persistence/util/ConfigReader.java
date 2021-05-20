package de.dedede.model.persistence.util;

import java.util.Properties;

/**
 * A singleton utility class for returning system-wide property values.
 * 
 * See the {@link Properties} class for the implementation of properties in Java.
 *
 */
public class ConfigReader {

	private ConfigReader() {}

	/**
	 * Returns the single instance of the ConfigReader.
	 * 
	 * @return The singleton ConfigReader instance
	 */
	public static ConfigReader getInstance() {
		return null;
	}
	
	/**
	 * Returns a properties-object containing the system configurations.
	 * This properties-object only permits read-only operations.
	 * 
	 * @return The system configurations
	 */
	public Properties getSystemConfigurations() {
		return null;
	}
	
}
