package hu.droidium.remote_home_manager.database;

import hu.droidium.telemetering.interfaces.AutoTarget;
import hu.droidium.telemetering.interfaces.DatastoreBase;
import hu.droidium.telemetering.interfaces.Measurement;
import hu.droidium.telemetering.interfaces.SensorType;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class MySQLDataStore extends DatastoreBase {
	
	private static final String MEASUREMENT_TABLE = "measurement";

	private static final String TIME = "timestamp";
	private static final String TYPE = "type";
	private static final String VALUE = "value";
	private static final String LOCATION = "location";

	private static final String THRESHGOLDS_TABLE = "thresholds";
	
	private static final String HEATER_PROGRAM_TABLE = "heater_program";

	private static final String HEATER = "heater";

	private static final String TARGET_TEMP = "target_temp";
	private static final String TARGET_THRESHOLD = "target_threshold";
	private static final String AUTO_MODE_ENABLED = "auto_mode_enabled";
	private static final String MANUAL_HEATING_TIL = "manual_heating_til";


	private static final String CREATE_MEASUREMENT = "CREATE TABLE IF NOT EXISTS " + MEASUREMENT_TABLE + " " +
			"(" +
			TIME + " BIGINT NOT NULL," +
			LOCATION + " TEXT NOT NULL," + 
			TYPE + " TEXT NOT NULL," + 
			VALUE + " BIGINT NOT NULL" +
			")";

	private static final String CREATE_HEATER_PROGRAM = "CREATE TABLE IF NOT EXISTS " + HEATER_PROGRAM_TABLE + " " +
			"(" +
			TIME + " BIGINT NOT NULL," +
			HEATER + " TEXT NOT NULL," + 
			MANUAL_HEATING_TIL + " BIGINT NOT NULL" +
			")";

	private static final String CREATE_THRESHOLDS = "CREATE TABLE IF NOT EXISTS " + THRESHGOLDS_TABLE + " " +
			"(" +
			TIME + " BIGINT NOT NULL," +
			LOCATION + " TEXT NOT NULL," + 
			TARGET_TEMP + " INT NOT NULL" +
			TARGET_THRESHOLD + " INT NOT NULL" +
			AUTO_MODE_ENABLED + " BOOL NOT NULL" +
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
		stmt.close();
		conn.setCatalog(databaseName);
		stmt = conn.createStatement();
		stmt.executeUpdate(CREATE_MEASUREMENT);
		stmt.executeUpdate(CREATE_THRESHOLDS);
		stmt.executeUpdate(CREATE_HEATER_PROGRAM);
		stmt.close();
	}

	@Override
	public List<String> getLocations() {
		try {
			Statement stmt = conn.createStatement();
			String sql = "SELECT DISTINCT location from " + MEASUREMENT_TABLE + ";";
			ResultSet result = stmt.executeQuery(sql);
			List<String> ret = new ArrayList<String>();
			for (result.beforeFirst(); result.next();) {
				ret.add(result.getString(LOCATION));
			}
			result.close();
			stmt.close();
			return ret;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public boolean saveMeasurement(String location, SensorType type, long time, long value) {
		String sql = "INSERT INTO " + MEASUREMENT_TABLE +
				"(" +
				TIME + ", " +
				LOCATION + ", " +
				TYPE + ", " +
				VALUE +
				") VALUES (" +
				time +"," + 
				"'" + location + "'," +
				"'" + type.getName() + "'," +
				value + 
				");";
		try {
			Statement stmt = conn.createStatement();
			int affectedRows = stmt.executeUpdate(sql);
			stmt.close();
			return affectedRows > 0;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public boolean bulkInster(List<Measurement> measurements) {
		try {
			conn.setAutoCommit(false);
			for (Measurement m : measurements) {
				if (!saveMeasurement(m.getLocation(), m.getType(), m.getTime(), m.getValue())) {
					throw new SQLException("Couldn't insert row.");
				}
			}
			conn.commit();
			conn.setAutoCommit(true);
		} catch (SQLException e) {
			e.printStackTrace();
			try {
				conn.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}
		return false;
	}

	@Override
	public Measurement getLastMeasurement(String location, SensorType type) {
		try {
			Statement stmt = conn.createStatement();
			String sql = "SELECT * FROM " + MEASUREMENT_TABLE + " WHERE " 
					+ LOCATION + " = '" + location + "' AND " + TYPE + " = '"+ type +"' "
					+ " ORDER BY " + TIME + " DESC LIMIT 1;";
			ResultSet result = stmt.executeQuery(sql);
			Measurement ret = null;
			if (result.first()) {
				ret = new Measurement(location, type, result.getLong(TIME), result.getLong(VALUE));
			}
			result.close();
			stmt.close();
			return ret;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public List<Measurement> getMeasurements(String location, SensorType type, long startTime, long endTime) {
		try {
			Statement stmt = conn.createStatement();
			String sql = "SELECT * FROM " + MEASUREMENT_TABLE + " WHERE " 
					+ LOCATION + " = '" + location + "' AND " + TYPE + " = '"+ type +"' AND "
					+ TIME + " > " + startTime + " AND "
					+ TIME + " < " + endTime
					+ " ORDER BY " + TIME + ";";
			ResultSet result = stmt.executeQuery(sql);
			List<Measurement> ret = new LinkedList<Measurement>();
			for (result.beforeFirst(); result.next();) {
				Measurement measurement = new Measurement(location, type, result.getLong(TIME), result.getLong(VALUE));
				ret.add(measurement);
			}
			result.close();
			stmt.close();
			return ret;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@Override
	public Measurement getMeasurementAverage(String location, SensorType type, long startTime, long endTime) {
		try {
			Statement stmt = conn.createStatement();
			String averageValue = "average_value";
			String sql = "SELECT AVG(" + VALUE + ") AS " + averageValue + " FROM " + MEASUREMENT_TABLE + " WHERE " 
					+ LOCATION + " = '" + location + "' AND " + TYPE + " = '"+ type +"' AND "
					+ TIME + " > " + startTime + " AND "
					+ TIME + " < " + endTime + ";";
			ResultSet result = stmt.executeQuery(sql);
			Measurement ret = null;
			if (result.first()) {
				ret = new Measurement(location, type, startTime, result.getLong(averageValue));
			}
			result.close();
			stmt.close();
			return ret;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static void main(String[] args) {
		try {
			new MySQLDataStore("jdbc:mysql://localhost:3306", "telemetering" , "root", "cicamica");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public List<SensorType> getAvailableSensorTypes(String location) {
		// For now, only sensor type available is temperature
		LinkedList<SensorType> ret = new LinkedList<SensorType>();
		ret.add(SensorType.TEMPERATURE);
		return ret;
	}

	@Override
	public long getHeatingEnd(String heater) {
		try {
			Statement stmt = conn.createStatement();
			String sql = "SELECT * FROM " + HEATER_PROGRAM_TABLE + " WHERE " 
					+ HEATER + " = '" + heater
					+ " ORDER BY " + TIME + " DESC LIMIT 1;";
			ResultSet result = stmt.executeQuery(sql);
			long ret = 0;
			if (result.first()) {
				ret = result.getLong(MANUAL_HEATING_TIL);
			}
			result.close();
			stmt.close();
			return ret;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}

	@Override
	public boolean heatUntil(String heater, long time, long til) {
		String sql = "INSERT INTO " + HEATER_PROGRAM_TABLE +
				"(" +
				TIME + ", " +
				HEATER + ", " +
				MANUAL_HEATING_TIL +
				") VALUES (" +
				time +"," + 
				"'" + heater + "'," +
				til + 
				");";
		try {
			Statement stmt = conn.createStatement();
			int affectedRows = stmt.executeUpdate(sql);
			stmt.close();
			return affectedRows > 0;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public boolean setTarget(String location, int temp, int threshold, boolean auto, long time) {
		String sql = "INSERT INTO " + THRESHGOLDS_TABLE +
				"(" +
				TIME + ", " +
				LOCATION + ", " +
				TARGET_TEMP + ", " +
				TARGET_THRESHOLD +
				") VALUES (" +
				time +"," + 
				"'" + location + "'," +
				temp + "," +
				threshold +  "," +
				auto + 
				");";
		try {
			Statement stmt = conn.createStatement();
			int affectedRows = stmt.executeUpdate(sql);
			stmt.close();
			return affectedRows > 0;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public boolean stopHeating(String heater, long time) {
		return heatUntil(heater, time, 0);
	}

	@Override
	public AutoTarget getTarget(String location) {
		try {
			Statement stmt = conn.createStatement();
			String sql = "SELECT * FROM " + THRESHGOLDS_TABLE
					+ " ORDER BY " + TIME + " DESC LIMIT 1;";
			ResultSet result = stmt.executeQuery(sql);
			int target = 0;
			int threshold = 0;
			long time = 0;
			boolean auto = false;
			if (result.first()) {
				target = result.getInt(TARGET_TEMP);
				threshold = result.getInt(TARGET_THRESHOLD);
				time = result.getLong(TIME);
				auto = result.getBoolean(AUTO_MODE_ENABLED);
			}
			result.close();
			stmt.close();
			return new AutoTarget(location, time, target, threshold, auto);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
}