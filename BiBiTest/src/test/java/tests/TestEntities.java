package tests;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import de.dedede.model.data.dtos.UserDto;
import de.dedede.model.persistence.daos.UserDao;
import de.dedede.model.persistence.exceptions.UserDoesNotExistException;

/**
 * Implements methods used to insert and delete entities into the database for
 * use in other test classes.
 * 
 * @author Jonas Picker
 */
public class TestEntities {

	/**
	 * Inserts a test medium into the database, and retrieves its id by the 
	 * column values.
	 * 
	 * @param conn the connection to work with.
	 * @param categoryId the id of the mediums category.
	 * @return the id of the inserted entry.
	 * @throws SQLException if a db communication error occured.
	 */
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

	/**
	 * Inserts a test category into the database, and retrieves its id by the 
	 * column values.
	 * 
     * @param conn the connection to work with.
	 * @return the id of the inserted entry.
	 * @throws SQLException if a db communication error occured.
	 */
	public static int insertTestCategory(Connection conn) throws SQLException {
		PreparedStatement insertTestCategory =conn.prepareStatement("insert "
				+ "into category(categoryID, title, description, "
				+ "parentCategoryID) values (default, 'testCategory123',"
				+ " 'This category is test data', NULL);");
		insertTestCategory.executeUpdate();
		conn.commit();
		insertTestCategory.close();
		PreparedStatement getCategoryId = conn.prepareStatement("SELECT "
				+ "categoryId FROM category WHERE (title = 'testCategory123') "
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

	/**
	 * Inserts a test uder into the database and retrieves his id by his email
	 * address.
	 * 
	 * @param conn the connection to work with.
	 * @return the id of the inserted entry.
	 * @throws SQLException if a db communication error occured.
	 * @throws UserDoesNotExistException if userid wasn't found in db.
	 */
	public static int insertTestUser(Connection conn) throws SQLException, 
												UserDoesNotExistException {
		PreparedStatement testUser = conn.prepareStatement(
		    	"insert into users(userID, emailAddress, passwordHashSalt, "
		    	+ "passwordHash, name, surname, postalCode, city, street, "
		    	+ "houseNumber, token, tokenCreation, userLendPeriod, "
		    	+ "lendStatus, verificationStatus, userRole) values "
		    	+ "(default, 'testemail@thisisanemailaddress.com', "
		    	+ "'aqweoiaskfjas', '8b6b6f3d381ae96090a87ebaf9b6b0307f1916642"
		    	+ "2845b93286364bfc06bf828', 'testuser123', "
		    	+ "'testuser123', '123123123', 'testcity123', "
		    	+ "'teststreet123', 'testhouse123', 'testtoken12304567890', "
		    	+ "CURRENT_TIMESTAMP, '11:22:33', 'ENABLED', 'VERIFIED',"
		    	+ " 'REGISTERED');"
		    	);
		testUser.executeUpdate();
		conn.commit();
		testUser.close();
		UserDto emailContainer = new UserDto();
	    emailContainer.setEmailAddress("testemail@thisisanemailaddress.com");
	    
	    return UserDao.getUserIdByEmail(conn, emailContainer);
	}

	/**
	 * Deletes the inserted test medium from the data store.
	 * 
	 * @param conn the connection to work with.
	 * @throws SQLException if a db communication error occured.
	 */
	public static void deleteTestMedium(Connection conn) throws SQLException {
		PreparedStatement stmt = conn.prepareStatement("DELETE FROM medium "
				+ "WHERE (title = 'testTitle1231') AND (demotext = 'This is the"
				+ " test demo text of test medium 123') AND (isbn = "
				+ "'123-456-789');");
		stmt.executeUpdate();
		conn.commit();
		stmt.close();
	}

	/**
	 * Deletes the inserted test category from the data store.
	 * 
	 * @param conn the connection to work with.
	 * @throws SQLException if a db communication error occured.
	 */
	public static void deleteTestCategory(Connection conn) throws SQLException {
		PreparedStatement stmt = conn.prepareStatement("DELETE FROM category "
				+ "WHERE  (title = 'testCategory123') AND (description = 'This "
				+ "category is test data') AND (parentcategoryid is null);");
		stmt.executeUpdate();
		conn.commit();
		stmt.close();
	}

}
