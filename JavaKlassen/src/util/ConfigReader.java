package util;

import java.util.Properties;

@Named
@ApplicationScoped
public class ConfigReader {
	
	private final static Logger logger = null;
	
	private final static  Properties configFile;

	private final static  MaintenanceProcesse maintenanceProcesse;
	
	
	
	private ConfigReader() {
		
	}
	
	public static ConfigReader getInstance() {
		return null;
	}
	
}
