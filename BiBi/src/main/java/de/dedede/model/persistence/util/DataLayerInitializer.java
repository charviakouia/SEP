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
			, "MediumCopy", "Category"}; 
	
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
		Connection connection = cpInstance.fetchConnection(5000);
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
					Logger.severe(found + " of the "
							+ tableNames.length +" required tables are "
							+ "present in the database.");
					System.out.println( found + " of the " + tableNames.length
							+ " required tables "
							+ "are present in the database.");
					try {
						consoleDialogue(connection);
					} catch (IOException io) {
						Logger.severe("Unable to read console Input");
						System.out.println("Unable to read console Input");
					}
				} else {
					Logger.detailed("All required tables seem to be present.");
					System.out.println("All required tables are present. "
							+ "Starting system with existing tables..........");
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
				+ " \"MediumCopy\" ");
		System.out.println("Do you want to (re)create the "
						+ "tables, type 'create'."); 
		System.out.println("If you wanna add sample"
						+ " entries to the database as well, type in 'c s'. "
						+ "In any case confirm with enter");
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
		        case 'c':
		        	try {
		        	Logger.detailed("'Y' was selected, attempting fresh"
		        			+ " database initialization.");
		        	System.out.println("'Y' was selected, attempting fresh"
		        			+ " database initialization.");
		        	dropOldDataScheme(connection);
		        	initializeEnums(connection);
		        	initializeIndependentTables(connection);
		        	initializeMedium(connection);
		        	initializeDependendTables(connection);
		        	Logger.detailed("New DB scheme initialized");
		        	insertDefaultAdminAndAppData(connection);
		        	Logger.development("DB freshly initialized and filled "
		        			+ "with standart attributes.");
		        	System.out.println("The database was initialized "
		        			+ "successfully.");
		        	if (scanner.hasNext()) {
		        		if (scanner.next().equalsIgnoreCase("s")) {
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
		        default:
		        	System.out.println("Only first letter of command is parsed,"
		        			+ " expects 'c' or  for table creation and 'c s' "
		        			+ "if you want samples as well");
		        } 
		        scanner.close();
		   }
		}
	}

	private static void dropOldDataScheme(Connection connection) throws
																SQLException {
		PreparedStatement dropTables = connection.prepareStatement(
				"DROP TABLE IF EXISTS"
						+ " Users, Application, Medium, "
				    	+ "Category, MediumCopy "
				    	+ "CASCADE;"
				);
		dropTables.execute();
		connection.commit();
		dropTables.close();
		Logger.development("droptables executed");
		PreparedStatement dropTypes = connection.prepareStatement(
				"DROP TYPE IF EXISTS "
						+ "copyStatus, registeredUserLendStatus, "
						+ "systemAnonAccess, systemRegistrationStatus, "
						+ "userRole, userVerificationStatus, userLendStatus"
						+ " CASCADE;"
				);
		dropTypes.execute();
		connection.commit();
		dropTypes.close();
		Logger.development("droptypes executed");
		Logger.detailed("Previous tables and enums dropped.");
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
		String s13 = "create table medium ("
				+ "	mediumID SERIAL,"
				+ "	mediumLendPeriod INTERVAL,"
				+ "	hasCategory INTEGER,"
				+ "	title VARCHAR(100) not NULL,"
				+ "	author1 VARCHAR(100),"
				+ "	author2 VARCHAR(100),"
				+ "	author3 VARCHAR(100),"
				+ "	author4 VARCHAR(100),"
				+ "	author5 VARCHAR(100),"
				+ "	mediumtype VARCHAR(100),"
				+ "	edition VARCHAR(100),"
				+ "	publisher VARCHAR(100),"
				+ "	releaseyear INTEGER,"
				+ "	isbn VARCHAR(100),"
				+ "	mediumlink VARCHAR(150),"
				+ "	demotext TEXT,"
				+ "	PRIMARY KEY(mediumID),"
				+ "	CONSTRAINT fk_Category FOREIGN KEY(hasCategory) REFERENCES Category(categoryID) ON DELETE SET null"
				+ "	);";
		PreparedStatement createMedium = connection.prepareStatement(s13);
		createMedium.execute();
		connection.commit();
		createMedium.close();
		Logger.development("Executed third create statement (medium) on DB "
				+ "initialization.");
	}
	
	private static void initializeDependendTables(Connection connection) 
			throws SQLException {
		String s17 = "CREATE TABLE MediumCopy ("
				+ "	copyID SERIAL,"
				+ "	mediumID INTEGER NOT NULL,"
				+ "	signature VARCHAR(100) not null UNIQUE,"
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
		PreparedStatement createDependent2 = connection.prepareStatement(s17);
		createDependent2.execute();
		connection.commit();
		createDependent2.close();
		Logger.development("Executed fourth group (further dependent table "
				+ "mediumcopy) of statements on "
				+ "DB initialization.");
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
		String insertSampleMediums = "	insert into medium(mediumId, "
				+ "mediumlendperiod, hascategory, title, author1, author2, "
				+ "author3, author4, author5, mediumtype, edition, publisher,"
				+ " releaseyear, isbn, mediumlink, demotext) values "
				+ "(default, '3 4:05:06', 1, 'exampleTitle1',"
				+ " 'exampleAuthor1-1', 'exampleAuthor2-1', 'exampleAuthor3-1'"
				+ ", 'exampleAuthor4-1', 'exampleAuthor5-1', 'exampleType1', "
				+ "'exampleEdition1', 'examplePublisher1', 1111, '111-111-111',"
				+ " 'exampleLink1', 'This is the example demo text of example"
				+ " medium 1'), (default, '1-2', 2, 'exampleTitle2',"
				+ " 'exampleAuthor1-2', 'exampleAuthor2-2', 'exampleAuthor3-2'"
				+ ", 'exampleAuthor3-2', null, 'exampleType2',"
				+ " 'exampleEdition2', 'examplePublisher2', 2222,"
				+ " '222-222-222', 'exampleLink2', 'This is the example"
				+ " demo text of example medium 2'), (default, '77:55:44', 1,"
				+ " 'exampleTitle3', null, 'exampleAuthor2-3',"
				+ " 'exampleAuthor3-3', 'exampleAuthor4-3', null,"
				+ " 'exampleType3', 'exampleEdition3', 'examplePublisher3',"
				+ " 3333, '333-333-333', 'exampleLink3', 'This is the example"
				+ " demo text of example medium 3');";
		String insertSampleUsers = "insert into users(userID, emailAddress, "
				+ "passwordHashSalt, passwordHash, "
				+ "name, surname, postalCode, city, street, houseNumber, "
				+ "token, tokenCreation, userLendPeriod, "
				+ "lendStatus, verificationStatus, userRole) values (default,"
				+ " 'choochoo-thomas@tutanota.com', "
				+ "'abcdefghijk', 'c8eb6be7da27a445473bc50d1bbf60d91e06b13321"
				+ "56ffdd252d87270bf351bf', 'user1', "
				+ "'name1', '12345', 'testcity1', 'teststreet1', 'testhouse1'"
				+ ", 'testtoken11234567890', CURRENT_TIMESTAMP,"
				+ "'11:11:11', 'ENABLED', 'VERIFIED', 'ADMIN'), (default, "
				+ "'sep21g01@fim.uni-passau.de', 'lmnopqrst', "
				+ "'b23a27949d63ec1acc7f39912237881cf86b8f3f59d1269b2cdb6df"
				+ "cf879d6bb', 'user2', 'name2', '67890', "
				+ "'testcity2', 'teststreet2', 'testhouse2','testtoken2123"
				+ "4567890', CURRENT_TIMESTAMP, '22:22:22', "
				+ "'ENABLED', 'VERIFIED', 'STAFF'), (default, 'jonas11@ads"
				+ ".uni-passau.de', 'uvwxyzäöü', "
				+ "'37a92ce5a5fb3b3c2a9305eb5180f898837fb3522b9277b26"
				+ "95da0aa59799f3a', 'user3', 'name3', "
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
