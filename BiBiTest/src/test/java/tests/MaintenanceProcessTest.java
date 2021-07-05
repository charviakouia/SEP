package tests;

import test.java.tests.PreTest;

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
import de.dedede.model.persistence.exceptions.CopyDoesNotExistException;
import de.dedede.model.persistence.exceptions.LostConnectionException;
import de.dedede.model.persistence.exceptions.MaxConnectionsException;
import de.dedede.model.persistence.exceptions.MediumDoesNotExistException;
import de.dedede.model.persistence.exceptions.UserDoesNotExistException;
import de.dedede.model.persistence.util.ConnectionPool;

/**
 * Tests MediumDao functionality used in execution of automated tasks.
 * 
 * @author Jonas Picker
 */
public class MaintenanceProcessTest extends TestEntities{
	
	private static int testMediumId;
	private static int testUserId;
	private static int testCategoryId;
	private static int testCopyId;

	/**
	 * Inserts a test user and copy, but needs to insert test category and 
	 * medium as well to satisfy foreign key dependencies.
	 * 
	 * @throws ClassNotFoundException @see PreTest.setUp()
	 * @throws SQLException if a db communication error occured
	 * @throws UserDoesNotExistException if the useremail didnt match any id
	 */
	@BeforeAll
	public static void setup() throws ClassNotFoundException, SQLException, 
								UserDoesNotExistException {
		PreTest.setUp(); 
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
				+ ((long) (reminderOffset/2)*1000));
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
		} finally {ConnectionPool.getInstance().releaseConnection(conn);}
	}
	
	/**
     * Deletes the inserted test entries from the database and shuts down the 
     * ConnectionPool.
     * 
     * @throws LostConnectionException @see MediumDao.deleteUser().
     * @throws MaxConnectionsException if db is overloaded.
     * @throws MediumDoesNotExistException if mediumid couldn't be found.
     * @throws UserDoesNotExistException if userid couldn't be found.
     * @throws CopyDoesNotExistException if copyid couldn't be found.
     * @throws SQLException if a db communication error occured.
     */
	@AfterAll
	public static void tearDown() throws LostConnectionException, 
		MaxConnectionsException, UserDoesNotExistException, SQLException {
		ConnectionPool instance = ConnectionPool.getInstance();
		Connection conn = instance.fetchConnection(5000);
		try {
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
	    UserDto userDto = new UserDto();
	    userDto.setId(testUserId);
	    UserDao.deleteUser(userDto);
		} finally {
			ConnectionPool.getInstance().releaseConnection(conn);
			ConnectionPool.destroyConnectionPool();
		}   
	}
	
	/**
	 * Reads all copies that need to be returned soon and checks, if the 
	 * testcopy is among them and the corresponding testuser and testmedium are
	 * set right.
	 * 
	 * @throws LostConnectionException if db communication error occured.
	 * @throws MaxConnectionsException if db is overloaded.
	 * @throws MediumDoesNotExistException if mediumid couldn't be found.
	 * @throws UserDoesNotExistException if userid couldn't be found.
	 */
	@Test
	public void testReadDueDates() throws LostConnectionException, 
		MaxConnectionsException, MediumDoesNotExistException, 
		UserDoesNotExistException {
		List<MediumCopyUserDto> results = MediumDao.readDueDateReminders();
		CopyDto copy = new CopyDto();
		UserDto user = new UserDto();
		MediumDto medium = new MediumDto();
		for (MediumCopyUserDto element : results) {
			if (element.getCopy().getSignature().equals("maintenanceTest123") 
			  && element.getCopy().getLocation().equals("maintenanceTest123")) {
				copy = element.getCopy();
				user = element.getUser();
				medium = element.getMedium();
			}
		}
		Assertions.assertEquals(testMediumId, medium.getId());
		Assertions.assertEquals(testUserId, user.getId());
		Assertions.assertEquals(testCopyId, copy.getId());
	}
	
}
