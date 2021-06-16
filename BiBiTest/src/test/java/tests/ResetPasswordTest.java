package tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.SQLException;
import java.util.Properties;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import de.dedede.model.data.dtos.UserDto;
import de.dedede.model.data.dtos.UserLendStatus;
import de.dedede.model.data.dtos.UserRole;
import de.dedede.model.logic.util.PasswordHashingModule;
import de.dedede.model.logic.util.TokenGenerator;
import de.dedede.model.logic.util.UserVerificationStatus;
import de.dedede.model.persistence.daos.UserDao;
import de.dedede.model.persistence.exceptions.EntityInstanceNotUniqueException;
import de.dedede.model.persistence.exceptions.LostConnectionException;
import de.dedede.model.persistence.exceptions.MaxConnectionsException;
import de.dedede.model.persistence.exceptions.UserDoesNotExistException;
import de.dedede.model.persistence.util.ConfigReader;
import de.dedede.model.persistence.util.ConnectionPool;

public class ResetPasswordTest {
	
	private static Properties props;
	private static UserDto user;

	@BeforeAll
	public static void setUp() throws ClassNotFoundException, SQLException {
		setUpProperties();
		ConfigReader.getInstance().setUpConfigReader(props);
		ConnectionPool.setUpConnectionPool();
		setUpUser();
	}
	
	@AfterAll
	public static void tearDown() {
		ConnectionPool.destroyConnectionPool();
	}
	
	@Test
	public void testSettingPassword() throws MaxConnectionsException, 
			LostConnectionException, UserDoesNotExistException {
		String password = "Example456";
		setPassword(password);
		createUser();
		UserDto sndUser = new UserDto();
		sndUser.setEmailAddress(user.getEmailAddress());
		UserDao.readUserByEmail(sndUser);
		assertEquals(sndUser.getPasswordHash(), user.getPasswordHash());
	}
	
	@Test
	public void testRetrievingByToken() throws UserDoesNotExistException {
		user.setToken(TokenGenerator.generateToken());
		createUser();
		UserDto sndUser = new UserDto();
		sndUser.setToken(user.getToken());
		UserDao.readUserByToken(sndUser);
		assertTrue(user.getEmailAddress().equals(sndUser.getEmailAddress()));
	}
	
	private static void setUpProperties() {
		props = new Properties();
		props.put("DB_USER", "sep21g01");
		props.put("DB_PASSWORD", "fooZae4cuoSa");
		props.put("DB_DRIVER", "org.postgresql.Driver");
		props.put("DB_SSL", "TRUE");
		props.put("DB_SSL_FACTORY", "org.postgresql.ssl.DefaultJavaSSLFactory");
		props.put("DB_HOST", "bueno.fim.uni-passau.de");
		props.put("DB_PORT", "5432");
		props.put("DB_NAME", "sep21g01t");
		props.put("DB_URL", "jdbc:postgresql://");
		props.put("DB_CAPACITY", "20");
	}
	
	private static void createUser() throws LostConnectionException, 
			MaxConnectionsException, UserDoesNotExistException {
		if (UserDao.userExists(user)) {
			UserDao.readUserByEmail(user);
			UserDao.deleteUser(user);
		}
		UserDao.createUser(user);
	}
	
	private static void setUpUser() {
		user = new UserDto();
		user.setFirstName("FirstName");
		user.setLastName("LastName");
		user.setEmailAddress("dedede44920@gmail.com");
		user.setRole(UserRole.STAFF);
		user.setStreet("Street");
		user.setUserLendStatus(UserLendStatus.ENABLED);
		user.setUserVerificationStatus(UserVerificationStatus.VERIFIED);
		setPassword("Example123");
	}
	
	private static void setPassword(String password) {
		String salt = PasswordHashingModule.generateSalt();
		String hash = PasswordHashingModule.hashPassword(password, salt);
		user.setPasswordHash(hash);
		user.setPasswordSalt(salt);
	}
	
}
