package test.java.TestsVonSergei;

import de.dedede.model.data.dtos.CopyStatus;
import de.dedede.model.data.dtos.MediumDto;
import de.dedede.model.logic.exceptions.BusinessException;
import de.dedede.model.logic.managed_beans.Medium;
import de.dedede.model.persistence.daos.MediumDao;
import de.dedede.model.persistence.exceptions.*;
import de.dedede.model.persistence.util.DataLayerInitializer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Sergei Pravdin
 */
public class MediumDaoTest {

    @BeforeAll
    static void preTestSetUp() throws DriverNotFoundException, LostConnectionException {
        DataLayerInitializer.execute();
    }

    @AfterAll
    public static void tearDown() {
        DataLayerInitializer.shutdownDataLayer();
    }

    @Test
    public void readMediumTest() throws LostConnectionException, EntityInstanceDoesNotExistException, MaxConnectionsException {
        MediumDto mediumDto = new MediumDto();
        mediumDto.setId(2);
        MediumDao.readMedium(mediumDto);
        Assertions.assertEquals(1, mediumDto.getCategory().getId());
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
    public void testCreateCopy() throws BusinessException, LostConnectionException, EntityInstanceDoesNotExistException, MaxConnectionsException {
        Medium medium = new Medium();
        medium.getMedium().setId(1);
        medium.getCopy().setId(555);
        medium.getCopy().setSignature("testSignature");
        medium.getCopy().setLocation("testPosition");
        medium.getCopy().setCopyStatus(CopyStatus.AVAILABLE);
        medium.createCopy();
        MediumDao.readMedium(medium.getMedium());
        Assertions.assertEquals("testSignature", medium.getMedium().getCopy(555).getSignature());
    }

}
