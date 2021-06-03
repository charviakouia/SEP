package de.dedede.model.logic.util;

import de.dedede.model.persistence.exceptions.InvalidSchemaException;
import de.dedede.model.persistence.exceptions.LostConnectionException;
import de.dedede.model.persistence.util.ConfigReader;
import de.dedede.model.persistence.util.DataLayerInitializer;
import de.dedede.model.persistence.util.Logger;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.faces.application.Application;
import jakarta.faces.event.AbortProcessingException;
import jakarta.faces.event.PostConstructApplicationEvent;
import jakarta.faces.event.PreDestroyApplicationEvent;
import jakarta.faces.event.SystemEvent;
import jakarta.faces.event.SystemEventListener;

/**
 *  Conducts and relays necessary actions before the system is shutdown or after it was started. Is registered in the faces-config.xml.
 */
@ApplicationScoped
public class SystemStartStop implements SystemEventListener {
	
	@PostConstruct
	public void init() {
		System.out.println("Starting system...");
	}
	
   /** {@inheritDoc}
   */
	@Override
	public void processEvent(SystemEvent systemEvent) throws AbortProcessingException {
		try {
				
			if (systemEvent instanceof PostConstructApplicationEvent) {
				initializeApplication();
			
			} else if (systemEvent instanceof PreDestroyApplicationEvent) {
				shutdownApplication();
			}
			} catch (Exception e) {
				System.out.println("Initialization process on system startup failed");
				//throw new AbortProcessingException();
			}
	}

	

	private void initializeApplication() throws InvalidSchemaException, LostConnectionException {
		ConfigReader config = ConfigReader.getInstance();
		config.getSystemConfigurations(); //Tests the config-reading process
		System.out.println("ConfigReader initialized");
		if (Logger.logSetup()) {
			System.out.println("The log-File was newly created.");
		} else {
			System.out.println("Log-File already exsits.");
		}
		
		if (EmailUtility.initializeConnection()) {
			System.out.println("Connected to the Mailserver successfully.");
		} else {
			System.out.println("Failed to connect to the Mail Server.");
		}
		
		DataLayerInitializer.execute();
		
	
	}
	
	private void shutdownApplication() {
	
	}
	
	/**
	 *{@inheritDoc}
	 *
	 */
	@Override
	public boolean isListenerForSource(Object source) {
		return (source instanceof Application);
	}
}
