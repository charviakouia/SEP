package de.dedede.model.logic.util;

import java.io.InputStream;
import java.util.ResourceBundle;

import de.dedede.model.data.dtos.ApplicationDto;
import de.dedede.model.persistence.daos.ApplicationDao;
import de.dedede.model.persistence.exceptions.DriverNotFoundException;
import de.dedede.model.persistence.exceptions.InvalidConfigurationException;
import de.dedede.model.persistence.exceptions.InvalidLogFileException;
import de.dedede.model.persistence.exceptions.LostConnectionException;
import de.dedede.model.persistence.util.ConfigReader;
import de.dedede.model.persistence.util.DataLayerInitializer;
import de.dedede.model.persistence.util.Logger;
import jakarta.faces.application.Application;
import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;
import jakarta.faces.event.AbortProcessingException;
import jakarta.faces.event.PostConstructApplicationEvent;
import jakarta.faces.event.PreDestroyApplicationEvent;
import jakarta.faces.event.SystemEvent;
import jakarta.faces.event.SystemEventListener;

/**
 * Conducts and relays necessary actions before the system is shutdown 
 * or after it was started. Is registered in the faces-config.xml.
 *  
 *  @author Jonas Picker
 *  
 */
public class SystemStartStop implements SystemEventListener {
	
	/**
	 * The config-files path relative to /webapp folder
	 */
	private static final String relative = "WEB-INF/config.properties";
	
	/**
	 * The id of the database entry used to hold the application configuration.
	 */
	private static final long appDataId = 1;
			
	/** @inheritDoc
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
	 * access controls after finally passing on the event to the 
	 * data layer on system start.
	 * 
	 * @throws LostConnectionException If data layer failed to communicate to DB
	 * @throws DriverNotFoundException If JDBC driver wasn't found 
	 * @throws InvalidConfigurationException if config File couldn't be read
	 */
	private void initializeApplication() { 
		FacesContext fctx = FacesContext.getCurrentInstance();
		ExternalContext ctx = fctx.getExternalContext();
		InputStream is = ctx.getResourceAsStream(relative);
		ConfigReader.getInstance().setUpConfigReader(is);						
		System.out.println("ConfigReader initialized");
		try {
			if (Logger.logSetup()) {
				System.out.println("The log-File was newly created.");
			} else {
				System.out.println("Log-File already exists.");
			}
		} catch (InvalidLogFileException e) {
			System.out.println("System will proceed without a permanent log,"
					+ " set LOG_CONSOLE to 'TRUE' or restart the system with"
					+ " a new log-File path.");
		}
		if (EmailUtility.initializeConnection()) { 
			Logger.detailed("Successful connection with the mail "
					+ "server established.");
			System.out.println("Connected to the Mailserver successfully.");
		} else {
			Logger.severe("Failed to connect to the Mail Server.");
			System.out.println("Failed to connect to the Mail Server.");
		}
		Application application = fctx.getApplication();
		ResourceBundle messages = application.evaluateExpressionGet(fctx, 
				"#{msg}", ResourceBundle.class);
		String emailSubject = messages.getString("reminderEmail.return_copy"
				+ "_reminder_subject");
		String emailBody = messages.getString("reminderEmail.return_copy"
				+ "_reminder_email");
		DataLayerInitializer.execute(emailSubject, emailBody);
		ApplicationDto idContainer = new ApplicationDto();
		idContainer.setId(appDataId);
		ApplicationDto appData = ApplicationDao.readCustomization(idContainer);
		SystemAnonAccess accessMode = appData.getAnonRights();
		TrespassListener.setAccessMode(accessMode);
		Logger.development("Set anonymous user access rights to: " 
				+ accessMode.toString());
	}
	
	private void shutdownApplication() {
		DataLayerInitializer.shutdownDataLayer();
	}
	
	/**
	 *@inheritDoc
	 */
	@Override
	public boolean isListenerForSource(Object source) {
		
		return (source instanceof Application);
	}
}
