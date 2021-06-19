package test.java.tests;

import de.dedede.model.persistence.util.ConfigReader;
import de.dedede.model.persistence.util.ConnectionPool;

import java.sql.SQLException;
import java.util.Properties;

public final class PreTest {

    private static Properties props;

    public static void setUp() throws SQLException, ClassNotFoundException {
        setUpProperties();
        ConfigReader.getInstance().setUpConfigReader(props);
        ConnectionPool.setUpConnectionPool();
    }

    private static void setUpProperties() {
        props = new Properties();
        props.put("DB_USER", "sep21g01");
        props.put("DB_PASSWORD", "fooZae4cuoSa");
        props.put("DB_DRIVER", "org.postgresql.Driver");
        props.put("DB_SSL", "TRUE");
        props.put("DB_SSL_FACTORY", "org.postgresql.ssl.DefaultJavaSSLFactory");
        props.put("DB_HOST", "bueno.fim.uni-passau.de");
        props.put("DB_PORT", "5432");
        props.put("DB_NAME", "sep21g01t");
        props.put("DB_URL", "jdbc:postgresql://");
        props.put("DB_CAPACITY", "20");
    }

}
