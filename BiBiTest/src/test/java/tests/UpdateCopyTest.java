package test.java.tests;


import de.dedede.model.data.dtos.CopyDto;
import de.dedede.model.data.dtos.MediumDto;
import de.dedede.model.persistence.daos.MediumDao;
import de.dedede.model.persistence.exceptions.*;
import de.dedede.model.persistence.util.ConnectionPool;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

/**
 * @author Sergei Pravdin
 */
public class UpdateCopyTest {

    private static MediumDto mediumDto = new MediumDto();
    private static String testLocation = "Kneipe";
    private static int testMediumId = 3;
    private static int testCopyId = 2075071941;


    @BeforeAll
    public static void setUp() throws ClassNotFoundException, SQLException, MediumDoesNotExistException {
        tests.PreTest.setUp();
        setMediumDto();
    }

    @AfterAll
    public static void tearDown() throws CategoryDoesNotExistException, EntityInstanceDoesNotExistException {
        ConnectionPool.destroyConnectionPool();
    }

    @Test
    public void updateCopyTest() throws MediumDoesNotExistException, CopyDoesNotExistException,
            CopyIsNotAvailableException {
        CopyDto copyDto = mediumDto.getCopy(testCopyId);
        copyDto.setLocation(testLocation);
        MediumDao.updateCopy(copyDto);
        MediumDao.readMedium(mediumDto);
        CopyDto testCopyDto = mediumDto.getCopy(testCopyId);
        Assertions.assertEquals(testCopyDto.getLocation(), testLocation);
    }

    private static void setMediumDto() throws MediumDoesNotExistException {
        mediumDto.setId(testMediumId);
        MediumDao.readMedium(mediumDto);
    }
}
