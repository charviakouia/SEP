package tests;

import static org.junit.jupiter.api.Assertions.*;

import test.java.tests.PreTest;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.TimeUnit;

import de.dedede.model.persistence.exceptions.*;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import de.dedede.model.data.dtos.ApplicationDto;
import de.dedede.model.data.dtos.CopyDto;
import de.dedede.model.data.dtos.CopyStatus;
import de.dedede.model.data.dtos.MediumCopyUserDto;
import de.dedede.model.data.dtos.MediumDto;
import de.dedede.model.data.dtos.PaginationDto;
import de.dedede.model.data.dtos.TokenDto;
import de.dedede.model.data.dtos.UserDto;
import de.dedede.model.data.dtos.UserLendStatus;
import de.dedede.model.data.dtos.UserRole;
import de.dedede.model.logic.util.PasswordHashingModule;
import de.dedede.model.logic.util.RegisteredUserLendStatus;
import de.dedede.model.logic.util.SystemAnonAccess;
import de.dedede.model.logic.util.SystemRegistrationStatus;
import de.dedede.model.logic.util.TokenGenerator;
import de.dedede.model.logic.util.UserVerificationStatus;
import de.dedede.model.persistence.daos.ApplicationDao;
import de.dedede.model.persistence.daos.MediumDao;
import de.dedede.model.persistence.daos.UserDao;
import de.dedede.model.persistence.util.ConnectionPool;

/**
 * Tests the functionality of the {@link MediumDao#readAllOverdueCopies(PaginationDto)} method, which
 * returns all the overdue medium-copies in the system. To do this, medium-copies are created and queried with
 * different deadline lengths. To ensure testing accuracy, this test can only be run if the specified
 * {@link UserDto} is unique.
 *
 * @author Ivan Charviakou
 */
class LendingViolationsTest {
	
	private static final int PAGE_SIZE = 10;
	private static final int SHORT_TIME_SECONDS = 10;
	private static final int LONG_TIME_SECONDS = 5 * 60 * 60;
	private static final int EXTRA_DELAY_SECONDS = 2;

	private static UserDto user;
	private static MediumDto medium;

	private int signatureCounter = 0;
	private Collection<Integer> createdCopies = new LinkedList<>();
	
	@BeforeAll
	public static void setUp() throws ClassNotFoundException, SQLException, MaxConnectionsException,
			LostConnectionException, InvalidConfigurationException, UserDoesNotExistException {
		PreTest.setUp();
		initializeUserDto();
		saveUser();
		initializeMediumDto();
		MediumDao.createMedium(medium);
	}
	
	@AfterAll
	public static void tearDown() throws LostConnectionException, MaxConnectionsException,
			EntityInstanceDoesNotExistException, MediumDoesNotExistException, UserDoesNotExistException {
		UserDao.deleteUser(user);
		MediumDao.deleteMedium(medium);
		ConnectionPool.destroyConnectionPool();
	}

	/**
	 * Ensures that the returned set of overdue copies is of the correct size and adheres to the specified
	 * pagination settings.
	 *
	 * @throws LostConnectionException            	Is thrown when the used connection stopped working correctly
	 * 												before completing the transaction.
	 * @throws MaxConnectionsException              Is thrown when no connection is available to carry out the
	 *  											operation
	 * @throws EntityInstanceNotUniqueException     Is thrown if the queried id isn't present in the datastore,
	 * 												typically the result of a race-condition
	 */
	@Test
	public void testResultSetSize() throws LostConnectionException, MaxConnectionsException,
			EntityInstanceNotUniqueException {
		for (int i = 0; i < PAGE_SIZE + 2; i++) {
			saveCopy(true, medium);
		}
		List<MediumCopyUserDto> readList = readOverdueCopiesForPage(1);
		assertEquals(2, readList.size());
	}

	/**
	 * Ensures that the returned set of overdue copies reflects overdue entries only.
	 *
	 * @throws LostConnectionException            	Is thrown when the used connection stopped working correctly
	 * 												before completing the transaction.
	 * @throws MaxConnectionsException              Is thrown when no connection is available to carry out the
	 *  											operation
	 * @throws EntityInstanceNotUniqueException     Is thrown if the queried id isn't present in the datastore,
	 * 												typically the result of a race-condition
	 */
	@Test
	public void testInsertingAndReading() throws LostConnectionException, MaxConnectionsException,
			EntityInstanceNotUniqueException {
		for (int i = 0; i < PAGE_SIZE / 3; i++) {
			saveCopy(true, medium);
		}
		for (int i = 0; i < 2 * PAGE_SIZE / 3; i++) {
			saveCopy(false, medium);
		}
		List<MediumCopyUserDto> readList = readOverdueCopiesForPage(0);
		assertEquals(PAGE_SIZE / 3, readList.size());
	}

	@AfterEach
	public void deleteCopies() throws CopyDoesNotExistException {
		CopyDto copy = new CopyDto();
		copy.setMediumId(medium.getId());
		for (Integer integer : createdCopies){
			copy.setId(integer);
			MediumDao.deleteCopy(copy);
		}
	}

	private void saveCopy(boolean late, MediumDto medium) throws EntityInstanceNotUniqueException {
		CopyDto copy = createCopyDto(late);
		MediumDao.createCopy(copy, medium);
		createdCopies.add(copy.getId());
	}

	private CopyDto createCopyDto(boolean late) {
		CopyDto copy = new CopyDto();
		do {
			copy.setSignature("uniqueness835" + signatureCounter++);
		} while (MediumDao.signatureExists(copy));
		copy.setMediumId(medium.getId());
		copy.setLocation("location");
		int timestampOffset = (late ? SHORT_TIME_SECONDS : LONG_TIME_SECONDS);
		copy.setDeadline(Timestamp.from(Instant.now().plusSeconds(timestampOffset)));
		copy.setCopyStatus(CopyStatus.BORROWED);
		copy.setActor(user.getId());
		return copy;
	}
	
	private static List<MediumCopyUserDto> readOverdueCopiesForPage(int page){
		PaginationDto paginationDetails = new PaginationDto();
		paginationDetails.setPageNumber(page);
		paginationDetails.setTotalAmountOfRows(PAGE_SIZE);
		try {
		    TimeUnit.SECONDS.sleep(SHORT_TIME_SECONDS + EXTRA_DELAY_SECONDS);
		} catch (InterruptedException ie) {
		    Thread.currentThread().interrupt();
		}
		return MediumDao.readAllOverdueCopies(paginationDetails);
	}

	private static void initializeUserDto() {
		user = new UserDto();
		user.setFirstName("Luigi");
		user.setLastName("");
		String salt = PasswordHashingModule.generateSalt();
		String hash = PasswordHashingModule.hashPassword("lStandsForWinner7", salt);
		user.setPasswordSalt(salt);
		user.setPasswordHash(hash);
		user.setEmailAddress("luigiNumberOne@gmail.com");
		user.setUserRole(UserRole.REGISTERED);
		user.setZipCode(12345);
		user.setCity("Toad town");
		user.setStreet("Mushroom Rd.");
		user.setStreetNumber("1");
		user.setLendingPeriod(Duration.ofDays(7));
		user.setUserLendStatus(UserLendStatus.ENABLED);
		user.setUserVerificationStatus(UserVerificationStatus.VERIFIED);
	}

	private static void saveUser() throws LostConnectionException, MaxConnectionsException {
		if (UserDao.userExists(user)) {
			throw new IllegalStateException("Cannot perform test for existing user");
		} else {
			UserDao.createUser(user);
		}
	}

	private static void initializeMediumDto() {
		medium = new MediumDto();
		medium.setReturnPeriod(Duration.of(5, ChronoUnit.MINUTES));
		medium.setTitle("Title");
		medium.setMediumType("Book");
		medium.setEdition("A");
		medium.setPublisher("Publisher");
		medium.setReleaseYear(1999);
		medium.setIsbn("ISBN");
	}
	
}
