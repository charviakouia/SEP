package dedede.model.persistence.util;

import java.sql.Connection;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicInteger;

public class ConnectionPool {
	
	private static volatile ConnectionPool INSTANCE = null;
	
	private Queue<Connection> connections = null;
	
	private final AtomicInteger maxConnections = new AtomicInteger();
	
	private ConnectionPool() {
		if (INSTANCE != null) {
			throw new IllegalStateException();
		}
	}
	
	// https://fullstackdeveloper.guru/2020/04/06/how-to-implement-singleton-pattern-in-java/
	public static ConnectionPool getInstance() {
		if (INSTANCE == null) {
			synchronized (ConnectionPool.class) {
				if (INSTANCE == null) {					
					INSTANCE = new ConnectionPool();
				}
			}
		}
		
		return INSTANCE;
	}
	
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
