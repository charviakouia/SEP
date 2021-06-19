package tests;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import de.dedede.model.data.dtos.CopyDto;
import de.dedede.model.data.dtos.MediumCopyUserDto;
import de.dedede.model.data.dtos.MediumDto;
import de.dedede.model.data.dtos.UserDto;
import de.dedede.model.persistence.daos.MediumDao;
import de.dedede.model.persistence.daos.UserDao;
import de.dedede.model.persistence.exceptions.LostConnectionException;
import de.dedede.model.persistence.exceptions.MaxConnectionsException;
import de.dedede.model.persistence.exceptions.MediumDoesNotExistException;
import de.dedede.model.persistence.exceptions.UserDoesNotExistException;
import de.dedede.model.persistence.util.ConnectionPool;

public class MaintenanceProcessTest {
	
	private static int testMediumId;
	private static int testUserId;
	private static int testCategoryId;
	private static int testCopyId;

	@BeforeAll
	public static void setup() {
		//To_Do ConfigReader + ConnectionPool Zeug 
		Connection conn = ConnectionPool.getInstance().fetchConnection(5000);
		try {
			PreparedStatement getReminderOffset = conn.prepareStatement(		
					"SELECT EXTRACT (EPOCH FROM (SELECT reminderoffset " 
							+ "FROM application WHERE one = 1));");
			ResultSet rs1 = getReminderOffset.executeQuery();
			conn.commit();
			rs1.next();
			double reminderOffset = rs1.getDouble(1);
			rs1.close();
			getReminderOffset.close();
			if (reminderOffset == 0) {
				reminderOffset = 3600;
			}
			testUserId = insertTestUser(conn);
		    testCategoryId = insertTestCategory(conn);
		    testMediumId = insertTestMedium(conn, testCategoryId);
			
			PreparedStatement stmt = conn.prepareStatement(
				"insert into mediumcopy (copyID, mediumid, signature, "
				+ "bibposition, status, deadline, actor)"
				+ "values (default, ?, 'maintenanceTest123', "
				+ "'maintenanceTest123', "
				+ "'BORROWED', ?, ?);"
				);
			stmt.setInt(1, testMediumId);
			Timestamp dueDate = new Timestamp(System.currentTimeMillis() 
					+ ((long) (reminderOffset - 1)*1000));
			stmt.setTimestamp(2, dueDate);
			stmt.setInt(3, testUserId);
			stmt.executeUpdate();
			conn.commit();
			stmt.close();
			PreparedStatement getCopyId = conn.prepareStatement("SELECT copyId"
					+ " FROM mediumcopy WHERE (signature "
					+ "= 'maintenanceTest123') AND (bibposition "
					+ "= 'maintenanceTest123');");
			ResultSet rs2 = getCopyId.executeQuery();
			conn.commit();
			rs2.next();
			testCopyId = rs2.getInt(1);
			rs2.close();
			getCopyId.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	@AfterAll
	public static void tearDown() throws LostConnectionException, 
		MaxConnectionsException, UserDoesNotExistException {
		try {
	    	   Connection conn = ConnectionPool.getInstance().fetchConnection(5000);    	   
	    	   PreparedStatement stmt = conn.prepareStatement(
	    			   "DELETE FROM mediumcopy WHERE (signature "
	    			   + "= 'maintenanceTest123') AND (bibposition "
	    			   + "= 'maintenanceTest123');"
	    			   );
	    	   stmt.executeUpdate();
	    	   conn.commit();
	    	   stmt.close();
	    	   MaintenanceProcessTest.deleteTestMedium(conn);
	    	   MaintenanceProcessTest.deleteTestCategory(conn);
	       } catch (SQLException e) {
	    	   e.printStackTrace();
	       }
	        UserDto userDto = new UserDto();
	        userDto.setEmailAddress("testemail@thisisanemailadress.com");
	        UserDao.deleteUser(userDto);
	        ConnectionPool.destroyConnectionPool();
	}
	
	@Test
	public static void testReadDueDates() throws LostConnectionException, 
		MaxConnectionsException, MediumDoesNotExistException, 
		UserDoesNotExistException {
		List<MediumCopyUserDto> results = MediumDao.readDueDateReminders();
		CopyDto copy = new CopyDto();
		UserDto user = new UserDto();
		MediumDto medium = new MediumDto();
		for (MediumCopyUserDto element : results) {
			if (element.getCopy().getSignature() == "maintenanceTest123" 
					&& element.getCopy().getLocation() == "maintenanceTest123") {
				copy = element.getCopy();
				user = element.getUser();
				medium = element.getMedium();
			}
		}
		Assertions.assertEquals(testCopyId, copy.getId());
		Assertions.assertEquals(testMediumId, medium.getId());
		Assertions.assertEquals(testUserId, user.getId());
	}

	public static int insertTestMedium(Connection conn, int categoryId) 
			throws SQLException {
		PreparedStatement insertTestMedium = conn.prepareStatement("insert into"
				+ " medium(mediumId, mediumlendperiod, hascategory, title,"
				+ " author1, author2, author3, author4, author5, mediumtype,"
				+ " edition, publisher, releaseyear, isbn, mediumlink,"
				+ " demotext)"
				+ "	values 	(default, '1 2:34:56', ?, 'testTitle1231', "
				+ "'testAuthor1232', 'testAuthor1233', 'testAuthor123', "
				+ "'testAuthor1234', 'testAuthor1235', 'testType123', "
				+ "'testEdition123', 'testPublisher123', 1234, '123-456-789',"
				+ " 'testLink123', 'This is the test demo text of test"
				+ " medium 123');");
		insertTestMedium.setInt(1, categoryId);
		insertTestMedium.executeUpdate();
		conn.commit();
		insertTestMedium.close();
		PreparedStatement getMediumId = conn.prepareStatement("SELECT mediumId"
				+ " FROM medium WHERE (title = 'testTitle1231') AND (demotext ="
				+ " 'This is the test demo text of test medium 123') AND (isbn"
				+ " = '123-456-789');");
		ResultSet rsId = getMediumId.executeQuery();
		conn.commit();
		rsId.next();
		int mediumId = rsId.getInt(1);
		rsId.close();
		getMediumId.close();
		
		return mediumId;
	}

	public static int insertTestCategory(Connection conn) throws SQLException {
		PreparedStatement insertTestCategory =conn.prepareStatement("insert "
				+ "into category(categoryID, title, description, "
				+ "parentCategoryID) values (default, 'testCategory123',"
				+ " 'This category is test data', NULL);");
		insertTestCategory.executeUpdate();
		conn.commit();
		insertTestCategory.close();
		PreparedStatement getCategoryId = conn.prepareStatement("SELECT "
				+ "categoryId FROM category WHERE (title = 'testTitle123') "
				+ "AND (description = 'This category is test data') AND "
				+ "(parentcategoryid is null);");
		ResultSet rsId = getCategoryId.executeQuery();
		conn.commit();
		rsId.next();
		int categoryId = rsId.getInt(1);
		rsId.close();
		getCategoryId.close();
		
		return categoryId;
	}

	public static int insertTestUser(Connection conn) throws SQLException {
		PreparedStatement testUser = conn.prepareStatement(
		    	"insert into users(userID, emailAddress, passwordHashSalt, "
		    	+ "passwordHash, name, surname, postalCode, city, street, "
		    	+ "houseNumber, token, tokenCreation, userLendPeriod, "
		    	+ "lendStatus, verificationStatus, userRole) values "
		    	+ "(default, 'testemail@thisisanemailadress.com', "
		    	+ "'aqweoiaskfjas', '02004cb3f812178459668835c436b5b2d7"
		    	+ "f8aa3481c24a52a5a4e34b6d6527f6', 'testuser123', "
		    	+ "'testuser123', 'testcity123', 'teststreet123', "
		    	+ "'testhouse123', 'testtoken12304567890', "
		    	+ "CURRENT_TIMESTAMP, '11:22:33', 'ENABLED', 'VERIFIED',"
		    	+ " 'REGISTERED');"
		    	);
		testUser.executeUpdate();
		conn.commit();
		testUser.close();
		UserDto emailContainer = new UserDto();
	    emailContainer.setEmailAddress("testemail@thisisanemailadress.com");
	    try {
	    	
			return UserDao.getUserIdByEmail(conn, emailContainer);
		} catch (UserDoesNotExistException e) {
			e.printStackTrace();
			
			return 0;
		}
	}

	public static void deleteTestMedium(Connection conn) throws SQLException {
		PreparedStatement stmt = conn.prepareStatement("DELETE FROM medium "
				+ "WHERE (title = 'testTitle1231') AND (demotext = 'This is the"
				+ " test demo text of test medium 123') AND (isbn = "
				+ "'123-456-789');");
		stmt.executeUpdate();
		conn.commit();
		stmt.close();
	}

	public static void deleteTestCategory(Connection conn) throws SQLException {
		PreparedStatement stmt = conn.prepareStatement("DELETE FROM category "
				+ "WHERE  (title = 'testTitle123') AND (description = 'This "
				+ "category is test data') AND (parentcategoryid is null);");
		stmt.executeUpdate();
		conn.commit();
		stmt.close();
	}
}
