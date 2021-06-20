package tests;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.Duration;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import de.dedede.model.logic.util.RegisteredUserLendStatus;
import de.dedede.model.logic.util.SystemAnonAccess;
import de.dedede.model.logic.util.SystemRegistrationStatus;
import de.dedede.model.persistence.exceptions.InvalidConfigurationException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import de.dedede.model.data.dtos.ApplicationDto;
import de.dedede.model.persistence.daos.ApplicationDao;
import de.dedede.model.persistence.exceptions.EntityInstanceDoesNotExistException;
import de.dedede.model.persistence.exceptions.LostConnectionException;
import de.dedede.model.persistence.exceptions.MaxConnectionsException;
import de.dedede.model.persistence.util.ConfigReader;
import de.dedede.model.persistence.util.ConnectionPool;

class ConnectionPoolTest {

	private static ConnectionPool poolInstance;
	private static final long CONNECTION_TIMEOUT = 5000;
	private static ApplicationDto dto;
	private boolean resetPool = false;
	private List<Connection> connections = new LinkedList<>();
	
	@BeforeAll
	public static void setUp() throws ClassNotFoundException, SQLException, InvalidConfigurationException {
		PreTest.setUp();
		poolInstance = ConnectionPool.getInstance();
		initializeDto();
	}
	
	@AfterAll
	public static void tearDown() {
		ConnectionPool.destroyConnectionPool();
	}
	
	@AfterEach
	public void resetPool() {
		if (resetPool) {
			for (Connection conn : connections) {
				poolInstance.releaseConnection(conn);
			}
		}
		resetPool = false;
	}
	
	@Test
	public void testExhaustingConnectionPool() {
		resetPool = true;
		assertThrows(MaxConnectionsException.class, () -> exhaustConnectionPool());
	}
	
	private void exhaustConnectionPool() {
		while (true) {
			connections.add(poolInstance.fetchConnection(CONNECTION_TIMEOUT));
		}
	}
	
	@Test
	public void testGettingAndReturningConnection() throws MaxConnectionsException {
		Connection conn = poolInstance.fetchConnection(CONNECTION_TIMEOUT);
		poolInstance.releaseConnection(conn);
	}
	
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
