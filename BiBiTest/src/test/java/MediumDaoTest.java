package test.java;

import de.dedede.model.data.dtos.MediumDto;
import de.dedede.model.persistence.daos.MediumDao;
import de.dedede.model.persistence.exceptions.EntityInstanceDoesNotExistException;
import de.dedede.model.persistence.exceptions.LostConnectionException;
import de.dedede.model.persistence.exceptions.MaxConnectionsException;
import de.dedede.model.persistence.util.ConfigReader;
import de.dedede.model.persistence.util.ConnectionPool;
import de.dedede.model.persistence.util.DataLayerInitializer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.sql.SQLException;

public class MediumDaoTest {

    @Test
    public void readMediumTest() throws LostConnectionException, EntityInstanceDoesNotExistException, MaxConnectionsException, SQLException, ClassNotFoundException {
        ConfigReader configReader = Mockito.mock(ConfigReader.class);
 //       FacesContext facesContext = ContextMocker.mockFacesContext();
        ConnectionPool.setUpConnectionPool();
        DataLayerInitializer.execute();
        MediumDto mediumDto = new MediumDto();
        mediumDto.setId(2);
        MediumDao.readMedium(mediumDto);
        Assertions.assertEquals(1, mediumDto.getCategory().getId());
    }
}
