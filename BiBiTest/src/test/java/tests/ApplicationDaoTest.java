package tests;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.SQLException;
import java.time.Duration;

import de.dedede.model.persistence.exceptions.InvalidConfigurationException;
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
	private static long id;
	
	@BeforeAll
	public static void setUp() throws ClassNotFoundException, SQLException, MaxConnectionsException,
			LostConnectionException, InvalidConfigurationException {
		ConnectionPool.setUpConnectionPool();
		initializeFirstDto();
		initializeSecondDto();
		ApplicationDao.createCustomization(current);
		other.setId(id = current.getId());
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
		current.setName("A");
		current.setEmailSuffixRegEx("A");
		current.setContactInfo("A");
		current.setSiteNotice("A");
		current.setPrivacyPolicy("A");
		current.setLogo(new byte[0]);
		current.setReturnPeriod(Duration.ofDays(10));
		current.setPickupPeriod(Duration.ofHours(5));
		current.setWarningPeriod(Duration.ofDays(2));
		current.setSystemRegistrationStatus(SystemRegistrationStatus.OPEN);
		current.setLookAndFeel("css a");
//		current.setAnonRights("OPAC");
		current.setLendingStatus("UNLOCKED");
	}

	private static void initializeSecondDto() {
		other = new ApplicationDto();
		other.setName("B");
		other.setEmailSuffixRegEx("B");
		other.setContactInfo("B");
		other.setSiteNotice("B");
		other.setPrivacyPolicy("B");
		other.setLogo(new byte[0]);
		other.setReturnPeriod(Duration.ofDays(11));
		other.setPickupPeriod(Duration.ofHours(6));
		other.setWarningPeriod(Duration.ofDays(3));
		other.setSystemRegistrationStatus(SystemRegistrationStatus.CLOSED);
		other.setLookAndFeel("css a");
//		other.setAnonRights("OPAC");
		other.setLendingStatus("UNLOCKED");
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
