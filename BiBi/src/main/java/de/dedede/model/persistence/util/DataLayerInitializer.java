package de.dedede.model.persistence.util;

import de.dedede.model.persistence.exceptions.InvalidSchemaException;
import de.dedede.model.persistence.exceptions.LostConnectionException;

/**
 * Initializes the application's utilities and environment.
 * This is required when the application is launched. 
 *
 */
public class DataLayerInitializer {

	private static void setUpConnectionPool() throws LostConnectionException {
		
	}
	
	private static void setUpDatabase() throws InvalidSchemaException {

	}
	
	private static void setUpMaintenanceProcess() throws LostConnectionException {
		
	}

	/**
	 * Performs tasks associated with the initialization of the data layer. 
	 * This includes setting up the data store, the connection pool, and
	 * the maintenance process.
	 * 
	 * @throws LostConnectionException Is thrown if a connection could
	 * 		not be established to the data store to perform the initialization.
	 * @see ConnectionPool
	 * @see MaintenanceProcess
	 */
	public static void execute() throws LostConnectionException {
		setUpDatabase();
		setUpConnectionPool();
		setUpMaintenanceProcess();
	}

}
