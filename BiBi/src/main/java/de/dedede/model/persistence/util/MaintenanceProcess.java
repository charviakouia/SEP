package de.dedede.model.persistence.util;

/**
 * Implements a runnable maintenance process as a singleton.
 * When run by a thread, the maintenance process will check
 * for inconsistent data entries in the application's data
 * store with a given frequency.
 *
 */
public final class MaintenanceProcess implements Runnable {
	
	private static int timeoutInterval;
	
	private static MaintenanceProcess instance;
	
	/**
	 * Sets the frequency, with which a maintenance process 
	 * instance queries the data store. Takes immediate effect.
	 * 
	 * @param interval The new time interval between checks.
	 */
	public static void setTimeoutInterval(int interval) {
		
	}
	
	private MaintenanceProcess() {}
	
	/**
	 * Returns the single instance of the MaintenanceProcess.
	 * 
	 * @return The singleton MaintenanceProcess instance
	 */
	public MaintenanceProcess getInstance() {
		if (instance == null) {
			instance = new MaintenanceProcess();
		}
		return instance;
	}
	
	/**
	 * Executes the maintenance process between set intervals.
	 * This method will cause the calling thread to sleep and 
	 * is therefore intended to be executed by a stand-alone 
	 * worker thread.
	 * 
	 */
	@Override
	public void run() {
		
	}
	
	/**
	 * Instructs the thread to execute any urgent tasks and shut 
	 * down in a graceful manner. After calling this method once,
	 * it has no effect.
	 * 
	 */
	public void shutdown() {}
	
}
