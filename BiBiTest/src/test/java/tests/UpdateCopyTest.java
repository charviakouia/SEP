package test.java.tests;


import de.dedede.model.data.dtos.MediumDto;
import de.dedede.model.persistence.daos.MediumDao;
import de.dedede.model.persistence.exceptions.CategoryDoesNotExistException;
import de.dedede.model.persistence.exceptions.EntityInstanceDoesNotExistException;
import de.dedede.model.persistence.exceptions.MediumDoesNotExistException;
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
    private static String defaultLocation = "ZB";
    private static String testLocation = "Kneipe";
    private static int testMediumId = 3;
    private static int testCopyId = 2075071941;


    @BeforeAll
    public static void setUp() throws ClassNotFoundException, SQLException, MediumDoesNotExistException {
        PreTest.setUp();
        setMediumDto();
    }

    @AfterAll
    public static void tearDown() throws CategoryDoesNotExistException, EntityInstanceDoesNotExistException {
        ConnectionPool.destroyConnectionPool();
    }

    @Test
    public void updateCopyTest() throws EntityInstanceDoesNotExistException, MediumDoesNotExistException {

    }

    private static void setMediumDto() throws MediumDoesNotExistException {
    }
}
