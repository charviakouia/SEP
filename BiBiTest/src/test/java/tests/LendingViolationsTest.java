package tests;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.Date;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import de.dedede.model.persistence.exceptions.InvalidConfigurationException;
import org.junit.jupiter.api.AfterAll;
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
import de.dedede.model.persistence.exceptions.EntityInstanceDoesNotExistException;
import de.dedede.model.persistence.exceptions.EntityInstanceNotUniqueException;
import de.dedede.model.persistence.exceptions.LostConnectionException;
import de.dedede.model.persistence.exceptions.MaxConnectionsException;
import de.dedede.model.persistence.exceptions.MediumDoesNotExistException;
import de.dedede.model.persistence.exceptions.UserDoesNotExistException;
import de.dedede.model.persistence.util.ConnectionPool;

class LendingViolationsTest {
	
	private static final int PAGE_SIZE = 10;
	private static final int LATENESS_LENGTH = 10;
	private static UserDto user;
	private static MediumDto medium;
	private static int singatureCounter = 0;
	
	@BeforeAll
	public static void setUp() throws ClassNotFoundException, SQLException, MaxConnectionsException,
			LostConnectionException, InvalidConfigurationException, UserDoesNotExistException {
		PreTest.setUp();
		initializeUserDto();
		saveUserIfExists();
		initializeMediumDto();
	}
	
	private static void initializeUserDto() {
		user = new UserDto();
		user.setFirstName("Luigi");
		user.setLastName("");
		String salt = PasswordHashingModule.generateSalt();
		String hash = PasswordHashingModule.hashPassword("lStandsForWinner7", salt);
		user.setPasswordSalt(salt);
		user.setPasswordHash(hash);
		user.setEmailAddress("ivancharviakou@gmail.com");
		user.setUserRole(UserRole.REGISTERED);
		user.setZipCode(12345);
		user.setCity("Toad town");
		user.setStreet("Mushroom Rd.");
		user.setStreetNumber("1");
		user.setLendingPeriod(Duration.ofDays(7));
		user.setUserLendStatus(UserLendStatus.ENABLED);
		user.setUserVerificationStatus(UserVerificationStatus.VERIFIED);
	}
	
	private static void saveUserIfExists() throws LostConnectionException, MaxConnectionsException, UserDoesNotExistException {
		if (UserDao.userExists(user)) {
			UserDao.readUserByEmail(user);
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
	
	private static CopyDto createCopyDto(boolean late, int seconds) {
		CopyDto copy = new CopyDto();
		copy.setMediumId(medium.getId());
		copy.setLocation("location");
		copy.setSignature(String.valueOf("uniqueness85335" + singatureCounter++));
		if (late) {
			copy.setDeadline(Timestamp.from(Instant.now().plusSeconds(seconds)));
		} else {
			copy.setDeadline(Timestamp.from(Instant.now().plusSeconds(5 * 60 * 60)));
		}
		copy.setCopyStatus(CopyStatus.BORROWED);
		copy.setActor(user.getId());
		return copy;
	}
	
	@AfterAll
	public static void tearDown() throws LostConnectionException, MaxConnectionsException, EntityInstanceDoesNotExistException {
		ConnectionPool.destroyConnectionPool();
	}

	@Test
	public void testPagination() throws LostConnectionException, MaxConnectionsException, EntityInstanceNotUniqueException, 
			MediumDoesNotExistException, EntityInstanceDoesNotExistException {
		MediumDao.createMedium(medium);
		MediumDao.returnOverdueCopies();
		for (int i = 0; i < PAGE_SIZE + 2; i++) {
			createCopyIfExists(true);
		}
		List<MediumCopyUserDto> readList = readOverdueCopiesForPage(1);
		assertEquals(2, readList.size());
	}
	
	@Test
	public void testInsertingAndReading() throws LostConnectionException, MaxConnectionsException, EntityInstanceNotUniqueException, 
			MediumDoesNotExistException, EntityInstanceDoesNotExistException {
		MediumDao.createMedium(medium);
		MediumDao.returnOverdueCopies();
		for (int i = 0; i < PAGE_SIZE / 3; i++) {
			createCopyIfExists(true);
		}
		for (int i = 0; i < 2 * PAGE_SIZE / 3; i++) {
			createCopyIfExists(false);
		}
		List<MediumCopyUserDto> readList = readOverdueCopiesForPage(0);
		assertEquals(PAGE_SIZE / 3, readList.size());
	}
	
	private static void createCopyIfExists(boolean late) throws LostConnectionException, MaxConnectionsException, 
			MediumDoesNotExistException, EntityInstanceNotUniqueException, EntityInstanceDoesNotExistException {
		CopyDto copy = createCopyDto(late, LATENESS_LENGTH);
		if (MediumDao.copyExists(copy)) {
			MediumDao.deleteCopyBySignature(copy);
		}
		MediumDao.createCopy(copy, medium);
	}
	
	private static List<MediumCopyUserDto> readOverdueCopiesForPage(int page){
		PaginationDto paginationDetails = new PaginationDto();
		paginationDetails.setPageNumber(page);
		paginationDetails.setTotalAmountOfRows(PAGE_SIZE);
		try {
		    TimeUnit.SECONDS.sleep(LATENESS_LENGTH + 2);
		} catch (InterruptedException ie) {
		    Thread.currentThread().interrupt();
		}
		return MediumDao.readAllOverdueCopies(paginationDetails);
	}
	
}
