package dedede.model.persistence.util;

import java.sql.Connection;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicInteger;

public class ConnectionPool {
	
	
	private Queue<Connection> connections = null;
	
	private final AtomicInteger maxConnections = new AtomicInteger();
	
	
	public Connection fetchConnection() {
		
		return null;
		
	}
	
	public void releseConnection() {
		
	}
	
	public Connection createConnection() {
		
		return null;
	}
	
	
	public void closeConnection() {
		
	}
	
	public void setupConnectionPool() {
		
	}
	
	public void destroyConnectionPool() {
		
	}
	
}
