package tests;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import de.dedede.model.data.dtos.CopyDto;
import de.dedede.model.data.dtos.CopyStatus;
import de.dedede.model.data.dtos.UserDto;
import de.dedede.model.persistence.daos.MediumDao;
import de.dedede.model.persistence.daos.UserDao;
import de.dedede.model.persistence.exceptions.CopyDoesNotExistException;
import de.dedede.model.persistence.exceptions.CopyIsNotAvailableException;
import de.dedede.model.persistence.exceptions.InvalidUserForCopyException;
import de.dedede.model.persistence.exceptions.LostConnectionException;
import de.dedede.model.persistence.exceptions.MaxConnectionsException;
import de.dedede.model.persistence.exceptions.MediumDoesNotExistException;
import de.dedede.model.persistence.exceptions.UserDoesNotExistException;
import de.dedede.model.persistence.util.ConnectionPool;

/**
 * Test MediumDao Methods used for returning and lending copies.
 * 
 * @author Jonas Picker
 */
public class LendAndReturnCopyTest {
	
	private static int testMediumId;
	private static int testCategoryId;
	private static int testUserId;
	
	/**
	 * Initializes PreTest-Dummies first, then inserts a test user and copy, 
	 * but needs to insert test category and medium as well to satisfy foreign 
	 * key dependencies.
	 * 
	 * @throws ClassNotFoundException @see PreTest.setUp()
	 * @throws SQLException if a db communication error occured
	 * @throws UserDoesNotExistException if the useremail didnt match any id
	 */
    @BeforeAll
    public static void setUp() throws ClassNotFoundException, SQLException,
    										UserDoesNotExistException {
    	PreTest.setUp();
        Connection conn = ConnectionPool.getInstance().fetchConnection(5000);
        try {
        testUserId = MaintenanceProcessTest.insertTestUser(conn);
        testCategoryId = MaintenanceProcessTest.insertTestCategory(conn);
        testMediumId = MaintenanceProcessTest.insertTestMedium(conn, 
        		testCategoryId);
        PreparedStatement testCopy = conn.prepareStatement(
        		"insert into mediumcopy(copyID, mediumid, signature, "
        		+ "bibposition, status, deadline, actor) values "
        		+ "(default, ?, 'lendAndReturnTest123', 'lendAndReturnTest123',"
        		+ " 'AVAILABLE', null, null);"
        		);
        testCopy.setInt(1, testMediumId);
        testCopy.executeUpdate();
        conn.commit();
        testCopy.close();
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
    	MaxConnectionsException, MediumDoesNotExistException,
    	UserDoesNotExistException, CopyDoesNotExistException, SQLException {
    	Connection conn = ConnectionPool.getInstance().fetchConnection(5000);
    	try {    	   
    	PreparedStatement stmt = conn.prepareStatement(
    			"DELETE FROM mediumcopy WHERE (signature "
    					+ "= 'lendAndReturnTest123') AND (bibposition "
    					+ "= 'lendAndReturnTest123');"
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
     * Lends the testcopy to the testuser and checks if it was persisted as 
     * lent, then returns the copy again and checks db for correctness of 
     * transaction.
     * 
     * @throws CopyDoesNotExistException if copyid couldn't be found.
     * @throws InvalidUserForCopyException if user has invalid status to lend.
     * @throws CopyIsNotAvailableException if copy has invalid status to be lent
     * @throws UserDoesNotExistException if userid couldn't be found.
     * @throws LostConnectionException if a db communication error occured.
     * @throws MaxConnectionsException if db is overloaded.
     * @throws MediumDoesNotExistException id mediumid couldn't be found.
     * @throws SQLException if a db communication error occured.
     */
    @Test
	public void lendAndReturnCopyTest() throws CopyDoesNotExistException,
	InvalidUserForCopyException, CopyIsNotAvailableException,
	UserDoesNotExistException, LostConnectionException, MaxConnectionsException,
	MediumDoesNotExistException, SQLException {
    	UserDto email = new UserDto();
    	email.setEmailAddress("testemail@thisisanemailaddress.com");
    	CopyDto signature = new CopyDto();
    	signature.setSignature("lendAndReturnTest123");
		MediumDao.lendCopy(signature, email);
		CopyDto copy = readTestCopy();
		Assertions.assertEquals(CopyStatus.BORROWED, copy.getCopyStatus()); 
		Assertions.assertEquals(testUserId, copy.getActor());
		MediumDao.returnCopy(signature, email);
		CopyDto copy2 = readTestCopy();
		Assertions.assertEquals(CopyStatus.AVAILABLE, copy2.getCopyStatus());
		Assertions.assertEquals(null, copy2.getActor());
	}
    
    private static CopyDto readTestCopy() throws SQLException {
    	ConnectionPool instance = ConnectionPool.getInstance();
    	Connection conn = instance.fetchConnection(5000);
    	try {
    	PreparedStatement stmt = conn.prepareStatement(
    			"SELECT copyID, mediumid, signature, bibposition, status,"
    					+ " deadline, actor FROM mediumcopy WHERE (signature ="
    					+ " 'lendAndReturnTest123') AND (bibposition = "
    					+ "'lendAndReturnTest123');"
    			);
    	ResultSet rs1 = stmt.executeQuery();
    	rs1.next();
    	CopyDto copy = new CopyDto();
    	copy.setId(rs1.getInt(1));
    	copy.setMediumId(rs1.getInt(2));
    	copy.setSignature(rs1.getString(3));
    	copy.setLocation(rs1.getString(4));
    	CopyStatus copyStatus = CopyStatus.valueOf(rs1.getString(5));
    	copy.setCopyStatus(copyStatus);
    	copy.setDeadline(rs1.getTimestamp(6));
    	copy.setActor(rs1.getInt(7));
    	if (copy.getActor() == 0) {
    		copy.setActor(null);
    	}
    	return copy;
    	} finally {ConnectionPool.getInstance().releaseConnection(conn);}
    }
    
}
