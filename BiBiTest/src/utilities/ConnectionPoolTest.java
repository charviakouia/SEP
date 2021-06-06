package utilities;

import java.sql.Connection;
import java.sql.SQLException;

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
	private static final long connectionTimeout = ConnectionPool.ACQUIRING_CONNECTION_PERIOD * 5;
	private static ApplicationDto dto;
	
	@BeforeAll
	public static void setUp() throws ClassNotFoundException, SQLException {
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
	public void testDataCreationAndDeletion() throws MaxConnectionsException, LostConnectionException, EntityInstanceDoesNotExistException {
		ApplicationDao.createCustomization(dto);
		ApplicationDao.deleteCustomization(dto);
	}
	
	private static void initializeDto() {
		dto = new ApplicationDto();
		dto.setId(100);
	}

}
