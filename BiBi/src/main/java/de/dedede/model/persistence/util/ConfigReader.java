package de.dedede.model.persistence.util;

import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;

/**
 * A singleton utility class for returning system-wide property values.
 * 
 * See the {@link Properties} class for the implementation of properties in Java.
 * 
 * @author Jonas Picker
 */
public class ConfigReader {
	
	/**
	 * Implicitly synchronized singleton-pattern to avoid 'synchronized' bottleneck on getInstance(). 
	 */
	private static final class InnerInstance {
		static final ConfigReader InnerInstance = new ConfigReader();
	}
	
	/**
	 * Needed as basis for loading config with webapp-relative path.
	 */
	private ExternalContext ext = FacesContext.getCurrentInstance().getExternalContext();
	
	/**
	 * The path to the config-File starting from the webapp-Folder.
	 */
	private Path relativeFilePath = Paths.get("WEB-INF", "config.properties");
	
	/**
	 * Singleton with private constructor.
	 */
	private ConfigReader() {}

	/**
	 * Returns the single instance of the ConfigReader. Synchronized implicitly by the ClassLoader.
	 * 
	 * @return The singleton ConfigReader instance
	 */
	public static ConfigReader getInstance() {
		
		return InnerInstance.InnerInstance;
	}
	
	/**
	 * Returns a properties-object containing the system configurations.
	 * This properties-object only permits read-only operations.
	 * 
	 * @return The system configurations
	 */
	public Properties getSystemConfigurations() { // eigene Exception werfen?
		Properties config = new Properties();
		try {
			InputStream stream = ext.getResourceAsStream(relativeFilePath.toString());
			config.load(stream);		
			stream.close();			
		} catch (Exception e) {
			//HandleEception
			System.out.println("getSysConfig failed");
		}
		
		return config;
	}
	
}
