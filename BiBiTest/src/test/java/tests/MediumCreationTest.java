package tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.SQLException;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Properties;

import de.dedede.model.persistence.exceptions.*;
import org.junit.jupiter.api.*;

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

/**
 * Tests the creation, reading, and deletion of medium and medium-copies using the {@link MediumDao} class.
 * The tests are carried out by trying different combinations of valid media and invalid media entities with
 * single and duplicate medium-copy entities.
 *
 * @author Ivan Charviakou
 */
public class MediumCreationTest {

	private static MediumDto medium;
	private static CopyDto copy;

	private boolean copyIsSet = false;
	private boolean mediumIsSet = false;
	private int signatureCounter = 0;

	@BeforeAll
	public static void setUp() throws ClassNotFoundException, SQLException, MaxConnectionsException, 
			LostConnectionException, UserDoesNotExistException, EntityInstanceNotUniqueException, 
			MediumDoesNotExistException, CopyDoesNotExistException, EntityInstanceDoesNotExistException {
		PreTest.setUp();
		setUpMedium();
		setUpCopy();
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

	private static void setUpCopy(){
		copy = new CopyDto();
		copy.setCopyStatus(CopyStatus.AVAILABLE);
		copy.setLocation("Location");
	}

	@AfterEach
	public void deleteEntities() throws MediumDoesNotExistException, CopyDoesNotExistException {
		if (copyIsSet){
			MediumDao.deleteCopy(copy);
			copyIsSet = false;
		}
		if (mediumIsSet){
			MediumDao.deleteMedium(medium);
			mediumIsSet = false;
		}
	}

	/**
	 * Tests whether a semantically valid medium can be created and retrieved from the datastore.
	 *
	 * @throws LostConnectionException			Is thrown when the used connection stopped working correctly
	 * 											before completing the transaction
	 * @throws MaxConnectionsException			Is thrown when no connection is available to carry out the operation
	 * @throws MediumDoesNotExistException		Is thrown if the queried id isn't present in the datastore,
	 * 											typically the result of a race-condition
	 */
	@Test
	public void testCreatingValidMedium() throws LostConnectionException, MaxConnectionsException, 
			MediumDoesNotExistException {
		saveMedium(medium);
		MediumDto sndMedium = replicateMediumId(medium, new MediumDto());
		MediumDao.readMedium(sndMedium);
		assertEquals(medium.getTitle(), sndMedium.getTitle());
	}

	/**
	 * Tests whether a semantically valid copy can be added to an existing medium in the datastore.
	 *
	 * @throws LostConnectionException				Is thrown when the used connection stopped working correctly
	 * 												before completing the transaction.
	 * @throws MaxConnectionsException				Is thrown when no connection is available to carry out the
	 * 												operation.
	 * @throws MediumDoesNotExistException			Is thrown if the queried id isn't present in the datastore,
	 * 												typically the result of a race-condition
	 * @throws EntityInstanceNotUniqueException		Is thrown if the copy's specified signature is already in use
	 * 												in the datastore
	 */
	@Test
	public void testAddingCopiesToMedium() throws LostConnectionException, MaxConnectionsException,
			MediumDoesNotExistException, EntityInstanceNotUniqueException {
		saveMedium(medium);
		saveCopy(copy, medium);
		MediumDto sndMedium = replicateMediumId(medium, new MediumDto());
		MediumDao.readMedium(sndMedium);
		assertTrue(sndMedium.getCopies().containsKey(copy.getId()));
	}

	/**
	 * Ensures that multiple copies with the same signature cannot simultaneously be present in the datastore.
	 *
	 * @throws LostConnectionException				Is thrown when the used connection stopped working correctly
	 * 												before completing the transaction.
	 * @throws MaxConnectionsException				Is thrown when no connection is available to carry out the
	 * 												operation
	 * @throws EntityInstanceNotUniqueException		Is thrown if the copy's specified signature is already in use
	 * 												in the datastore
	 */
	@Test
	public void testAddingInvalidCopies() throws LostConnectionException, MaxConnectionsException,
			EntityInstanceNotUniqueException {
		saveMedium(medium);
		saveCopy(copy, medium);
		assertThrows(EntityInstanceNotUniqueException.class, () -> MediumDao.createCopy(copy, medium));
	}

	private void saveCopy(CopyDto copy, MediumDto medium) throws EntityInstanceNotUniqueException {
		do {
			copy.setSignature("albatross88" + signatureCounter++);
		} while (MediumDao.signatureExists(copy));
		MediumDao.createCopy(copy, medium);
		copyIsSet = true;
	}

	private void saveMedium(MediumDto medium){
		MediumDao.createMedium(medium);
		mediumIsSet = true;
	}

	private MediumDto replicateMediumId(MediumDto source, MediumDto destination){
		destination.setId(source.getId());
		return destination;
	}
	
}
