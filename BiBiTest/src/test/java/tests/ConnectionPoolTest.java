package tests;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.Duration;

import de.dedede.model.logic.util.SystemRegistrationStatus;
import de.dedede.model.persistence.exceptions.InvalidConfigurationException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import de.dedede.model.data.dtos.ApplicationDto;
import de.dedede.model.persistence.daos.ApplicationDao;
import de.dedede.model.persistence.exceptions.EntityInstanceDoesNotExistException;
import de.dedede.model.persistence.exceptions.LostConnectionException;
import de.dedede.model.persistence.exceptions.MaxConnectionsException;
import de.dedede.model.persistence.util.ConnectionPool;

class ConnectionPoolTest {

	private static ConnectionPool poolInstance;
	private static final long connectionTimeout = ConnectionPool.getAcquiringConnectionPeriod() * 5;
	private static ApplicationDto dto;
	
	@BeforeAll
	public static void setUp() throws ClassNotFoundException, SQLException, InvalidConfigurationException {
		ConnectionPool.setUpConnectionPool();
		poolInstance = ConnectionPool.getInstance();
		initializeDto();
	}
	
	@AfterAll
	public static void tearDown() {
		ConnectionPool.destroyConnectionPool();
	}
	
	@Test
	public void testGettingAndReturningConnection() throws MaxConnectionsException {
		Connection conn = poolInstance.fetchConnection(connectionTimeout);
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
		dto.setEmailAddressSuffixRegEx("mushroomKingdom");
		dto.setContactInfo("1-800-FIRE-FLOWER");
		dto.setSiteNotice("For legal matters, contact Dr. E. Gadd");
		dto.setPrivacyPolicy("For privacy concerns, contact King Boo");
		dto.setLogo(new byte[0]);
		dto.setReturnPeriod(Duration.ofDays(12));
		dto.setPickupPeriod(Duration.ofHours(7));
		dto.setWarningPeriod(Duration.ofDays(4));
		dto.setSystemRegistrationStatus(SystemRegistrationStatus.OPEN);
		dto.setLookAndFeel("css a");
		dto.setAnonRights("OPAC");
		dto.setLendingStatus("UNLOCKED");
	}

}
