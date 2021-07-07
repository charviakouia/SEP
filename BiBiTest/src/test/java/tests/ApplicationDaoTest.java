package tests;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.SQLException;
import java.time.Duration;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Queue;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
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
import de.dedede.model.persistence.exceptions.InvalidConfigurationException;

/**
 * Tests the functionality of the application-DAO {@link ApplicationDao}. In particular, the read and update
 * operations are examined. For this, a series of corresponding entries are created and later deleted.
 *
 * @author Ivan Charviakou
 */
class ApplicationDaoTest {

	private static Queue<ApplicationDto> nextEntry = new LinkedList<>();
	private static Collection<Long> createdEntries = new LinkedList<>();
	
	@BeforeAll
	public static void setUp() throws ClassNotFoundException, SQLException, MaxConnectionsException,
			LostConnectionException, InvalidConfigurationException {
		PreTest.setUp();
		initializeDto("A", "A", "A", "A", "A", new byte[0],
				Duration.ofDays(10), Duration.ofDays(5), Duration.ofDays(2), SystemRegistrationStatus.OPEN,
				SystemAnonAccess.OPAC, RegisteredUserLendStatus.UNLOCKED);
		initializeDto("B", "B", "B", "B", "B", new byte[0],
				Duration.ofDays(11), Duration.ofDays(6), Duration.ofDays(3), SystemRegistrationStatus.CLOSED,
				SystemAnonAccess.OPAC, RegisteredUserLendStatus.UNLOCKED);
		saveNextDto();
	}
	
	@AfterAll
	public static void tearDown() throws LostConnectionException, MaxConnectionsException, 
			EntityInstanceDoesNotExistException {
		ApplicationDto current = new ApplicationDto();
		for (long id : createdEntries){
			current.setId(id);
			ApplicationDao.deleteCustomization(current);
		}
		ConnectionPool.destroyConnectionPool();
	}

	/**
	 * Tests whether a user can be retrieved by the id, with which the user is associated with in the data store.
	 *
	 * @throws MaxConnectionsException	Is thrown when no connection is available to carry out the operation
	 * @throws LostConnectionException	Is thrown when the used connection stopped working correctly before completing
	 * 									the transaction.
	 */
	@Test
	public void testReading() throws MaxConnectionsException, LostConnectionException {
		ApplicationDto newDto = replicateId(nextEntry.peek(), new ApplicationDto());
		ApplicationDao.readCustomization(newDto);
		assertEquals(nextEntry.peek().getSystemRegistrationStatus(), newDto.getSystemRegistrationStatus());
	}

	/**
	 * Tests whether a user can be retrieved by its associated id after it has ben updated.
	 *
	 * @throws MaxConnectionsException				Is thrown when no connection is available to carry out the
	 * 												operation
	 * @throws EntityInstanceDoesNotExistException	Is thrown if the queried id isn't present in the datastore,
	 * 												typically the result of a race-condition
	 * @throws LostConnectionException				Is thrown when the used connection stopped working correctly
	 * 												before completing the transaction.
	 */
	@Test
	public void testUpdatingAndReading() throws MaxConnectionsException, EntityInstanceDoesNotExistException, 
			LostConnectionException {
		replicateIdToNextDto();
		ApplicationDao.updateCustomization(nextEntry.peek());
		ApplicationDto newDto = replicateId(nextEntry.peek(), new ApplicationDto());
		ApplicationDao.readCustomization(newDto);
		assertEquals(nextEntry.peek().getSystemRegistrationStatus(), newDto.getSystemRegistrationStatus());
	}

	private static ApplicationDto initializeDto(String name, String emailRegex, String contactInfo,
			String siteNotice, String privacyPolicy, byte[] logo, Duration returnPeriod, Duration pickupPeriod,
			Duration warningPeriod, SystemRegistrationStatus registrationStatus, SystemAnonAccess access,
			RegisteredUserLendStatus userLendStatus){
		ApplicationDto appDto = createDto(name, emailRegex, contactInfo, siteNotice, privacyPolicy, logo,
				returnPeriod, pickupPeriod, warningPeriod, registrationStatus, access, userLendStatus);
		nextEntry.offer(appDto);
		return appDto;
	}

	private static ApplicationDto createDto(String name, String emailRegex, String contactInfo,
			String siteNotice, String privacyPolicy, byte[] logo, Duration returnPeriod, Duration pickupPeriod,
			Duration warningPeriod, SystemRegistrationStatus registrationStatus, SystemAnonAccess access,
			RegisteredUserLendStatus userLendStatus){
			ApplicationDto result = new ApplicationDto();
		result.setName(name);
		result.setEmailSuffixRegEx(emailRegex);
		result.setContactInfo(contactInfo);
		result.setSiteNotice(siteNotice);
		result.setPrivacyPolicy(privacyPolicy);
		result.setLogo(logo);
		result.setReturnPeriod(returnPeriod);
		result.setPickupPeriod(pickupPeriod);
		result.setWarningPeriod(warningPeriod);
		result.setSystemRegistrationStatus(registrationStatus);
		result.setAnonRights(access);
		result.setLendingStatus(userLendStatus);
		return result;
	}

	private static void saveNextDto(){
		nextEntry.offer(nextEntry.poll());
		ApplicationDto target = nextEntry.peek();
		ApplicationDao.createCustomization(target);
		createdEntries.add(target.getId());
	}

	private static void replicateIdToNextDto(){
		ApplicationDto previous = nextEntry.poll();
		nextEntry.offer(previous);
		replicateId(previous, nextEntry.peek());
	}

	private static ApplicationDto replicateId(ApplicationDto source, ApplicationDto dest){
		dest.setId(source.getId());
		return dest;
	}
	
}
