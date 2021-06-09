package de.dedede.model.persistence.util;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

import de.dedede.model.persistence.exceptions.InvalidConfigurationException;
import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;

/**
 * A singleton utility class for returning system-wide property values.
 * 
 * See the {@link Properties} class for the implementation of properties in Java
 * 
 * @author Jonas Picker
 */
public class ConfigReader {
	
	/**
	 * Implicitly synchronized singleton-pattern to avoid 'synchronized' 		
	 * bottleneck on getInstance(). 
	 */
	private static final class InnerInstance {									//Changed to runtimeexception?!
		static final ConfigReader InnerInstance = new ConfigReader();			//Bonus: default values behandeln?
	}
	
	/**
	 * Holds the Properties Object with system configurations.
	 */
	private Properties systemConfigurations = new Properties();
	
	/**
	 * Needed as basis for loading config with webapp-relative path.
	 */
	private ExternalContext ext = 
			findContext().getExternalContext();
	
	/**
	 * The path to the config-File starting from the webapp-Folder.
	 */
	private Path relativeFilePath = Paths.get("WEB-INF", "config.properties");
	
	/**
	 * Singleton with private constructor. Initializes the ConfigReader by 
	 * reading the config-File.
	 * 
	 * @throws InvalidConfigurationException if the file couldn't be read
	 */
	private ConfigReader() {
		String pathString = relativeFilePath.toString();
		InputStream stream = ext.getResourceAsStream(pathString);
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
	 * @return The value for the key or the default value if the original 
	 * 			value is empty
	 */
	public String getKey(String key, String defaultValue)  {
		
		return systemConfigurations.getProperty(key, defaultValue);
	}
	
	private FacesContext findContext() {
		
		return FacesContext.getCurrentInstance();
	}

}
