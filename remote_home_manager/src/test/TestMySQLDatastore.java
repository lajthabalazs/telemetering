package test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import hu.droidium.remote_home_manager.database.MySQLDataStore;

import org.junit.After;
import org.junit.Before;

public class TestMySQLDatastore extends TestDatastore {

	@Before
	public void init() {
		dropDatabase();
		try {
			datastore = new MySQLDataStore("jdbc:mysql://localhost:3306", "telemetering" , "root", "cicamica");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	@After
	public void destroy() {
		dropDatabase();
	}	

	private void dropDatabase() {
		try {
		    System.out.println("Loading driver...");
		    Class.forName("com.mysql.jdbc.Driver");
		    System.out.println("Driver loaded!");
		} catch (ClassNotFoundException e) {
		    throw new RuntimeException("Cannot find the driver in the classpath!", e);
		}
		
		
		Connection conn;
		try {
			conn = DriverManager.getConnection("jdbc:mysql://localhost:3306", "root", "cicamica");
			// Create database if it doesn't exist
			Statement stmt = conn.createStatement();
			String sql = "DROP DATABASE IF EXISTS telemetering";
			stmt.executeUpdate(sql);
			stmt.close();
			conn.close();
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
	}
}
