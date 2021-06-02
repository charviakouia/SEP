package de.dedede.model.persistence.util;

import java.util.Properties;

import de.dedede.model.persistence.exceptions.InvalidSchemaException;
import de.dedede.model.persistence.exceptions.LostConnectionException;

/**
 * Initializes the application's utilities and environment.
 * This is required when the application is launched. 
 *
 */
public final class DataLayerInitializer {
	
	/**
	 * Performs tasks associated with the initialization of the data layer. 
	 * This includes setting up the data store, the connection pool, and
	 * the maintenance process.
	 * 
	 * @throws LostConnectionException Is thrown if a connection could
	 * 		not be established to the data store to perform the initialization.
	 * @throws InvalidSchemaException
	 * @see ConnectionPool
	 * @see MaintenanceProcess
	 */
	public static void execute() throws LostConnectionException, InvalidSchemaException {
		setUpDatabase();
		setUpConnectionPool();
		setUpMaintenanceProcess();
	}
	
	
	
	private static void setUpDatabase() throws InvalidSchemaException, LostConnectionException {
		
	}
	
	private static void setUpConnectionPool() {
		
	}
	
	private static void setUpMaintenanceProcess() throws LostConnectionException {
		Properties config = ConfigReader.getInstance().getSystemConfigurations();
		Properties connection = new Properties();
	}

	

}
