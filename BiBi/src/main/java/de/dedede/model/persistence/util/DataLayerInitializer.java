package de.dedede.model.persistence.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;
import java.util.Scanner;

import de.dedede.model.persistence.exceptions.InvalidSchemaException;
import de.dedede.model.persistence.exceptions.LostConnectionException;

/**
 * Initializes the application's utilities and environment.
 * This is required when the application is launched. 
 *
 */
public class DataLayerInitializer {
	
	private static final String[] tableNames = {"Users", "Application", "Medium", "CustomAttribute", "Copy", "AttributeType", "Category"}; 
	private static final String DB_DRIVER = "DB_DRIVER";
	private static final String DB_USER = "DB_USER";
	private static final String DB_PASSWORD = "DB_PASSWORD";
	private static final String DB_SSL = "DB_SSL";
	private static final String DB_HOST = "DB_HOST";
	private static final String DB_URL = "DB_URL";
	private static final String DB_SSL_FACTORY =  "DB_SSL_FACTORY";
	private static final String DB_PORT = "DB_PORT";
	private static final String DB_NAME = "DB_NAME";
	
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
		
		try {
			setUpConnectionPool();
		} catch (ClassNotFoundException cnfe) {
			Logger.severe("Database JDBC driver was not found during ConnectionPool initialization.");
			throw new LostConnectionException("Database driver encountered a problem during system start.");
		} catch (SQLException sqle) {
			Logger.severe("SQLException occured during database communication while initializing ConnectionPool.");
			throw new LostConnectionException("A problem occured while trying to connect to the database during system startup.");
		}
		
		try {
			setUpDatabase();
		} catch (SQLException sqle) {
			Logger.severe("SQLException occured during database initialization");
			throw new LostConnectionException("A problem occured while communicating with the database during system startup.");
		}
		
		setUpMaintenanceProcess();			
	}
	
	public static void shutdownDataLayer() {
		MaintenanceProcess.shutdown();
		ConnectionPool.destroyConnectionPool();
	}
	
	private static class CloseConnectionThread extends Thread {
		
		private Connection connection;
		
		public CloseConnectionThread(Connection connection) {
			this.connection=connection;
		}
		@Override
		public void run() {
		try {
			connection.close();
		} catch (SQLException ignored) {System.out.println("Shutdownhook fail");}}
		
	}
		
	private static void setUpDatabase() throws SQLException {
		Properties config = ConfigReader.getInstance().getSystemConfigurations();
		Properties connectionProperties = new Properties();
		Connection connection = null;
		try {
            System.out.println("Loading JDBC Driver.");
            Logger.detailed("Loading JDBC Driver.");
            Class.forName(config.getProperty(DB_DRIVER));
        }
        catch(ClassNotFoundException e) { //already thrown in setUpConnectionPool()
        	Logger.detailed("JDBC Driver not found on database initialization.");
            System.out.println("JDBC Driver not found.");
        }
		connectionProperties.setProperty("user", config.getProperty(DB_USER));
		connectionProperties.setProperty("password", config.getProperty(DB_PASSWORD));
		
		if (config.getProperty(DB_SSL).equalsIgnoreCase("TRUE")) {
			connectionProperties.setProperty("ssl", "true");
			connectionProperties.setProperty("sslfactory", config.getProperty(DB_SSL_FACTORY));
		}
		try {
            System.out.println("Testing Database Connection");
            String port = "";
            if (!config.getProperty(DB_PORT).equals("") || !(config.getProperty(DB_PORT) == null)) {
            	port = ":" + config.getProperty(DB_PORT);
            }
            connection = DriverManager.getConnection(config.getProperty(DB_URL)
                + config.getProperty(DB_HOST) + port + "/" + config.getProperty(DB_NAME), connectionProperties);
        }
        catch(SQLException e) {
        	Logger.severe("Getting Connection from driver failed on database initialization.");
            System.out.println("Failed to initialize database connection");
            throw e;
        } 
		if (connection != null) {
			Logger.development("Successful initial connection with database established.");
			System.out.println("Database connection was established successfully.");
			CloseConnectionThread thread = new CloseConnectionThread(connection);
			Runtime.getRuntime().addShutdownHook(thread);
			DatabaseMetaData metadata = connection.getMetaData();
			ResultSet resultSet = metadata.getTables(null, null, null, new String[]{"TABLE"});
			try {
				for (String str : tableNames) {
					resultSet.getString(str);
				}
				Logger.detailed("All required tables are present. Database is ready for use.");
				System.out.println("All required tables are present. Database is ready for use.");
			} catch (SQLException e) {
				Logger.severe("Not all or none of the required tables are present in the database.");
				System.out.println("Not all or none of the required tables are present in the database.");
				try {
					consoleDialogue(connection);
				} catch (IOException io) {
					Logger.severe("Unable to read console Input");
					System.out.println("Unable to read console Input");
				}
			}
			connection.close();
		} 
		
	}


	private static void setUpConnectionPool() throws SQLException, ClassNotFoundException {
		ConnectionPool.setUpConnectionPool();
	}
	
	private static void setUpMaintenanceProcess() {
		MaintenanceProcess mp = MaintenanceProcess.getInstance();
		mp.setup();
		mp.startup();
	}
	
	private static void consoleDialogue(Connection connection) throws IOException, SQLException {
		System.out.println("You can start a fresh database initialization now, "
				+ "this will overwrite existing tables with the names: "
				+ "\"Users\", \"Application\", \"Medium\", \"Category\", "
				+ "\"CustomAttribute\", \"AttributeType\", \"Copy\" "
				+ "Do you want to (re)create the tables, type 'y', else type 'n' but "
				+ "the application will not function correctly if tables are missing!");
		BufferedReader stdn = new BufferedReader(new InputStreamReader(System.in));
		boolean end = false;
		while (!end) {
			System.out.print("BiBi> ");
		    String input;
			input = stdn.readLine();
		    
			if (input == null) {
		        break;
		    }
			Scanner scanner = new Scanner(input);
			if (scanner.hasNext()) {
		    	String command = scanner.next();
		        char[] order = command.toCharArray();
		        //only the first letter of command is matched
		        char fc = order[0];
		        
		        switch (fc) {
		        case 'y':
		        	try {
		        	Logger.detailed("'Y' was selected, attempting fresh database initialization.");
		        	connection.setAutoCommit(false);
		        	PreparedStatement dropTables = connection.prepareStatement(
		        			"DROP TABLE IF EXISTS Users, Application, Medium, "
		        			+ "Category, CustomAttribute, AttributeType, Copy;"
		        			);
		        	dropTables.execute();
		        	connection.commit();
		        	initializeNewScheme(connection);
		        	initializeStandartAttributes(connection); //<----- To-Do!
		        	Logger.development("DB freshly initialized.");
		        	System.out.println("The database was initialized successfully.");	
		        	sampleEntries();																//	REMOVE IN PROD.!
		        	Logger.development("The database was populated with sample entries!");  		//  REMOVE IN PROD.!
		        	System.out.println("The database was populated with sample entries!");			//  REMOVE IN PROD.!
		        	} catch (SQLException sql) {
		        		Logger.severe("SQL Error while dropping/creating tables on DB initialization.");
		        		System.out.println("SQL Error while dropping/creating tables on DB initialization.");
		        		scanner.close();
		        		throw sql;
		        	}
		        	end = true;
		        	break;
		        case 'n': 
		        	Logger.detailed("'N' was selected, proceeding system start with potentially damaged/incomplete tables.");
		        	end = true;
		        	break;
		        default:
		        	System.out.println("First letter of command is parsed, expects 'y' or 'n' for Yes or No");
		        } 
		        scanner.close();
		   }
		}
	}
	
	private static void initializeStandartAttributes(Connection connection) {
		
	}
	
	private static void sampleEntries() {
		String s1 = "";
		
	}
	
	private static void initializeNewScheme(Connection connection) throws SQLException {
		String s1 = "CREATE TYPE UserLendStatus AS ENUM ("
				+ "	'DISABLED',"
				+ "	'ENABLED'"
				+ ");";
		String s2 = "CREATE TYPE UserVerificationStatus AS ENUM ("
				+ "	'VERIFIED',"
				+ "	'UNVERIFIED'"
				+ ");";
		String s3 = "CREATE TYPE UserRole AS ENUM ("
				+ "	'REGISTERED',"
				+ "	'STAFF',"
				+ "	'ADMIN'"
				+ ");";
		String s4 = "CREATE TYPE SystemAnonAccess AS ENUM ("
				+ "	'REGISTRATION',"
				+ "	'OPAC'"
				+ ");";
		String s5 = "CREATE TYPE RegisteredUserLendStatus AS ENUM ("
				+ "	'MANUAL',"
				+ "	'UNLOCKED'"
				+ ");";
		String s6 = "CREATE TYPE CopyStatus AS ENUM ("
				+ "	'BORROWED',"
				+ "	'READY_FOR_PICKUP',"
				+ "	'AVAILABLE'"
				+ ");";
		String s7 = "CREATE TYPE AttributeDataType AS ENUM ("
				+ "	'TEXT',"
				+ "	'IMAGE',"
				+ "	'LINK'"
				+ ");";
		String s8 = "CREATE TYPE AttributeMultiplicity AS ENUM ("
				+ "	'SINGLE_VALUED',"
				+ "	'MULTI_VALUED'"
				+ ");"
				+ "";
		String s9 = "CREATE TYPE MediumPreviewPosition AS ENUM ("
				+ "	'FIRST',"
				+ "	'SECOND',"
				+ "	'THIRD',"
				+ "	'FOURTH',"
				+ "	'HIDDEN'"
				+ ");";
		String s10 = "CREATE TYPE AttributeModifiability AS ENUM ("
				+ "	'MODIFIABLE',"
				+ "	'STATIC'"
				+ ");";
		String s11 = "CREATE TABLE Users ("
				+ "	userID SERIAL,"
				+ "	emailAddress VARCHAR(100) NOT NULL UNIQUE,"
				+ "	passwordHashSalt VARCHAR(40) NOT NULL,"
				+ "	passwordHash CHAR(64) NOT NULL,"
				+ "	name VARCHAR(100),"
				+ "	surname VARCHAR(100),"
				+ "	postalCode VARCHAR(20),"
				+ "	city VARCHAR(100),"
				+ "	street VARCHAR(100),"
				+ "	houseNumber VARCHAR(20),"
				+ "	token CHAR(20) UNIQUE,"
				+ "	userLendPeriod INTERVAL,"
				+ "	lendStatus USERLENDSTATUS NOT NULL DEFAULT 'DISABLED',"
				+ "	verificationStatus USERVERIFICATIONSTATUS NOT NULL DEFAULT 'UNVERIFIED',"
				+ "	userRole USERROLE NOT NULL DEFAULT 'REGISTERED',"
				+ "	PRIMARY KEY(userID),"
				+ "	tokenCreation TIMESTAMP,"
				+ "	CONSTRAINT positive_tokenCreation CHECK (tokenCreation >= NOW())"
				+ ");";
		String s12 = "CREATE TABLE Application ("
				+ "	one SERIAL,"
				+ "	bibName VARCHAR(100),"
				+ "	emailRegEx VARCHAR(200),"
				+ "	contactInfo TEXT,"
				+ "	imprintInfo TEXT,"
				+ "	privacyPolicy TEXT,"
				+ "	bibLogo BYTEA,"
				+ "	globalLendLimit INTERVAL,"
				+ "	globalMarkingLimit INTERVAL,"
				+ "	reminderOffset INTERVAL,"
				+ "	registrationStatus SYSTEMREGISTRATIONSTATUS NOT NULL DEFAULT 'OPEN',"
				+ "	lookAndFeel VARCHAR(40) NOT NULL,"
				+ "	anonRights SYSTEMANONACCESS NOT NULL DEFAULT 'OPAC',"
				+ "	userLendStatus REGISTEREDUSERLENDSTATUS NOT NULL DEFAULT 'UNLOCKED'"
				+ ");";
		String s13 = "CREATE TABLE Medium ("
				+ "	mediumID SERIAL,"
				+ "	mediumLendPeriod INTERVAL,"
				+ "	hasCategory INTEGER,"
				+ "	PRIMARY KEY(mediumID),"
				+ "	CONSTRAINT fk_Category FOREIGN KEY(hasCategory) REFERENCES Category(categoryID) ON DELETE SET NULL"
				+ ");";
		String s14 = "CREATE TABLE Category ("
				+ "	categoryID SERIAL,"
				+ "	title VARCHAR(100) NOT NULL,"
				+ "	description TEXT,"
				+ "	parentCategoryID INTEGER,"
				+ "	PRIMARY KEY(categoryID),"
				+ "	CONSTRAINT fk_Category FOREIGN KEY(parentCategoryID) REFERENCES Category(categoryID) ON DELETE CASCADE"
				+ ");";
		String s15 = "CREATE TABLE CustomAttribute ("
				+ "	attributeID SERIAL,"
				+ "	mediumID INTEGER NOT NULL,"
				+ "	attributeName VARCHAR(40) NOT NULL,"
				+ "	attributeValue BYTEA,"
				+ "	PRIMARY KEY(mediumID, attributeID),"
				+ "	CONSTRAINT fk_Medium FOREIGN KEY(mediumID) REFERENCES Medium(mediumID) ON DELETE CASCADE"
				+ ");";
		String s16 = "CREATE TABLE AttributeType ("
				+ "	typeID SERIAL,"
				+ "	attributeID INTEGER NOT NULL,"
				+ " mediumID INTEGER NOT NULL,"
				+ "	previewPosition MEDIUMPREVIEWPOSITION NOT NULL DEFAULT 'HIDDEN',"
				+ "	multiplicity ATTRIBUTEMULTIPLICITY NOT NULL DEFAULT 'SINGLE_VALUED',"
				+ "	modifiability ATTRIBUTEMODIFIABILITY NOT NULL DEFAULT 'MODIFIABLE',"
				+ "	attributeDataType ATTRIBUTEDATATYPE NOT NULL DEFAULT 'TEXT',"
				+ "	PRIMARY KEY(mediumID, attributeID, typeID),"
				+ "	CONSTRAINT fk_CustomAttribute FOREIGN KEY(attributeID, mediumID) REFERENCES CustomAttribute(attributeID, mediumID) ON DELETE CASCADE"
				+ ");";
		String s17 = "CREATE TABLE MediumCopy ("
				+ "	copyID SERIAL,"
				+ "	mediumID INTEGER NOT NULL UNIQUE,"
				+ "	signature VARCHAR(100) UNIQUE,"
				+ "	bibPosition VARCHAR(100),"
				+ "	status COPYSTATUS NOT NULL DEFAULT 'AVAILABLE',"
				+ "	deadline TIMESTAMP,"
				+ "	actor INTEGER,"
				+ "	PRIMARY KEY(mediumID, copyID),"
				+ "	CONSTRAINT fk_Medium FOREIGN KEY(mediumID) REFERENCES Medium(mediumID) ON DELETE CASCADE,"
				+ "	CONSTRAINT fk_User FOREIGN KEY(actor) REFERENCES Users(userID) ON DELETE RESTRICT,"
				+ "	CONSTRAINT positive_deadline CHECK ((deadline IS NULL) OR (deadline >= CURRENT_TIMESTAMP),"
				+ "	CONSTRAINT no_actor_available CHECK ((status != 'AVAILABLE'::COPYSTATUS) OR (actor IS NULL))"
				+ ");";
		String s18 = "CREATE OR REPLACE FUNCTION check_status_validity() RETURNS TRIGGER AS"
				+ "$BODY$"
				+ "	BEGIN"
				+ "		IF (OLD.status = 'BORROWED'::COPYSTATUS) AND (NEW.status = 'READY_FOR_PICKUP'::COPYSTATUS) THEN"
				+ "		RAISE EXCEPTION 'Cannot mark a lent copy' ;"
				+ "		ELSEIF (OLD.status = 'BORROWED'::COPYSTATUS) AND (NEW.status = 'BORROWED'::COPYSTATUS) THEN"
				+ "		RAISE EXCEPTION 'Copy is already lent' ;"
				+ "		ELSEIF (OLD.status = 'READY_FOR_PICKUP'::COPYSTATUS) AND (NEW.status = 'READY_FOR_PICKUP'::COPYSTATUS) THEN"
				+ "		RAISE EXCEPTION 'Copy is already marked' ;"
				+ "		ELSEIF (NEW.status = 'AVAILABLE'::COPYSTATUS) AND (NEW.actor IS NOT NULL) THEN"
				+ "		RAISE EXCEPTION 'An available copys actor must be NULL' ;"
				+ "		ELSEIF NOT ((OLD.status = 'READY_FOR_PICKUP'::COPYSTATUS AND NEW.status = 'BORROWED'::COPYSTATUS) AND (OLD.actor = NEW.actor)) THEN"
				+ "		RAISE EXCEPTION 'A marked copy can only be lent by the marking user!' ;"
				+ "		ELSEIF NOT (	   ((OLD.status = 'AVAILABLE'::COPYSTATUS) AND (NEW.status = 'BORROWED'::COPYSTATUS))"
				+ "						OR ((OLD.status = 'READY_FOR_PICKUP'::COPYSTATUS) AND (NEW.status = 'AVAILABLE'::COPYSTATUS))"
				+ "						OR ((OLD.status = 'AVAILABLE'::COPYSTATUS) AND (NEW.status = 'AVAILABLE'::COPYSTATUS))"
				+ "					) THEN"
				+ "		RAISE EXCEPTION 'Invalid operation' ;"
				+ "		RETURN NEW ;"
				+ "	END IF ;"
				+ "	END"
				+ "$BODY$"
				+ "LANGUAGE plpgsql;";
		String s19 = "CREATE TRIGGER on_status_update"
				+ "	BEFORE UPDATE OF status ON MediumCopy"
				+ "	FOR EACH ROW"
				+ "	EXECUTE PROCEDURE check_status_validity();";
		
		PreparedStatement createCustomTypes = connection.prepareStatement(s1);
		createCustomTypes.addBatch(s2);
		createCustomTypes.addBatch(s3);
		createCustomTypes.addBatch(s4);
		createCustomTypes.addBatch(s5);
		createCustomTypes.addBatch(s6);
		createCustomTypes.addBatch(s7);
		createCustomTypes.addBatch(s8);
		createCustomTypes.addBatch(s9);
		createCustomTypes.addBatch(s10);
		createCustomTypes.executeBatch();
		connection.commit();
		Logger.development("Executed first batch (enums) of statements on DB initialization.");
		PreparedStatement createIndependentTables = connection.prepareStatement(s11);
		createIndependentTables.addBatch(s12);
		createIndependentTables.addBatch(s14);
		createIndependentTables.executeBatch();
		connection.commit();
		Logger.development("Executed second batch (tables) of statements on DB initialization.");
		PreparedStatement createMedium = connection.prepareStatement(s13);
		createMedium.execute();
		connection.commit();
		Logger.development("Executed third batch (dependent tables) of statements on DB initialization.");
		PreparedStatement createDependentTables = connection.prepareStatement(s15);
		createDependentTables.addBatch(s17);
		createDependentTables.executeBatch();
		connection.commit();
		Logger.development("Executed fourth batch (further dependent tables and function) of statements on DB initialization.");
		PreparedStatement createAttributeTypeAndFunction = connection.prepareStatement(s16);
		createAttributeTypeAndFunction.addBatch(s18);
		createAttributeTypeAndFunction.executeBatch();
		connection.commit();
		Logger.development("Executed last batch (trigger) of statements on DB initialization.");
		PreparedStatement createTrigger = connection.prepareStatement(s19);
		createTrigger.execute();
		connection.commit();
	}


}
