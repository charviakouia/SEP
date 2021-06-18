package tests;

import java.io.InputStream;
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
import de.dedede.model.persistence.exceptions.InvalidConfigurationException;
import de.dedede.model.persistence.exceptions.InvalidUserForCopyException;
import de.dedede.model.persistence.exceptions.LostConnectionException;
import de.dedede.model.persistence.exceptions.MaxConnectionsException;
import de.dedede.model.persistence.exceptions.MediumDoesNotExistException;
import de.dedede.model.persistence.exceptions.UserDoesNotExistException;
import de.dedede.model.persistence.util.ConfigReader;
import de.dedede.model.persistence.util.ConnectionPool;

public class LendAndReturnCopyTest {
	
	private static int testMediumId;
	private static int testCategoryId;
	private static int testUserId;
	
    @BeforeAll
    public static void setUp() throws ClassNotFoundException, SQLException, InvalidConfigurationException {
    	InputStream is = 
				LendAndReturnCopyTest.class.getResourceAsStream("..\\BiBi\\src\\main\\webapp\\WEB-INF\\config.properties"); //im Arsch
    	ConfigReader.getInstance().setUpConfigReader(is);
        ConnectionPool.setUpConnectionPool();
        Connection conn = ConnectionPool.getInstance().fetchConnection(5000);
        try {
        testUserId = MaintenanceProcessTest.insertTestUser(conn);
        testCategoryId = MaintenanceProcessTest.insertTestCategory(conn);
        testMediumId = MaintenanceProcessTest.insertTestMedium(conn, testCategoryId);
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
    
    @AfterAll
    public static void tearDown() throws LostConnectionException, 
    	MaxConnectionsException, MediumDoesNotExistException,
    	UserDoesNotExistException, CopyDoesNotExistException {
       try {
    	   Connection conn = ConnectionPool.getInstance().fetchConnection(5000);    	   
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
       } catch (SQLException e) {
    	   e.printStackTrace();
       }
        UserDto userDto = new UserDto();
        userDto.setEmailAddress("testemail@thisisanemailadress.com");
        UserDao.deleteUser(userDto);
        ConnectionPool.destroyConnectionPool();
    }
	
    @Test
	public static void lendAndReturnCopyTest() throws CopyDoesNotExistException,
	InvalidUserForCopyException, CopyIsNotAvailableException,
	UserDoesNotExistException, LostConnectionException, MaxConnectionsException,
	MediumDoesNotExistException {
    	UserDto email = new UserDto();
    	email.setEmailAddress("testemail@thisisanemailadress.com");
    	CopyDto signature = new CopyDto();
    	signature.setSignature("testSignature123");
		MediumDao.lendCopy(signature, email);
		CopyDto copy = readTestCopy();
		Assertions.assertEquals(CopyStatus.BORROWED,copy.getCopyStatus()); 
		Assertions.assertEquals(testUserId, copy.getActor());
		MediumDao.returnCopy(signature, email);
		Assertions.assertEquals(CopyStatus.AVAILABLE, copy.getCopyStatus());
		Assertions.assertEquals(null, copy.getActor());
	}
    
    private static CopyDto readTestCopy() {
    	try {
    		Connection conn = ConnectionPool.getInstance().fetchConnection(5000);
    		PreparedStatement stmt = conn.prepareStatement(
    				"SELECT copyID, mediumid, signature, bibposition, status,"
    				+ " deadline, actor FROM mediumcopy WHERE (signature ="
    				+ " 'testSignature123') AND (bibposition = "
    				+ "'testSignature123');"
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
    	} catch (SQLException e) {
    		e.printStackTrace();
    		return null;
    	}
    }
    
}
