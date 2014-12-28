package hu.droidium.remote_home_manager;

import hu.droidium.telemetering.interfaces.Measurement;
import hu.droidium.telemetering.interfaces.SensorType;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public class MySQLDataStore extends DatastoreBase {
	
	private static final String MEASUREMENT_TABLE = "measurement";

	private static final String SENSOR_ID = "sensor_id";
	private static final String TIME = "timestamp";
	private static final String VALUE = "value";

	private static final String CREATE_MEASUREMENT = "CREATE TABLE IF NOT EXISTS " + MEASUREMENT_TABLE + " " +
			"(" +
			TIME + " INTEGER NOT NULL," +
			SENSOR_ID + " TEXT NOT NULL," +
			VALUE + " INTEGER NOT NULL" +
			")";

	
	private Connection conn;

	public MySQLDataStore(String serverURL, String databaseName, String user, String password) throws SQLException {
		
		try {
		    System.out.println("Loading driver...");
		    Class.forName("com.mysql.jdbc.Driver");
		    System.out.println("Driver loaded!");
		} catch (ClassNotFoundException e) {
		    throw new RuntimeException("Cannot find the driver in the classpath!", e);
		}
		
		
		conn = DriverManager.getConnection(serverURL, user, password);
		// Create database if it doesn't exist
		Statement stmt = conn.createStatement();
		String sql = "CREATE DATABASE If NOT EXISTS " + databaseName;
		stmt.executeUpdate(sql);
		// Create tables
		conn.setCatalog(databaseName);
		stmt.close();
		stmt = conn.createStatement();
		stmt.executeUpdate(CREATE_MEASUREMENT);
	}

	@Override
	public String[] getLocations() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean saveMeasurement(String location, SensorType type, long time, long value) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean bulkInster(List<Measurement> measurements) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Measurement getLastMeasurement(String location, SensorType type) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Measurement> getLastHoursMeasurements(String location, SensorType type) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Measurement> getMeasurements(String location, SensorType type, long startTime, long endTime) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Measurement getLastHoursAverage(String location, SensorType type) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Measurement getLastDaysAverage(String location, SensorType type) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Measurement getLastWeeksAverage(String location, SensorType type) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Measurement getLastMonthsAverage(String location, SensorType type) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Measurement> getLastDayByHours(String location, SensorType type) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Measurement> getLastWeekByDays(String location, SensorType type) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Measurement> getLastMonthByDays(String location, SensorType type) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Measurement> getMeasurementAverages(String location, SensorType type, long startTime, long endTime,
			long window) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Measurement getLastHoursMaximum(String location, SensorType type) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Measurement getLastHoursMinimum(String location, SensorType type) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Measurement getLastDaysMaximum(String location, SensorType type) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Measurement getLastDaysMinimum(String location, SensorType type) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Measurement getLastWeeksMaximum(String location, SensorType type) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Measurement getLastWeeksMinimum(String location, SensorType type) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Measurement getLastMonthsMaximum(String location, SensorType type) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Measurement getLastMonthsMinimum(String location, SensorType type) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Measurement getMaximum(String location, SensorType type, long startTime, long endTime) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Measurement getMinimum(String location, SensorType type, long startTime, long endTime) {
		// TODO Auto-generated method stub
		return null;
	}

	public static void main(String[] args) {
		try {
			new MySQLDataStore("jdbc:mysql://localhost:3306", "telemetering" , "root", "cicamica");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}