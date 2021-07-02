package tests;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.Duration;
import java.util.LinkedList;
import java.util.List;

import test.java.tests.PreTest;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import de.dedede.model.logic.util.RegisteredUserLendStatus;
import de.dedede.model.logic.util.SystemAnonAccess;
import de.dedede.model.logic.util.SystemRegistrationStatus;
import de.dedede.model.persistence.exceptions.InvalidConfigurationException;
import de.dedede.model.data.dtos.ApplicationDto;
import de.dedede.model.persistence.daos.ApplicationDao;
import de.dedede.model.persistence.exceptions.EntityInstanceDoesNotExistException;
import de.dedede.model.persistence.exceptions.LostConnectionException;
import de.dedede.model.persistence.exceptions.MaxConnectionsException;
import de.dedede.model.persistence.util.ConnectionPool;

/**
 * Tests the functionality of the connection pool. In particular, connections are retrieved, returned, and used
 * to conduct basic operations in the data store. In addition, the pool's capacity is tested by depleting it of all
 * connections.
 *
 * @author Ivan Charviakou
 */
class ConnectionPoolTest {

	private static final long CONNECTION_TIMEOUT = 5000;

	private static ApplicationDto dto;
	private static int capacity;

	private boolean resetPool = false;
	private List<Connection> connections = new LinkedList<>();
	
	@BeforeAll
	public static void setUp() throws ClassNotFoundException, SQLException, InvalidConfigurationException {
		PreTest.setUp();
		initializeDto();
		capacity = Integer.parseInt(PreTest.getProperty("DB_CAPACITY"));
	}
	
	@AfterAll
	public static void tearDown() {
		ConnectionPool.destroyConnectionPool();
	}
	
	@AfterEach
	public void resetPool() {
		if (resetPool) {
			for (Connection conn : connections) {
				ConnectionPool.getInstance().releaseConnection(conn);
			}
		}
		resetPool = false;
	}

	/**
	 * Ensures that the connection pool reacts correctly to being depleted of all its connections.
	 */
	@Test
	public void testExhaustingConnectionPool() {
		resetPool = true;
		assertDoesNotThrow(this::exhaustConnectionPool);
		ConnectionPool pool = ConnectionPool.getInstance();
		assertThrows(MaxConnectionsException.class, () -> pool.fetchConnection(CONNECTION_TIMEOUT));
	}
	
	private void exhaustConnectionPool() {
		for (int i = 0; i < capacity; i++){
			Connection conn = ConnectionPool.getInstance().fetchConnection(CONNECTION_TIMEOUT);
			connections.add(conn);
		}
	}

	/**
	 * Tests whether connection retrieval and return work as expected.
	 *
	 * @throws MaxConnectionsException	Is thrown when no connection is currently available
	 */
	@Test
	public void testGettingAndReturningConnection() throws MaxConnectionsException {
		Connection conn = ConnectionPool.getInstance().fetchConnection(CONNECTION_TIMEOUT);
		ConnectionPool.getInstance().releaseConnection(conn);
	}

	/**
	 * Ensures that the returned connections are valid and can be used to perform operations in the datastore.
	 *
	 * @throws MaxConnectionsException				Is thrown when no connection is available to carry out the
	 * 												operation
	 * @throws LostConnectionException				Is thrown when the used connection stopped working correctly
	 * 												before completing the transaction.
	 * @throws EntityInstanceDoesNotExistException	Is thrown if the queried id isn't present in the datastore,
	 * 												typically the result of a race-condition
	 */
	@Test
	public void testDataCreationAndDeletion() throws MaxConnectionsException, LostConnectionException,
			EntityInstanceDoesNotExistException {
		ApplicationDao.createCustomization(dto);
		ApplicationDao.deleteCustomization(dto);
	}
	
	private static void initializeDto() {
		dto = new ApplicationDto();
		dto.setName("Luigi's library");
		dto.setEmailSuffixRegEx("mushroomKingdom");
		dto.setContactInfo("1-800-FIRE-FLOWER");
		dto.setSiteNotice("For legal matters, contact Dr. E. Gadd");
		dto.setPrivacyPolicy("For privacy concerns, contact King Boo");
		dto.setLogo(new byte[0]);
		dto.setReturnPeriod(Duration.ofDays(12));
		dto.setPickupPeriod(Duration.ofHours(7));
		dto.setWarningPeriod(Duration.ofDays(4));
		dto.setSystemRegistrationStatus(SystemRegistrationStatus.OPEN);
		dto.setAnonRights(SystemAnonAccess.OPAC);
		dto.setLendingStatus(RegisteredUserLendStatus.UNLOCKED);
	}

}
