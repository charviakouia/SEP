package tests;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.SQLException;
import java.time.Duration;

import de.dedede.model.persistence.exceptions.InvalidConfigurationException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.dedede.model.data.dtos.ApplicationDto;
import de.dedede.model.logic.util.RegisteredUserLendStatus;
import de.dedede.model.logic.util.SystemAnonAccess;
import de.dedede.model.logic.util.SystemRegistrationStatus;
import de.dedede.model.persistence.daos.ApplicationDao;
import de.dedede.model.persistence.exceptions.EntityInstanceDoesNotExistException;
import de.dedede.model.persistence.exceptions.LostConnectionException;
import de.dedede.model.persistence.exceptions.MaxConnectionsException;
import de.dedede.model.persistence.util.ConnectionPool;

class ApplicationDaoTest {
	
	private static ApplicationDto current, first, second;
	
	@BeforeAll
	public static void setUp() throws ClassNotFoundException, SQLException, MaxConnectionsException,
			LostConnectionException, InvalidConfigurationException {
		PreTest.setUp();
		initializeFirstDto();
		initializeSecondDto();
		ApplicationDao.createCustomization(current = first);
		second.setId(current.getId());
	}
	
	@AfterAll
	public static void tearDown() throws LostConnectionException, MaxConnectionsException, 
			EntityInstanceDoesNotExistException {
		ApplicationDao.deleteCustomization(current);
		ConnectionPool.destroyConnectionPool();
	}

	@Test
	public void testReading() throws MaxConnectionsException, LostConnectionException {
		ApplicationDto newDto = new ApplicationDto();
		newDto.setId(current.getId());
		newDto = ApplicationDao.readCustomization(newDto);
		assertEquals(current.getSystemRegistrationStatus(), newDto.getSystemRegistrationStatus());
	}
	
	@Test
	public void testUpdatingAndReading() throws MaxConnectionsException, EntityInstanceDoesNotExistException, 
			LostConnectionException {
		ApplicationDao.updateCustomization(current = second);
		ApplicationDto newDto = new ApplicationDto();
		newDto.setId(current.getId());
		newDto = ApplicationDao.readCustomization(newDto);
		assertEquals(current.getSystemRegistrationStatus(), newDto.getSystemRegistrationStatus());
	}
	
	private static void initializeFirstDto() {
		first = new ApplicationDto();
		first.setName("A");
		first.setEmailSuffixRegEx("A");
		first.setContactInfo("A");
		first.setSiteNotice("A");
		first.setPrivacyPolicy("A");
		first.setLogo(new byte[0]);
		first.setReturnPeriod(Duration.ofDays(10));
		first.setPickupPeriod(Duration.ofHours(5));
		first.setWarningPeriod(Duration.ofDays(2));
		first.setSystemRegistrationStatus(SystemRegistrationStatus.OPEN);
		first.setAnonRights(SystemAnonAccess.OPAC);
		first.setLendingStatus(RegisteredUserLendStatus.UNLOCKED);
	}

	private static void initializeSecondDto() {
		second = new ApplicationDto();
		second.setName("B");
		second.setEmailSuffixRegEx("B");
		second.setContactInfo("B");
		second.setSiteNotice("B");
		second.setPrivacyPolicy("B");
		second.setLogo(new byte[0]);
		second.setReturnPeriod(Duration.ofDays(11));
		second.setPickupPeriod(Duration.ofHours(6));
		second.setWarningPeriod(Duration.ofDays(3));
		second.setSystemRegistrationStatus(SystemRegistrationStatus.CLOSED);
		second.setAnonRights(SystemAnonAccess.OPAC);
		second.setLendingStatus(RegisteredUserLendStatus.UNLOCKED);
	}
	
}
