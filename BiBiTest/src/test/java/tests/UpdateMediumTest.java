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
public class UpdateMediumTest {

    private static MediumDto mediumDto = new MediumDto();
    private static String defaultEdition = "1st";
    private static String testEdition = "21st";
    private static int testId = 3;


    @BeforeAll
    public static void setUp() throws ClassNotFoundException, SQLException, MediumDoesNotExistException {
        tests.PreTest.setUp();
        setMediumDto();
    }

    @AfterAll
    public static void tearDown() throws CategoryDoesNotExistException, EntityInstanceDoesNotExistException {
        mediumDto.setEdition(defaultEdition);
        MediumDao.updateMedium(mediumDto);
        ConnectionPool.destroyConnectionPool();
    }

    @Test
    public void updateMediumTest() throws EntityInstanceDoesNotExistException, MediumDoesNotExistException {
        mediumDto.setEdition(testEdition);
        MediumDao.updateMedium(mediumDto);
        MediumDto mediumDtoTest = new MediumDto();
        mediumDtoTest.setId(testId);
        MediumDao.readMedium(mediumDtoTest);
        Assertions.assertEquals(mediumDtoTest.getEdition(), testEdition);
    }

    @Test
    public void readCopyTest() {
        CopyDto copyDto = mediumDto.getCopy(616861318);
        Assertions.assertEquals("ZB", copyDto.getLocation());
    }

    private static void setMediumDto() throws MediumDoesNotExistException {
        mediumDto.setId(testId);
        MediumDao.readMedium(mediumDto);
    }
}
