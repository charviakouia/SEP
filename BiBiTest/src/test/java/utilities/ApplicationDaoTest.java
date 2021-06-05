package utilities;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.SQLException;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import de.dedede.model.data.dtos.ApplicationDto;
import de.dedede.model.logic.util.SystemRegistrationStatus;
import de.dedede.model.persistence.daos.ApplicationDao;
import de.dedede.model.persistence.exceptions.EntityInstanceDoesNotExistException;
import de.dedede.model.persistence.exceptions.LostConnectionException;
import de.dedede.model.persistence.exceptions.MaxConnectionsException;
import de.dedede.model.persistence.util.ConnectionPool;

class ApplicationDaoTest {
	
	private static ApplicationDto current, other;
	private static final long id = 100;
	
	@BeforeAll
	public static void setUp() throws ClassNotFoundException, SQLException, MaxConnectionsException, 
			LostConnectionException {
		ConnectionPool.setUpConnectionPool();
		initializeFirstDto();
		initializeSecondDto();
		ApplicationDao.createCustomization(current);
	}
	
	@AfterAll
	public static void tearDown() throws LostConnectionException, MaxConnectionsException, 
			EntityInstanceDoesNotExistException {
		ApplicationDao.deleteCustomization(current);
		ConnectionPool.destroyConnectionPool();
	}

	@Test
	public void testReading() throws MaxConnectionsException, LostConnectionException {
		ApplicationDto dto = readById(id);
		assertEquals(current.getSystemRegistrationStatus(), dto.getSystemRegistrationStatus());
	}
	
	@Test
	public void testUpdatingAndReading() throws MaxConnectionsException, EntityInstanceDoesNotExistException, 
			LostConnectionException {
		ApplicationDao.updateCustomization(other);
		swapDtos();
		ApplicationDto dto = readById(id);
		assertEquals(current.getSystemRegistrationStatus(), dto.getSystemRegistrationStatus());
	}
	
	private static void initializeFirstDto() {
		current = new ApplicationDto();
		current.setId(id);
		current.setSystemRegistrationStatus(SystemRegistrationStatus.CLOSED);
	}

	private static void initializeSecondDto() {
		other = new ApplicationDto();
		other.setId(id);
		other.setSystemRegistrationStatus(SystemRegistrationStatus.OPEN);
	}
	
	private ApplicationDto readById(long id) throws LostConnectionException, MaxConnectionsException {
		ApplicationDto newDto = new ApplicationDto();
		newDto.setId(id);
		newDto = ApplicationDao.readCustomization(newDto);
		return newDto;
	}
	
	private void swapDtos() {
		ApplicationDto temp = current;
		current = other;
		other = temp;
	}
	
}
