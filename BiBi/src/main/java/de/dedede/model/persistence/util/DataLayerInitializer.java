package de.dedede.model.persistence.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

import de.dedede.model.persistence.exceptions.DriverNotFoundException;
import de.dedede.model.persistence.exceptions.InvalidConfigurationException;
import de.dedede.model.persistence.exceptions.LostConnectionException;

/**
 * Initializes the application's utilities and environment.
 * This is required when the application is launched. 
 *
 * @author Jonas Picker
 * 	
 */
public class DataLayerInitializer {
	
	private static final String[] tableNames = {"Users", "Application", "Medium"
			, "CustomAttribute", "MediumCopy", "AttributeType", "Category"}; 
	
	/**
	 * Performs tasks associated with the initialization of the data layer. 
	 * This includes setting up the data store, the connection pool, and
	 * the maintenance process.
	 * 
	 * @throws LostConnectionException Is thrown if a connection could
	 * 		not be established to the data store to perform the initialization.
	 * @throws DriverNotFoundException if the JDBC driver was the problem source
	 * @throws InvalidConfigurationException If a configuration in the application's 
	 * 		configuration file is invalid
	 * @see ConnectionPool
	 * @see MaintenanceProcess
	 */
	public static void execute() throws LostConnectionException, 
			DriverNotFoundException, InvalidConfigurationException {
		try {
			ConnectionPool.setUpConnectionPool();								
		} catch (ClassNotFoundException cnfe) {
			Logger.severe("Database JDBC driver was not found during "
					+ "ConnectionPool initialization.");
			throw new LostConnectionException("Database driver encountered a "
					+ "problem during system start.", cnfe);
		} catch (SQLException sqle) {
			Logger.severe("An error occured during database communication while"
					+ " initializing ConnectionPool.");
			throw new LostConnectionException("A problem occured while "
					+ "trying to connect to the database during "
					+ "system startup.", sqle);
		} catch (InvalidConfigurationException e) {
			Logger.severe("The connection pool encountered a faulty configuration "
					+ "while starting...");
			throw e;
		}
		
		try {
			setUpDatabase();
		} catch (SQLException sqle) {
			Logger.severe("SQLException occured during"
					+ " database initialization");
			throw new LostConnectionException("A problem occured while "
					+ "communicating with the database during "
					+ "system startup.", sqle);
		} catch (DriverNotFoundException dnfe) {
			throw dnfe;
		}
		setUpMaintenanceProcess();		
	}
	
	/**
	 * Relays tasks associated with graceful shutdown of data layer processes.
	 * 
	 *  @see ConnectionPool
	 *  @see MaintenanceProcess
	 */
	public static void shutdownDataLayer() {
		MaintenanceProcess.shutdown();
		ConnectionPool.destroyConnectionPool();
	}
	
	/**
	 * Capsules a thread that closes open DB initialization connection 
	 * if JVM is terminated
	 */
	private static class CloseConnThread extends Thread {
		
		private Connection connection;
		
		public CloseConnThread(Connection connection) {
			this.connection=connection;
		}
		
		@Override
		public void run() {
		try {
			connection.close();
		} catch (SQLException ignored) {
			System.out.println("Shutdownhook fail");
			}
		}
		
	}
	
	private static void setUpDatabase() throws SQLException {
		ConnectionPool cpInstance = ConnectionPool.getInstance();
		Connection connection = cpInstance.fetchConnection(2000);
		if (connection != null) {
			Logger.development("Successful initial connection with database "
					+ "established.");
			System.out.println("Database connection was established "
					+ "successfully.");
			CloseConnThread thread = new CloseConnThread(connection);
			Runtime.getRuntime().addShutdownHook(thread);
			DatabaseMetaData metadata = connection.getMetaData();
			ResultSet resultSet = metadata.getTables(null, null, null,
					new String[] {"TABLE"});
			try {
				int found = 0;
				while(resultSet.next()) {
					for (String str : tableNames) {
						String tableName = resultSet.getString("TABLE_NAME");
						if (tableName.equalsIgnoreCase(str)) {
							found++;
							break;
						};
					}
				}
				if (found != tableNames.length) {
					Logger.severe(found + " of the 7 required tables are "
							+ "present in the database.");
					System.out.println( found + " of the 7 required tables "
							+ "are present in the database. You can do a fresh "
							+ "restart now:");
					try {
						consoleDialogue(connection);
					} catch (IOException io) {
						Logger.severe("Unable to read console Input");
						System.out.println("Unable to read console Input");
					}
				} else {
					Logger.detailed("All required tables seem to be present.");
					System.out.println("All required tables are present. "
							+ "You can still reinitialize a fresh database "
							+ "scheme if you wish:");
					try {
						consoleDialogue(connection);
					} catch (IOException io) {
						Logger.severe("Unable to read console Input");
						System.out.println("Unable to read console Input");
					}
				}
			} catch (SQLException e) {
				Logger.severe("An Error occured while verifying the "
						+ "database scheme.");
				System.out.println("An Error occured while verifying "
						+ "the database scheme.");
			}
			try {
				connection.close(); 
				Logger.detailed("DB connection was closed after"
						+ " initialization process.");
			} catch (SQLException sqle) {
				Logger.detailed("DB connection failed to close after"
						+ " initialization process.");
				throw sqle;
			}
		} 
		
	}
		
	private static void setUpMaintenanceProcess() {
		MaintenanceProcess mp = MaintenanceProcess.getInstance();
		mp.setup();
		mp.startup();
	}
	
	private static void consoleDialogue(Connection connection) throws 
				IOException, SQLException {
		System.out.println("You can start a fresh database initialization now, "
				+ "this will overwrite existing tables with the names: ");
		System.out.println("\"Users\", \"Application\","
						+ " \"Medium\", \"Category\", "
				+ "\"CustomAttribute\", \"AttributeType\", \"MediumCopy\" ");
		System.out.println("Do you want to (re)create the "
						+ "tables, type 'y', (a standart set of attributes will"
						+ " be added), else type 'n'."); 
		System.out.println("If you wanna add sample"
						+ " entries to the database, type in 'y y'. In any"
						+ " case confirm with enter");
		InputStreamReader inputStreamReader = new InputStreamReader(System.in);
		BufferedReader stdn = new BufferedReader(inputStreamReader);
		boolean end = false;
		while (!end) {
			System.out.print("BiBi> ");
		    String input = stdn.readLine();
					    
			if (input == null) {
		        break;
		    }
			Scanner scanner = new Scanner(input);
			scanner.useDelimiter("\\s+");
			
			if (scanner.hasNext()) {
		    	String command = scanner.next();
		        char[] order = command.toCharArray();
		        //only the first letter of command is matched
		        char fc = Character.toLowerCase(order[0]);
		        
		        switch (fc) {
		        case 'y':
		        	try {
		        	Logger.detailed("'Y' was selected, attempting fresh"
		        			+ " database initialization.");
		        	System.out.println("'Y' was selected, attempting fresh"
		        			+ " database initialization.");
		        	Logger.development("Autocommit was set to true on "
		        			+ "initial DB connection");
		        	dropOldDataScheme(connection);
		        	initializeEnums(connection);
		        	initializeIndependentTables(connection);
		        	initializeMedium(connection);
		        	initializeDependendTables(connection);
		        	initializeDependendAndFunction(connection);
		        	initializeTrigger(connection);
		        	Logger.detailed("New DB scheme initialized");
		        	initializeStandartAttributes(connection);
		        	insertDefaultAdminAndAppData(connection);
		        	Logger.development("DB freshly initialized and filled "
		        			+ "with standart attributes.");
		        	System.out.println("The database was initialized "
		        			+ "successfully.");
		        	if (scanner.hasNext()) {
		        		if (scanner.next().equalsIgnoreCase("y")) {
		        			sampleEntries(connection);														
				        	Logger.development("The database was populated with"
				        			+ " sample entries!");  													
				        	System.out.println("The database was populated with"
				        			+ " sample entries!");
		        		}
		        	}
		        	} catch (SQLException sql) {
		        		Logger.severe("SQL Error while dropping/creating tables"
		        				+ " on DB initialization.");
		        		System.out.println("SQL Error while dropping/creating"
		        				+ " tables on DB initialization.");
		        		scanner.close();
		        		throw sql;
		        	}
		        	end = true;
		        	break;
		        case 'n': 
		        	Logger.detailed("'N' was selected, proceeding system start"
		        			+ " with existing tables.");
		        	System.out.println("'N' was selected, proceeding system"
		        			+ " start with existing tables.");
		        	end = true;
		        	break;
		        default:
		        	System.out.println("First letter of command is parsed,"
		        			+ " expects 'y' or 'n' for Yes or No");
		        } 
		        scanner.close();
		   }
		}
	}

	private static void dropOldDataScheme(Connection connection) throws
																SQLException {
		PreparedStatement dropTables = connection.prepareStatement(
				"DROP TYPE IF EXISTS attributeModifiability, "
				+ "attributeMultiplicity, attributeDataType, "
		    			+ "copyStatus, registeredUserLendStatus, "
		    			+ "systemAnonAccess, systemRegistrationStatus, "
		    			+ "userRole, userVerificationStatus, userLendStatus,"
		    			+ " mediumPreviewPosition CASCADE;"
				);
		dropTables.execute();
		connection.commit();
		dropTables.close();
		Logger.development("droptables executed");
		PreparedStatement dropTypes = connection.prepareStatement(
				"DROP TABLE IF EXISTS Users, Application, Medium, "
		    	+ "Category, CustomAttribute, AttributeType, MediumCopy "
		    	+ "CASCADE;"
				);
		dropTypes.execute();
		connection.commit();
		dropTypes.close();
		Logger.development("droptypes executed");
		Logger.detailed("Previous tables and enums dropped.");
		PreparedStatement dropTrigger = connection.prepareStatement(
				"DROP TRIGGER IF EXISTS on_status_update ON MediumCopy "
				+ "CASCADE;");
		dropTrigger.execute();
		connection.commit();
		dropTrigger.close();
		Logger.development("trigger dropped");
		PreparedStatement dropFunction = connection.prepareStatement(
				"DROP FUNCTION IF EXISTS check_status_validity() CASCADE;");
		dropFunction.execute();
		connection.commit();
		dropFunction.close();
		Logger.development("function dropped");
		Logger.detailed("Previous function and trigger dropped.");
	}
	
	private static void initializeEnums(Connection connection) throws 
															SQLException {
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
		String s4 = "CREATE TYPE SystemRegistrationStatus AS ENUM ("
				+ "	'OPEN',"
				+ "	'CLOSED'"
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
		String s11 = "CREATE TYPE systemAnonAccess AS ENUM ("
				+ "	'REGISTRATION',"
				+ "	'OPAC'"
				+ ");";
		PreparedStatement createCustomTypes1 = connection.prepareStatement(s1);
		createCustomTypes1.execute();
		connection.commit();
		createCustomTypes1.close();
		PreparedStatement createCustomTypes2 = connection.prepareStatement(s2);
		createCustomTypes2.execute();
		connection.commit();
		createCustomTypes2.close();
		PreparedStatement createCustomTypes3 = connection.prepareStatement(s3);
		createCustomTypes3.execute();
		connection.commit();
		createCustomTypes3.close();
		PreparedStatement createCustomTypes4 = connection.prepareStatement(s4);
		createCustomTypes4.execute();
		connection.commit();
		createCustomTypes4.close();
		PreparedStatement createCustomTypes5 = connection.prepareStatement(s5);
		createCustomTypes5.execute();
		connection.commit();
		createCustomTypes5.close();
		PreparedStatement createCustomTypes6 = connection.prepareStatement(s6);
		createCustomTypes6.execute();
		connection.commit();
		createCustomTypes6.close();
		PreparedStatement createCustomTypes7 = connection.prepareStatement(s7);
		createCustomTypes7.execute();
		connection.commit();
		createCustomTypes7.close();
		PreparedStatement createCustomTypes8 = connection.prepareStatement(s8);
		createCustomTypes8.execute();
		connection.commit();
		createCustomTypes8.close();
		PreparedStatement createCustomTypes9 = connection.prepareStatement(s9);
		createCustomTypes9.execute();
		connection.commit();
		createCustomTypes9.close();
		PreparedStatement createCustomType10 = connection.prepareStatement(s10);
		createCustomType10.execute();
		connection.commit();
		createCustomType10.close();
		PreparedStatement createCustomType11 = connection.prepareStatement(s11);
		createCustomType11.execute();
		connection.commit();
		createCustomType11.close();
		Logger.development("Added " + 11 + " custom types (enums) to DB on"
				+ " initialization.");
	}
	
	private static void initializeIndependentTables(Connection connection) 
			throws SQLException {
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
				+ "	tokenCreation TIMESTAMP,"
				+ "	userLendPeriod INTERVAL,"
				+ "	lendStatus USERLENDSTATUS NOT NULL DEFAULT 'DISABLED',"
				+ "	verificationStatus USERVERIFICATIONSTATUS NOT NULL DEFAULT"
				+ " 'UNVERIFIED',"
				+ "	userRole USERROLE NOT NULL DEFAULT 'REGISTERED',"
				+ "	PRIMARY KEY(userID),"
				+ "	CONSTRAINT positive_tokenCreation CHECK (tokenCreation >= "
				+ "CURRENT_TIMESTAMP)"
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
				+ "	registrationStatus SYSTEMREGISTRATIONSTATUS NOT NULL "
				+ "DEFAULT 'OPEN',"
				+ "	lookAndFeel VARCHAR(40) NOT NULL,"
				+ "	anonRights SYSTEMANONACCESS NOT NULL DEFAULT 'OPAC',"
				+ "	userLendStatus REGISTEREDUSERLENDSTATUS NOT NULL DEFAULT"
				+ " 'UNLOCKED'"
				+ ");";
		String s14 = "CREATE TABLE Category ("
				+ "	categoryID SERIAL,"
				+ "	title VARCHAR(100) NOT NULL,"
				+ "	description TEXT,"
				+ "	parentCategoryID INTEGER,"
				+ "	PRIMARY KEY(categoryID),"
				+ "	CONSTRAINT fk_Category FOREIGN KEY(parentCategoryID) "
				+ "REFERENCES Category(categoryID) ON DELETE CASCADE"
				+ ");";
		PreparedStatement createIndependent3 = connection.prepareStatement(s14);
		createIndependent3.executeUpdate(); 
		connection.commit();
		createIndependent3.close();
		PreparedStatement createIndependent1 = connection.prepareStatement(s11);
		createIndependent1.executeUpdate();
		connection.commit();
		createIndependent1.close();
		PreparedStatement createIndependent2 = connection.prepareStatement(s12);
		createIndependent2.executeUpdate();
		connection.commit();
		createIndependent2.close();
		Logger.development("Executed second group (independent tables "
				+ "user/category/application) of create statements on DB "
				+ "initialization.");
	}
	
	private static void initializeMedium(Connection connection) 
			throws SQLException {
		String s13 = "CREATE TABLE Medium ("
				+ "	mediumID SERIAL,"
				+ "	mediumLendPeriod INTERVAL,"
				+ "	hasCategory INTEGER,"
				+ "	PRIMARY KEY(mediumID),"
				+ "	CONSTRAINT fk_Category FOREIGN KEY(hasCategory) REFERENCES"
				+ " Category(categoryID) ON DELETE SET NULL"
				+ ");";
		PreparedStatement createMedium = connection.prepareStatement(s13);
		createMedium.execute();
		connection.commit();
		createMedium.close();
		Logger.development("Executed third create statement (medium) on DB "
				+ "initialization.");
	}
	
	private static void initializeDependendTables(Connection connection) 
			throws SQLException {
		String s15 = "CREATE TABLE CustomAttribute ("
				+ "	attributeID SERIAL,"
				+ "	mediumID INTEGER NOT NULL,"
				+ "	attributeName VARCHAR(40) NOT NULL,"
				+ "	attributeValue BYTEA,"
				+ "	PRIMARY KEY(mediumID, attributeID),"
				+ "	CONSTRAINT fk_Medium FOREIGN KEY(mediumID) REFERENCES "
				+ "Medium(mediumID) ON DELETE CASCADE"
				+ ");";
		String s17 = "CREATE TABLE MediumCopy ("
				+ "	copyID SERIAL,"
				+ "	mediumID INTEGER NOT NULL,"
				+ "	signature VARCHAR(100) UNIQUE,"
				+ "	bibPosition VARCHAR(100),"
				+ "	status COPYSTATUS NOT NULL DEFAULT 'AVAILABLE',"
				+ "	deadline TIMESTAMP,"
				+ "	actor INTEGER,"
				+ "	PRIMARY KEY(mediumID, copyID),"
				+ "	CONSTRAINT fk_Medium FOREIGN KEY(mediumID) REFERENCES "
				+ "Medium(mediumID) ON DELETE CASCADE,"
				+ "	CONSTRAINT fk_User FOREIGN KEY(actor) REFERENCES "
				+ "Users(userID) ON DELETE RESTRICT,"
				+ "	CONSTRAINT positive_deadline CHECK ((deadline IS NULL) OR"
				+ " (deadline >= CURRENT_TIMESTAMP)),"
				+ "	CONSTRAINT no_actor_available CHECK ((status != "
				+ "'AVAILABLE'::COPYSTATUS) OR (actor IS null))"
				+ ");";
		PreparedStatement createDependent1 = connection.prepareStatement(s15);
		createDependent1.execute();
		connection.commit();
		createDependent1.close();
		PreparedStatement createDependent2 = connection.prepareStatement(s17);
		createDependent2.execute();
		connection.commit();
		createDependent2.close();
		Logger.development("Executed fourth group (further dependent tables "
				+ "CostumAttribute/mediumcopy) of statements on "
				+ "DB initialization.");
	}
	
	private static void initializeDependendAndFunction(Connection connection) 
			throws SQLException {
		String s16 = "CREATE TABLE AttributeType ("
				+ "	typeID SERIAL,"
				+ "	attributeID INTEGER NOT NULL,"
				+ " mediumID INTEGER NOT NULL,"
				+ "	previewPosition MEDIUMPREVIEWPOSITION NOT NULL "
				+ "DEFAULT 'HIDDEN',"
				+ "	multiplicity ATTRIBUTEMULTIPLICITY NOT NULL DEFAULT "
				+ "'SINGLE_VALUED',"
				+ "	modifiability ATTRIBUTEMODIFIABILITY NOT NULL DEFAULT "
				+ "'MODIFIABLE',"
				+ "	attributeDataType ATTRIBUTEDATATYPE NOT NULL DEFAULT "
				+ "'TEXT',"
				+ "	PRIMARY KEY(mediumID, attributeID, typeID),"
				+ "	CONSTRAINT fk_CustomAttribute FOREIGN KEY(attributeID,"
				+ " mediumID) REFERENCES CustomAttribute(attributeID, mediumID)"
				+ " ON DELETE CASCADE"
				+ ");";
		String s18 = "CREATE FUNCTION check_status_validity() RETURNS "
				+ "TRIGGER AS "
				+ "$BODY$"
				+ "	BEGIN			"
				+ "		IF (OLD.status = 'LENT'::COPYSTATUS) AND "
				+ "(NEW.status = 'MARKED'::COPYSTATUS) THEN "
				+ "		RAISE EXCEPTION 'Cannot mark a lent copy' ;"
				+ "		ELSEIF (OLD.status = 'LENT'::COPYSTATUS) AND "
				+ "(NEW.status = 'LENT'::COPYSTATUS) THEN "
				+ "		RAISE EXCEPTION 'Copy is already lent' ;"
				+ "		ELSEIF (OLD.status = 'MARKED'::COPYSTATUS) AND "
				+ "(NEW.status = 'MARKED'::COPYSTATUS) THEN "
				+ "		RAISE EXCEPTION 'Copy is already marked' ;"
				+ "		ELSEIF (NEW.status = 'AVAILABLE'::COPYSTATUS) AND "
				+ "(NEW.actor IS NOT NULL) THEN "
				+ "		RAISE EXCEPTION 'An available copys actor must be"
				+ " NULL' ;"
				+ "		ELSEIF NOT ((OLD.status = 'MARKED'::COPYSTATUS AND "
				+ "NEW.status = 'LENT'::COPYSTATUS) AND "
				+ "(OLD.actor = NEW.actor)) THEN "
				+ "		RAISE EXCEPTION 'A marked copy can only be lent by the "
				+ "marking user!' ;							"
				+ "		ELSEIF NOT (	   ((OLD.status = "
				+ "'AVAILABLE'::COPYSTATUS) AND (NEW.status = "
				+ "'LENT'::COPYSTATUS)) "
				+ "						OR ((OLD.status = "
				+ "'MARKED'::COPYSTATUS) AND "
				+ "(NEW.status = 'AVAILABLE'::COPYSTATUS)) "
				+ "						OR "
				+ "((OLD.status = 'AVAILABLE'::COPYSTATUS) AND (NEW.status ="
				+ " 'AVAILABLE'::COPYSTATUS)) "
				+ "					) THEN "
				+ "		RAISE EXCEPTION 'Invalid operation' ;"
				+ "		RETURN new ;"
				+ "	END if ;"
				+ "	end"
				+ "$BODY$"
				+ "LANGUAGE plpgsql;";
		PreparedStatement createAttType = connection.prepareStatement(s16);
		createAttType.execute();
		connection.commit();
		createAttType.close();
		PreparedStatement createFunction = connection.prepareStatement(s18);
		createFunction.execute();
		connection.commit();
		createFunction.close();
		Logger.development("Executed fifth group (function + attrTypes) of "
				+ "statements on DB initialization.");
	}
	
	private static void initializeTrigger(Connection connection) 
			throws SQLException {
		String s19 = "CREATE TRIGGER on_status_update"
				+ "	BEFORE UPDATE OF status ON MediumCopy"
				+ "	FOR EACH ROW"
				+ "	EXECUTE PROCEDURE check_status_validity();";
		PreparedStatement createTrigger = connection.prepareStatement(s19);
		createTrigger.execute();
		connection.commit();
		createTrigger.close();
		Logger.development("Executed last batch (trigger) of statements "
				+ "on DB initialization.");
	}
		
	private static void initializeStandartAttributes(Connection conn) 
			throws SQLException {
		String insertAnchor = "INSERT INTO Medium(mediumID, mediumLend"
				+ "Period, hasCategory) VALUES (DEFAULT, NULL, NULL);";
		String insertAttributes = "insert into customAttribute(attributeID,"
				+ " mediumID, attributeName, attributeValue) "
				+ "values "
				+ "(default, 1, 'Titel', decode('Titel des Mediums', "
				+ "'escape')),"
				+ "(default, 1, 'Autor', decode('erster Autor des Mediums',"
				+ " 'escape')),"
				+ "(default, 1, 'Autor', decode('zweiter Autor des Mediums',"
				+ " 'escape')),"
				+ "(default, 1, 'Typ', decode('Klassifikation des Mediums',"
				+ " 'escape')),"
				+ "(default, 1, 'Auflage', decode('Auflage des Mediums',"
				+ " 'escape')),"
				+ "(default, 1, 'Verlag', decode('Verleger des Mediums',"
				+ " 'escape')),"
				+ "(default, 1, 'Erscheinungsjahr', decode('Erscheinungsjahr"
				+ " des Mediums', 'escape')),"
				+ "(default, 1, 'ISBN-Nummer', decode('ISBN Kennung des "
				+ "Mediums', 'escape')),"
				+ "(default, 1, 'Elektronische Version', decode('Link auf "
				+ "elektronische Version des Mediums', 'escape')),"
				+ "(default, 1, 'Freitext', decode('Freitextasuzug aus dem "
				+ "Medium', 'escape')),"
				+ "(default, 1, 'Icon', NULL);";
		String insertAttTypes = "insert into attributeType(typeID,"
				+ " attributeID, mediumID, previewPosition, multiplicity, "
				+ "modifiability, attributedatatype) "
				+ "values"
				+ "(default, 1, 1, 'SECOND', 'SINGLE_VALUED', 'MODIFIABLE',"
				+ " 'TEXT'),"
				+ "(default, 2, 1, 'THIRD', 'MULTI_VALUED', 'MODIFIABLE',"
				+ " 'TEXT'),"
				+ "(default, 3, 1, 'THIRD', 'MULTI_VALUED', 'MODIFIABLE', "
				+ "'TEXT'),"
				+ "(default, 4, 1, 'HIDDEN', 'SINGLE_VALUED', 'MODIFIABLE',"
				+ " 'TEXT'),"
				+ "(default, 5, 1, 'HIDDEN', 'SINGLE_VALUED', 'MODIFIABLE',"
				+ " 'TEXT'),"
				+ "(default, 6, 1, 'HIDDEN', 'SINGLE_VALUED', 'MODIFIABLE',"
				+ " 'TEXT'),"
				+ "(default, 7, 1, 'HIDDEN', 'SINGLE_VALUED', 'MODIFIABLE',"
				+ " 'TEXT'),"
				+ "(default, 8, 1, 'HIDDEN', 'SINGLE_VALUED', 'MODIFIABLE', "
				+ "'TEXT'),"
				+ "(default, 9, 1, 'HIDDEN', 'SINGLE_VALUED', 'MODIFIABLE',"
				+ " 'LINK'),"
				+ "(default, 10, 1, 'HIDDEN', 'SINGLE_VALUED', 'MODIFIABLE',"
				+ " 'TEXT'),"
				+ "(default, 11, 1, 'FIRST', 'SINGLE_VALUED', 'STATIC',"
				+ " 'IMAGE');";
		PreparedStatement anchorMedium = conn.prepareStatement(insertAnchor);
		int affected1 = anchorMedium.executeUpdate();
		conn.commit();
		anchorMedium.close();
		Logger.development(affected1 + " entry inserted into Medium,"
				+ " is anchor for scheme.");
		PreparedStatement attributes = conn.prepareStatement(insertAttributes);
		int affected2 = attributes.executeUpdate();
		conn.commit();
		attributes.close();
		Logger.development(affected2 + "Default attributes inserted.");
		PreparedStatement attTypes = conn.prepareStatement(insertAttTypes);
		int affected3 = attTypes.executeUpdate();
		conn.commit();
		attTypes.close();
		Logger.development(affected3 + " default attribute types inserted.");
	}
	
	private static void insertDefaultAdminAndAppData(Connection conn) 
			throws SQLException {
		String insertDefaultAdmin = "insert into users(userID, emailAddress, "
				+ "passwordHashSalt, passwordHash, name, surname, postalCode, "
				+ "city, street, houseNumber, token, tokenCreation, "
				+ "userLendPeriod, lendStatus, verificationStatus, userRole) "
				+ "values (default, 'jonaspicker@gmail.com', 'abcdefghijk', "
				+ "'0c3c77e1b8e04157995c5a81fddcd1a30c09955ecb708043af13af"
				+ "742720eccc', 'defaultAdmin', 'defaultAdmin', '12345', 'tes"
				+ "tcityADM', 'teststreetADM', 'testhouseADM', 'adminto"
				+ "ken1123456789', CURRENT_TIMESTAMP, '11:11:11', 'ENABLED',"
				+ " 'VERIFIED', 'ADMIN');";
		String insSaAppData = "insert into application(one, "
				+ "bibname, emailregex, contactinfo, imprintinfo,"
				+ " privacypolicy, biblogo, globallendlimit, "
				+ "globalmarkinglimit,"
				+ " reminderoffset, registrationstatus, "
				+ "lookandfeel, anonrights, userlendstatus)"
				+ "values (default, 'testBibName', '.*', 'Exemplary contact "
				+ "info', 'Example imprint', 'Example privacy Policy',"
				+ " null, '99:59:59', '24:00:00', '1:00:00', default, "
				+ "'SelectedLookAndFeel', default, default);";
		PreparedStatement sampleAppData = conn.prepareStatement(insSaAppData);
		sampleAppData.execute();
		conn.commit();
		sampleAppData.close();
		Logger.development("Exemplary application data was added");
		PreparedStatement defAdm = conn.prepareStatement(insertDefaultAdmin);
		defAdm.execute();
		conn.commit();
		defAdm.close();
		Logger.development("Default admin was added");
	}
	
	private static void sampleEntries(Connection conn) throws SQLException {
		
		String insertSampleCategory1 = "insert into category(categoryID, "
				+ "title, description, parentCategoryID) "
				+ "values (default, 'SampleParentCategory', "
				+ "'This category is part of sample data, it should have "
				+ "1 child category and no parent', NULL);";
		String insertSampleCategory2 = "insert into category(categoryID, "
				+ "title, description, parentCategoryID) "
				+ "values (default, 'SampleChildCategory', "
				+ "'This category is part of sample data, it should have "
				+ "SampleParentCategory as parent and no children', 1);";
		String insertSampleMediums = "insert into medium(mediumID,"
				+ " mediumLendPeriod, hasCategory) "
				+ "VALUES (DEFAULT, '3 4:05:06' , 1), (DEFAULT, '1-2', 2);";
		String insertSampleAttributes1 = "insert into "
				+ "customAttribute(attributeID, mediumID, attributeName, "
				+ "attributeValue)"
				+ "values "
				+ "(default, 2, 'Titel', decode('Titel des ersten"
				+ " Beispielmediums', 'escape')),"
				+ "(default, 2, 'Autor', decode('erster Autor des ersten "
				+ "Beispielmediums', 'escape')),"
				+ "(default, 2, 'Autor', decode('zweiter Autor des ersten "
				+ "Beispielmediums', 'escape')),"
				+ "(default, 2, 'Typ', decode('Klassifikation des ersten "
				+ "Beispielmediums', 'escape')),"
				+ "(default, 2, 'Auflage', decode('Auflage des ersten "
				+ "Beispielmediums', 'escape')),"
				+ "(default, 2, 'Verlag', decode('Verleger des ersten"
				+ " Beispielmediums', 'escape')),"
				+ "(default, 2, 'Erscheinungsjahr', decode('1969', 'escape')),"
				+ "(default, 2, 'ISBN-Nummer', decode('1239128437746',"
				+ " 'escape')),"
				+ "(default, 2, 'Elektronische Version', decode('Link"
				+ " auf elektronische Version des ersten Beispielmediums', "
				+ "'escape')),"
				+ "(default, 2, 'Freitext', decode('Freitextasuzug aus dem"
				+ " ersten Beispielmediums', 'escape')),"
				+ "(default, 2, 'Icon', NULL);";
		String insertSampleAttributes2 = "insert into customAttribute"
				+ "(attributeID, mediumID, attributeName, attributeValue) "
				+ "values "
				+ "(default, 3, 'Titel', decode('Titel des 2ten "
				+ "Beispielmediums', 'escape')),"
				+ "(default, 3, 'Autor', decode('erster Autor des 2ten "
				+ "Beispielmediums', 'escape')),"
				+ "(default, 3, 'Autor', decode('zweiter Autor des 2ten "
				+ "Beispielmediums', 'escape')),"
				+ "(default, 3, 'Typ', decode('Klassifikation des 2ten "
				+ "Beispielmediums', 'escape')),"
				+ "(default, 3, 'Auflage', decode('Auflage des 2ten "
				+ "Beispielmediums', 'escape')),"
				+ "(default, 3, 'Verlag', decode('Verleger des 2ten "
				+ "Beispielmediums', 'escape')),"
				+ "(default, 3, 'Erscheinungsjahr', decode('2021', "
				+ "'escape')),"
				+ "(default, 3, 'ISBN-Nummer', decode('0987654321', "
				+ "'escape')),"
				+ "(default, 3, 'Elektronische Version', decode('Link "
				+ "auf elektronische Version des 2ten Beispielmediums',"
				+ " 'escape')),"
				+ "(default, 3, 'Freitext', decode('Freitextasuzug aus"
				+ " dem 2ten Beispielmediums', 'escape')),"
				+ "(default, 3, 'Icon', NULL);";
		String insertSampleAttributeTypes1 = "insert into attributeType"
				+ "(typeID, attributeID, mediumID, "
				+ "previewPosition, multiplicity, modifiability, attrib"
				+ "utedatatype) "
				+ "values"
				+ "(default, 12, 2, 'SECOND', 'SINGLE_VALUED', 'MODIFIABLE'"
				+ ", 'TEXT'),"
				+ "(default, 13, 2, 'THIRD', 'MULTI_VALUED', 'MODIFIABLE', "
				+ "'TEXT'),"
				+ "(default, 14, 2, 'THIRD', 'MULTI_VALUED', 'MODIFIABLE', "
				+ "'TEXT'),"
				+ "(default, 15, 2, 'HIDDEN', 'SINGLE_VALUED', 'MODIFIABLE',"
				+ " 'TEXT'),"
				+ "(default, 16, 2, 'HIDDEN', 'SINGLE_VALUED', 'MODIFIABLE',"
				+ " 'TEXT'),"
				+ "(default, 17, 2, 'HIDDEN', 'SINGLE_VALUED', 'MODIFIABLE',"
				+ " 'TEXT'),"
				+ "(default, 18, 2, 'HIDDEN', 'SINGLE_VALUED', 'MODIFIABLE',"
				+ " 'TEXT'),"
				+ "(default, 19, 2, 'HIDDEN', 'SINGLE_VALUED', 'MODIFIABLE',"
				+ " 'TEXT'),"
				+ "(default, 20, 2, 'HIDDEN', 'SINGLE_VALUED', 'MODIFIABLE',"
				+ " 'LINK'),"
				+ "(default, 21, 2, 'HIDDEN', 'SINGLE_VALUED', 'MODIFIABLE',"
				+ " 'TEXT'),"
				+ "(default, 22, 2, 'FIRST', 'SINGLE_VALUED', 'STATIC',"
				+ " 'IMAGE');";
		String insertSampleAttributeTypes2 = "insert into attributeType(typeID,"
				+ " attributeID, mediumID, "
				+ "previewPosition, multiplicity, modifiability,"
				+ " attributedatatype) "
				+ "values"
				+ "(default, 23, 3, 'SECOND', 'SINGLE_VALUED', 'MODIFIABLE'"
				+ ", 'TEXT'),"
				+ "(default, 24, 3, 'THIRD', 'MULTI_VALUED', 'MODIFIABLE',"
				+ " 'TEXT'),"
				+ "(default, 25, 3, 'THIRD', 'MULTI_VALUED', 'MODIFIABLE',"
				+ " 'TEXT'),"
				+ "(default, 26, 3, 'HIDDEN', 'SINGLE_VALUED', 'MODIFIABLE', "
				+ "'TEXT'),"
				+ "(default, 27, 3, 'HIDDEN', 'SINGLE_VALUED', 'MODIFIABLE',"
				+ " 'TEXT'),"
				+ "(default, 28, 3, 'HIDDEN', 'SINGLE_VALUED', 'MODIFIABLE', "
				+ "'TEXT'),"
				+ "(default, 29, 3, 'HIDDEN', 'SINGLE_VALUED', 'MODIFIABLE', "
				+ "'TEXT'),"
				+ "(default, 30, 3, 'HIDDEN', 'SINGLE_VALUED', 'MODIFIABLE', "
				+ "'TEXT'),"
				+ "(default, 31, 3, 'HIDDEN', 'SINGLE_VALUED', 'MODIFIABLE',"
				+ " 'LINK'),"
				+ "(default, 32, 3, 'HIDDEN', 'SINGLE_VALUED', 'MODIFIABLE',"
				+ " 'TEXT'),"
				+ "(default, 33, 3, 'FIRST', 'SINGLE_VALUED', "
				+ "'STATIC', 'IMAGE');";
		String insertSampleUsers = "insert into users(userID, emailAddress, "
				+ "passwordHashSalt, passwordHash, "
				+ "name, surname, postalCode, city, street, houseNumber, "
				+ "token, tokenCreation, userLendPeriod, "
				+ "lendStatus, verificationStatus, userRole) values (default,"
				+ " 'choochoo-thomas@tutanota.com', "
				+ "'abcdefghijk', '373a605659e31a32b3b0020f887c580c3d97b7"
				+ "fb57264505e84be0c79903af4a', 'user1', "
				+ "'name1', '12345', 'testcity1', 'teststreet1', 'testhouse1'"
				+ ", 'testtoken11234567890', CURRENT_TIMESTAMP,"
				+ "'11:11:11', 'ENABLED', 'VERIFIED', 'ADMIN'), (default, "
				+ "'sep21g01@fim.uni-passau.de', 'lmnopqrst', "
				+ "'0a0311d42897cf9a63d510c453fecf03239967f4edac6d29f39ae6"
				+ "c787a54dc1', 'user2', 'name2', '67890', "
				+ "'testcity2', 'teststreet2', 'testhouse2','testtoken2123"
				+ "4567890', CURRENT_TIMESTAMP, '22:22:22', "
				+ "'ENABLED', 'VERIFIED', 'STAFF'), (default, 'jonas11@ads"
				+ ".uni-passau.de', 'uvwxyzäöü', "
				+ "'182228fe0e08c479f765683e2ac83f19c31a85270b19d1b60b221a"
				+ "35a1c3d420', 'user3', 'name3', "
				+ "'90123', 'testcity3', 'teststreet3', 'testhouse3','tes"
				+ "ttoken31234567890', CURRENT_TIMESTAMP, "
				+ "'33:33:33', 'ENABLED', 'VERIFIED', 'REGISTERED');";
		String insertSampleCopies = "insert into mediumcopy (copyID, mediumid,"
				+ " signature, bibposition, status, deadline, actor) \r\n"
				+ "values (default, 2, 'sampleSignature1', 'samplePosition1', "
				+ "'AVAILABLE', null, NULL),\r\n"
				+ "(default, 2, 'sampleSignature2', 'samplePosition2', "
				+ "'AVAILABLE', null, NULL),\r\n"
				+ "(default, 3, 'sampleSignature3', 'samplePosition3', "
				+ "'AVAILABLE', null, NULL),\r\n"
				+ "(default, 3, 'sampleSignature4', 'samplePosition4', "
				+ "'AVAILABLE', null, NULL);";
		PreparedStatement sampleCategories1 = 
				conn.prepareStatement(insertSampleCategory1);
		int c1 = sampleCategories1.executeUpdate();
		conn.commit();
		sampleCategories1.close();
		Logger.development(c1 + " sample category added");
		PreparedStatement sampleCategories2 = 
				conn.prepareStatement(insertSampleCategory2);
		int c2 = sampleCategories2.executeUpdate();
		conn.commit();
		sampleCategories2.close();
		Logger.development("Another (" + c2 + ") sample category added");
		PreparedStatement sampleMedium =
				conn.prepareStatement(insertSampleMediums);
		int c3 = sampleMedium.executeUpdate();
		conn.commit();
		sampleMedium.close();
		Logger.development(c3 + " medium samples added");
		PreparedStatement sampleAttributes1 =
				conn.prepareStatement(insertSampleAttributes1);
		int c4 = sampleAttributes1.executeUpdate();
		conn.commit();
		sampleAttributes1.close();
		PreparedStatement sampleAttributes2 =
				conn.prepareStatement(insertSampleAttributes2);
		int c5 = c4 + sampleAttributes2.executeUpdate();
		conn.commit();
		sampleAttributes2.close();
		Logger.development(c5+ " sample attributes added");
		PreparedStatement sampleAttributeTypes =
				conn.prepareStatement(insertSampleAttributeTypes1);
		int c6 = sampleAttributeTypes.executeUpdate();
		conn.commit();
		sampleAttributeTypes.close();
		PreparedStatement sampleAttTypes2 =
				conn.prepareStatement(insertSampleAttributeTypes2);
		int c7 = c6 + sampleAttTypes2.executeUpdate();
		conn.commit();
		sampleAttTypes2.close();
		Logger.development(c7 + " sample attribute types added");
		PreparedStatement sampleUsers = 
				conn.prepareStatement(insertSampleUsers);
		int c8 = sampleUsers.executeUpdate();
		conn.commit();
		sampleUsers.close();
		Logger.development(c8 + " sample user added");
		PreparedStatement samCop = conn.prepareStatement(insertSampleCopies);
		int c9 = samCop.executeUpdate();
		conn.commit();
		samCop.close();
		Logger.development(c9 + " sample copies added");		
	}
	
}
