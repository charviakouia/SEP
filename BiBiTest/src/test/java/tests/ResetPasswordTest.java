package tests;

import test.java.tests.PreTest;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.SQLException;
import java.util.Properties;

import de.dedede.model.persistence.exceptions.*;
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
import de.dedede.model.persistence.util.ConfigReader;
import de.dedede.model.persistence.util.ConnectionPool;

/**
 * Tests the password-reset functionality, as represented by the corresponding collection of {@link UserDao} methods.
 * In particular, user passwords and tokens are updated for a user, which are then used to retrieve the same user.
 *
 * @author Ivan Charviakou
 */
public class ResetPasswordTest {
	
	private static UserDto user;

	@BeforeAll
	public static void setUp() throws ClassNotFoundException, SQLException {
		PreTest.setUp();
		setUpUser();
		saveUser();
	}
	
	@AfterAll
	public static void tearDown() throws UserDoesNotExistException {
		UserDao.deleteUser(user);
		ConnectionPool.destroyConnectionPool();
	}

	/**
	 * Tests whether a user's password can be reset. To do this, the existing user's password hash is compared to
	 * the updated user's password hash, which is retrieved from the datastore.
	 *
	 * @throws MaxConnectionsException                  Is thrown when no connection is available to carry out the
	 * 													operation
	 * @throws LostConnectionException                  Is thrown when the used connection stopped working correctly
	 * 													before completing the transaction.
	 * @throws UserDoesNotExistException                Is thrown if the queried user isn't present in the datastore,
	 *  												typically the result of a race-condition
	 * @throws EntityInstanceDoesNotExistException      Is thrown if the queried user isn't present in the datastore,
	 * 		  											typically the result of a race-condition
	 */
	@Test
	public void testSettingPassword() throws MaxConnectionsException,
			LostConnectionException, UserDoesNotExistException, EntityInstanceDoesNotExistException {
		setPassword("Example456");
		UserDao.updateUser(user);
		UserDto sndUser = replicateUserEmail(user, new UserDto());
		UserDao.readUserByEmail(sndUser);
		assertEquals(sndUser.getPasswordHash(), user.getPasswordHash());
	}

	/**
	 * Tests whether a user's token can be used to retrieve the given user after being updated.
	 *
	 * @throws UserDoesNotExistException                Is thrown if the queried user isn't present in the datastore,
	 *  												typically the result of a race-condition
	 * @throws EntityInstanceDoesNotExistException      Is thrown if the queried user isn't present in the datastore,
	 * 		  											typically the result of a race-condition
	 */
	@Test
	public void testRetrievingByToken() throws UserDoesNotExistException, EntityInstanceDoesNotExistException {
		user.setToken(TokenGenerator.generateToken());
		UserDao.updateUser(user);
		UserDto sndUser = replicateUserToken(user, new UserDto());
		UserDao.readUserByToken(sndUser);
		assertTrue(user.getEmailAddress().equals(sndUser.getEmailAddress()));
	}

	private UserDto replicateUserToken(UserDto source, UserDto destination){
		destination.setToken(source.getToken());
		return destination;
	}

	private UserDto replicateUserEmail(UserDto source, UserDto destination){
		destination.setEmailAddress(source.getEmailAddress());
		return destination;
	}

	private static void saveUser() throws LostConnectionException, MaxConnectionsException {
		if (UserDao.userExists(user)) {
			throw new IllegalStateException("Cannot perform test for existing user");
		} else {
			UserDao.createUser(user);
		}
	}
	
	private static void setUpUser() {
		user = new UserDto();
		user.setFirstName("FirstName");
		user.setLastName("LastName");
		user.setEmailAddress("luigiNumberOne@gmail.com");
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
