package tests;

import de.dedede.model.data.dtos.CopyDto;
import de.dedede.model.data.dtos.CopyStatus;
import de.dedede.model.data.dtos.MediumDto;
import de.dedede.model.persistence.daos.MediumDao;
import de.dedede.model.persistence.exceptions.*;
import de.dedede.model.persistence.util.ConnectionPool;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Sergei Pravdin
 */

public class MediumDaoTest {

    @BeforeAll
    public static void setUp() throws ClassNotFoundException, SQLException, InvalidConfigurationException {
        ConnectionPool.setUpConnectionPool();
    }

    @AfterAll
    public static void tearDown() throws LostConnectionException, MaxConnectionsException, MediumDoesNotExistException {
        CopyDto copyDto = new CopyDto();
        copyDto.setId(555);
        MediumDao.deleteCopy(copyDto);
        ConnectionPool.destroyConnectionPool();
    }

    @Test
    public void readMediumTest() throws LostConnectionException, MaxConnectionsException, MediumDoesNotExistException {
        MediumDto mediumDto = new MediumDto();
        mediumDto.setId(2);
        Assertions.assertTrue(MediumDao.readMedium(mediumDto).getCopies().containsKey(333));
    }

    @Test
    public void mediumDoesNotExistTest() {
        MediumDto mediumDto = new MediumDto();
        int testId = 1111111;
        mediumDto.setId(testId);

        Exception exception = assertThrows(MediumDoesNotExistException.class, () -> {
            MediumDao.readMedium(mediumDto);
        });
        String expectedMessage = "A medium does not exist with id: " + testId;
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    public void testCreateCopy() throws LostConnectionException, MaxConnectionsException,
            EntityInstanceNotUniqueException, MediumDoesNotExistException {
        MediumDto mediumDto = new MediumDto();
        CopyDto copyDto = new CopyDto();
        mediumDto.setId(2);
        copyDto.setId(555);
        copyDto.setSignature("testSignature2");
        copyDto.setCopyStatus(CopyStatus.BORROWED);
        copyDto.setLocation("testLocation2");
        copyDto.setActor(333);
        MediumDao.createCopy(copyDto, mediumDto);
        Assertions.assertEquals("testSignature2", MediumDao.readMedium(mediumDto).getCopy(555).getSignature());
    }

}
