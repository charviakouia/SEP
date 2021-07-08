package de.dedede.model.persistence.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import de.dedede.model.persistence.exceptions.InvalidConfigurationException;


/**
 * A singleton utility class for returning system-wide property values.
 * See the {@link Properties} class for the implementation of properties in Java
 * 
 * @author Jonas Picker
 */
public class ConfigReader {
	
	/**
	 * Implicitly synchronized singleton-pattern to avoid 'synchronized' 		
	 * bottleneck on getInstance(). 
	 */
	private static final class InnerInstance {									
		static final ConfigReader InnerInstance = new ConfigReader();			
	}
	
	/**
	 * Holds the Properties Object with system configurations.
	 */
	private Properties systemConfigurations = new Properties();
			
	/**
	 * Singleton with private constructor.
	 */
	private ConfigReader() {}
	
	/**
	 * Initial reading of the configuration file is done here
	 * 
	 * @param stream an InputStream with the configurationFile
	 */
	public void setUpConfigReader(InputStream stream) {
		try {
			systemConfigurations.load(stream);
			stream.close();	
		} catch (IOException e) {
			System.out.println("Couldn't read config-File.");
			throw new InvalidConfigurationException("Critical Error while "
					+ "reading config File", e);
		}		
		System.out.println("System Configurations initialized.");
	}
	
	// Ivan wrote this - workaround to make tests work
	public void setUpConfigReader(Properties props) {
		systemConfigurations = props;
	}
	
	/**
	 * Returns the single instance of the ConfigReader. Synchronized implicitly 
	 * by the ClassLoader.
	 * 
	 * @return The singleton ConfigReader instance
	 */
	public static ConfigReader getInstance() {
		return InnerInstance.InnerInstance;
	}
	
	/**
	 * Returns a String containing the system configuration under the specified
	 * key.
	 * 
	 * @param String key the name of the config variable key.
	 * @return The value for the key
	 */
	public String getKey(String key) {
		return systemConfigurations.getProperty(key);
	}
	
	
	/**
	 * Returns a String containing the system configuration under the specified
	 * key.
	 * 
	 * @param String key the name of the config variable key.
	 * @return The value for the key parsed and returned as an int
	 */
	public int getKeyAsInt(String key) {
		return Integer.parseInt(systemConfigurations.getProperty(key));
	}
	
	/**
	 * Returns a String containing the system configuration under the specified
	 * key.
	 * 
	 * @param String key the name of the config variable key.
	 * @param defaultValue the default value if no value for the key is found
	 * @return The value for the key or the default value if the original 
	 * 			value is empty
	 */
	public String getKey(String key, String defaultValue)  {
		return systemConfigurations.getProperty(key, defaultValue);
	}
	
	/**
	 * Returns a String containing the system configuration under the specified
	 * key.
	 * 
	 * @param String key the name of the config variable key.
	 * @param defaultValue the default value if no value for the key is found
	 * @return The value for the key parsed and returned as an int
	 */
	public int getKeyAsInt(String key, int defaultValue) {
		return Integer.parseInt(systemConfigurations.getProperty(key, 
				String.valueOf(defaultValue)));
	}
	
}