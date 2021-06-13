package tests;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import de.dedede.model.data.dtos.CopyDto;
import de.dedede.model.data.dtos.CopyStatus;
import de.dedede.model.data.dtos.MediumDto;
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
	
    @BeforeAll
    public static void setUp() throws ClassNotFoundException, SQLException, InvalidConfigurationException {
		
    	InputStream is = 
				LendAndReturnCopyTest.class.getResourceAsStream("..\\BiBi\\src\\main\\webapp\\WEB-INF\\config.properties");
    	ConfigReader.getInstance().setUpConfigReader(is);
        ConnectionPool.setUpConnectionPool();
        Connection conn = ConnectionPool.getInstance().fetchConnection(5000);
        try {
        PreparedStatement testUser = conn.prepareStatement(
        		"insert into users(userID, emailAddress, passwordHashSalt, passwordHash, name, surname, postalCode, city, street, houseNumber, token, tokenCreation, userLendPeriod, lendStatus, verificationStatus, userRole) values (5, 'testemail@thisisanemailadress.com', 'aqweoiaskfjas', '02004cb3f812178459668835c436b5b2d7f8aa3481c24a52a5a4e34b6d6527f6', 'testuser123', 'testuser123', 'testcity123', 'teststreet123', 'testhouse123', 'testtoken01234567890', CURRENT_TIMESTAMP, '11:22:33', 'ENABLED', 'VERIFIED', 'REGISTERED');"
        		);
        int one1 = testUser.executeUpdate();
        conn.commit();
        testUser.close();
        PreparedStatement testCopy = conn.prepareStatement(
        		"insert into mediumcopy(copyID, mediumid, signature, bibposition, status, deadline, actor) values (5, 2, 'testSignature123', 'testPosition123', 'AVAILABLE', null, null);"
        		);
        int one2 = testCopy.executeUpdate();
        conn.commit();
        testCopy.close();
        System.out.println(one1 + " insert into users and " + one2 +  " insert into mediumcopy for test");
		} finally {ConnectionPool.getInstance().releaseConnection(conn);}
    }
    
    @AfterAll
    public static void tearDown() throws LostConnectionException, MaxConnectionsException, MediumDoesNotExistException, UserDoesNotExistException {
        CopyDto copyDto = new CopyDto();
        copyDto.setId(5);
        MediumDao.deleteCopy(copyDto);
        UserDto userDto = new UserDto();
        userDto.setId(5);
        UserDao.deleteUser(userDto);
        ConnectionPool.destroyConnectionPool();
    }
	
    @Test
	public static void lendAndReturnCopyTest() throws CopyDoesNotExistException, InvalidUserForCopyException, CopyIsNotAvailableException, UserDoesNotExistException, LostConnectionException, MaxConnectionsException, MediumDoesNotExistException {
    	UserDto email = new UserDto();
    	email.setEmailAddress("testemail@thisisanemailadress.com");
    	MediumDto id = new MediumDto();
    	id.setId(2);
    	CopyDto signature = new CopyDto();
    	signature.setSignature("testSignature123");
		MediumDao.lendCopy(signature, email);
		Assertions.assertEquals(CopyStatus.BORROWED, MediumDao.readMedium(id).getCopy(5).getCopyStatus());
		Assertions.assertEquals(5, MediumDao.readMedium(id).getCopy(5).getActor());
		MediumDao.returnCopy(signature, email);
		Assertions.assertEquals(CopyStatus.AVAILABLE, MediumDao.readMedium(id).getCopy(5).getCopyStatus());
		Assertions.assertEquals(null, MediumDao.readMedium(id).getCopy(5).getActor());
	}
    
}
