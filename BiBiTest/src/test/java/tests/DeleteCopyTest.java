package tests;

import de.dedede.model.data.dtos.CopyDto;
import de.dedede.model.data.dtos.CopyStatus;
import de.dedede.model.data.dtos.MediumDto;
import de.dedede.model.persistence.daos.MediumDao;
import de.dedede.model.persistence.exceptions.CategoryDoesNotExistException;
import de.dedede.model.persistence.exceptions.CopyDoesNotExistException;
import de.dedede.model.persistence.exceptions.EntityInstanceNotUniqueException;
import de.dedede.model.persistence.exceptions.MediumDoesNotExistException;
import de.dedede.model.persistence.util.ConnectionPool;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import test.java.tests.PreTest;

import java.sql.SQLException;

/**
 * @author Sergei Pravdin
 */
public class DeleteCopyTest {

    private static final CopyDto copyDto = new CopyDto();
    private static MediumDto mediumDto = new MediumDto();
    private static final String copyLocation = "testLocation";
    private static final String copySignature = "testSign";
    private static final int mediumId = 203;
    private static final int copyId = 999;

    @BeforeAll
    public static void setUp() throws ClassNotFoundException, SQLException,
            MediumDoesNotExistException, EntityInstanceNotUniqueException {
        PreTest.setUp();
        setUpMediumDto();
        setUpCopyDto();
        MediumDao.createCopy(copyDto, mediumDto);
    }

    @AfterAll
    public static void tearDown() throws CategoryDoesNotExistException {
        ConnectionPool.destroyConnectionPool();
    }

    private static void setUpMediumDto() throws MediumDoesNotExistException {
        mediumDto.setId(mediumId);
        mediumDto = MediumDao.readMedium(mediumDto);
    }

    private static void setUpCopyDto() {
        copyDto.setId(copyId);
        copyDto.setCopyStatus(CopyStatus.AVAILABLE);
        copyDto.setSignature(copySignature);
        copyDto.setLocation(copyLocation);
        copyDto.setMediumId(mediumId);
    }

    @Test
    public void deleteCopyTest() throws CopyDoesNotExistException {
        MediumDao.deleteCopy(copyDto);
        Assertions.assertFalse(MediumDao.copyExists(copyDto));
    }
}
