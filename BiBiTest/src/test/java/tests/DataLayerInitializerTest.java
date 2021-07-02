package tests;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;

import test.java.tests.PreTest;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import de.dedede.model.persistence.util.ConnectionPool;

public class DataLayerInitializerTest {
	
	private static final String[] tableNames = {"Users", "Application", "Medium"
			, "MediumCopy", "Category"}; 

	/**
	 * Initializes the PreTest-dummies.
	 * 
	 * @throws ClassNotFoundException @see PreTest.setUp().
	 * @throws SQLException if a db communication error occured.
	 */
	@BeforeAll
	public static void setUp() throws ClassNotFoundException, SQLException {
		PreTest.setUp();
	}
	
	/**
	 * Destroys the ConnectionPool.
	 */
	@AfterAll
	public static void tearDown() {
		ConnectionPool.destroyConnectionPool();
	}
	
	/**
	 * Checks if all entries are present in the datastore given by @see PreTest.
	 * Implicitly requires the system to have been started once before.
	 * 
	 * @throws SQLException if db communication error occured.
	 */
	@Test
	public void tablesExistTest () throws SQLException {
		ConnectionPool instance = ConnectionPool.getInstance();
		Connection conn = instance.fetchConnection(5000);
		try {
			DatabaseMetaData metadata = conn.getMetaData();
			ResultSet existingTables = metadata.getTables(null, null, null,
				new String[] {"TABLE"});
			int numberOfTables = 0;
			while(existingTables.next()) {
				for (String str : tableNames) {
					String tableName = existingTables.getString("TABLE_NAME");
					if (tableName.equalsIgnoreCase(str)) {
						numberOfTables++;
						break;
					};
				}
			}
			Assertions.assertEquals(tableNames.length, numberOfTables);
		} finally {
			ConnectionPool.getInstance().releaseConnection(conn);
		}
	}

}
