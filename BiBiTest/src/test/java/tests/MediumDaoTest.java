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
        PreTest.setUp();
    }

    @AfterAll
    public static void tearDown() throws LostConnectionException, MaxConnectionsException, MediumDoesNotExistException, CopyDoesNotExistException {
        CopyDto copyDto = new CopyDto();
        copyDto.setId(555);
        MediumDao.deleteCopy(copyDto);
        ConnectionPool.destroyConnectionPool();
    }

    @Test
    public void readMediumTest() throws LostConnectionException, MaxConnectionsException, MediumDoesNotExistException {
        MediumDto mediumDto = new MediumDto();
        mediumDto.setId(203);
        Assertions.assertTrue(MediumDao.readMedium(mediumDto).getCopies().containsKey(629));
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
}
