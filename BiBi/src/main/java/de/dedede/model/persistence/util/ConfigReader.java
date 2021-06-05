package de.dedede.model.persistence.util;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

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
	private static final class InnerInstance {
		static final ConfigReader InnerInstance = new ConfigReader();
	}
	
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
	 * Singleton with private constructor.
	 */
	private ConfigReader() {}

	private FacesContext findContext() {
		
		return FacesContext.getCurrentInstance();
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
	
	public void setupConfigReader() throws IOException {
		Properties testProperties = new Properties();
		String pathString = relativeFilePath.toString();
		InputStream stream = ext.getResourceAsStream(pathString);
		testProperties.load(stream);		
		stream.close();	
		System.out.println("Testreading configuration file successful.");
	}
	
	/**
	 * Returns a properties-object containing the system configurations.
	 * This properties-object permits read-only operations.
	 * 
	 * @return The system configurations
	 */
	public Properties getSystemConfigurations() {
		Properties config = new Properties();
		try {
			String pathString = relativeFilePath.toString();
			InputStream stream = ext.getResourceAsStream(pathString);
			config.load(stream);		
			stream.close();			
		} catch (Exception e) {
			Logger.severe("Failed to read system configurations.");
			System.out.println("Unable to read system configuration file.");
		}
		
		return config;
	}
	
}
