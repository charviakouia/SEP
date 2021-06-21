package tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.SQLException;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Properties;

import de.dedede.model.persistence.exceptions.*;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import de.dedede.model.data.dtos.CategoryDto;
import de.dedede.model.data.dtos.CopyDto;
import de.dedede.model.data.dtos.CopyStatus;
import de.dedede.model.data.dtos.MediumDto;
import de.dedede.model.data.dtos.UserDto;
import de.dedede.model.logic.managed_beans.MediumCreator;
import de.dedede.model.persistence.daos.CategoryDao;
import de.dedede.model.persistence.daos.MediumDao;
import de.dedede.model.persistence.daos.UserDao;
import de.dedede.model.persistence.util.ConfigReader;
import de.dedede.model.persistence.util.ConnectionPool;

public class MediumCreationTest {
	
	private static CopyDto copy;
	private static MediumDto medium;

	@BeforeAll
	public static void setUp() throws ClassNotFoundException, SQLException, MaxConnectionsException, 
			LostConnectionException, UserDoesNotExistException, EntityInstanceNotUniqueException, 
			MediumDoesNotExistException, CopyDoesNotExistException, EntityInstanceDoesNotExistException {
		PreTest.setUp();
		deleteCopyIfExists();
		setUpMedium();
	}
	
	@AfterAll
	public static void tearDown() {
		ConnectionPool.destroyConnectionPool();
	}
	
	private static void setUpMedium() {
		medium = new MediumDto();
		medium.setReturnPeriod(Duration.of(5, ChronoUnit.MINUTES));
		medium.setTitle("Title");
		medium.setMediumType("Book");
		medium.setEdition("A");
		medium.setPublisher("Publisher");
		medium.setReleaseYear(1999);
		medium.setIsbn("ISBN");
	}
	
	private static void deleteCopyIfExists() throws LostConnectionException, MaxConnectionsException,
			CopyDoesNotExistException, EntityInstanceDoesNotExistException {
		copy = new CopyDto();
		copy.setCopyStatus(CopyStatus.AVAILABLE);
		copy.setLocation("Location");
		copy.setSignature("Signature");
		if (MediumDao.copyExists(copy)) {
			MediumDao.readCopyBySignature(copy);
			MediumDao.deleteCopy(copy);
		}
	}
	
	@Test
	public void testCreatingValidMedium() throws LostConnectionException, MaxConnectionsException, 
			MediumDoesNotExistException {
		MediumDao.createMedium(medium);
		MediumDto sndMedium = new MediumDto();
		sndMedium.setId(medium.getId());
		MediumDao.readMedium(sndMedium);
		assertEquals(medium.getTitle(), sndMedium.getTitle());
	}
	
	@Test
	public void testAddingCopiesToMedium() throws LostConnectionException, MaxConnectionsException, 
			MediumDoesNotExistException, EntityInstanceNotUniqueException, CopyDoesNotExistException, EntityInstanceDoesNotExistException {
		deleteCopyIfExists();
		MediumDao.createMedium(medium);
		MediumDao.createCopy(copy, medium);
		MediumDto sndMedium = new MediumDto();
		sndMedium.setId(medium.getId());
		MediumDao.readMedium(sndMedium);
		assertTrue(sndMedium.getCopies().containsKey(copy.getId()));
	}
	
	@Test
	public void testAddingInvalidCopies() throws LostConnectionException, MaxConnectionsException, EntityInstanceNotUniqueException {
		MediumDao.createMedium(medium);
		MediumDao.createCopy(copy, medium);
		assertThrows(EntityInstanceNotUniqueException.class, () -> MediumDao.createCopy(copy, medium));
	}
	
}
