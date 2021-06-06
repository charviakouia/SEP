package de.dedede.model.logic.util;

import java.io.IOException;

import de.dedede.model.persistence.exceptions.DriverNotFoundException;
import de.dedede.model.persistence.exceptions.InvalidConfigurationException;
import de.dedede.model.persistence.exceptions.InvalidLogFileException;
import de.dedede.model.persistence.exceptions.LostConnectionException;
import de.dedede.model.persistence.util.ConfigReader;
import de.dedede.model.persistence.util.DataLayerInitializer;
import de.dedede.model.persistence.util.Logger;
import jakarta.faces.application.Application;
import jakarta.faces.event.AbortProcessingException;
import jakarta.faces.event.PostConstructApplicationEvent;
import jakarta.faces.event.PreDestroyApplicationEvent;
import jakarta.faces.event.SystemEvent;
import jakarta.faces.event.SystemEventListener;

/**
 *  Conducts and relays necessary actions before the system is shutdown 
 *  or after it was started. Is registered in the faces-config.xml.
 *  
 *  @author Jonas Picker
 *  
 */
public class SystemStartStop implements SystemEventListener {
		
   /** {@inheritDoc}
   */
	@Override
	public void processEvent(SystemEvent systemEvent) 
			throws AbortProcessingException {
		try {	
			if (systemEvent instanceof PostConstructApplicationEvent) {
				initializeApplication();
			} else if (systemEvent instanceof PreDestroyApplicationEvent) {
				shutdownApplication();
			}
		} catch (Exception e) {
				System.out.println("Initialization process on system "
						+ "startup failed critically!");
				throw new AbortProcessingException("Initialization "
						+ "process on system startup failed critically!", e);
		}
	}

	/**
	 * Turns on first ConfigReader, then Logger followed by EmailUtility and 
	 * finally passes on the event to the data layer on system start.
	 * 
	 * @throws LostConnectionException If data layer failed to communicate to DB
	 * @throws DriverNotFoundException If JDBC driver wasn't found 
	 * @throws InvalidConfigurationException If config file is unreadable
	 */
	private void initializeApplication() throws LostConnectionException, 
	DriverNotFoundException, InvalidConfigurationException {
		ConfigReader config = ConfigReader.getInstance();
		try {
			config.setupConfigReader();
		} catch (IOException e1) {
			System.out.println("Critical error while trying to access"
					+ " system configurations!");
			throw new InvalidConfigurationException("Critical error while "
					+ "trying to read system configuration file!", e1);
		} 
		System.out.println("ConfigReader initialized");
		try {
			if (Logger.logSetup()) {
				System.out.println("The log-File was newly created.");
			} else {
				System.out.println("Log-File already exsits.");
			}
		} catch (InvalidLogFileException e) {
			System.out.println("System will proceed without a permanent log,"
					+ " set LOG_CONSOLE to 'TRUE' or restart the system with"
					+ " a new log-File path.");
		}
		if (EmailUtility.initializeConnection()) { //maybe Exception Handling when Email-Utility is finished?
			Logger.detailed("Successful connection with the mail "
					+ "server established.");
			System.out.println("Connected to the Mailserver successfully.");
		} else {
			Logger.severe("Failed to connect to the Mail Server.");
			System.out.println("Failed to connect to the Mail Server.");
		}
		
		DataLayerInitializer.execute();
	}
	
	private void shutdownApplication() {
		DataLayerInitializer.shutdownDataLayer();
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
