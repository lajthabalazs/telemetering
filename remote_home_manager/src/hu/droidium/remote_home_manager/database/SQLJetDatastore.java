package hu.droidium.remote_home_manager.database;

import hu.droidium.remote_home_manager.HungarianLanguageModule;
import hu.droidium.telemetering.interfaces.AutoTarget;
import hu.droidium.telemetering.interfaces.DatastoreBase;
import hu.droidium.telemetering.interfaces.Measurement;
import hu.droidium.telemetering.interfaces.SensorType;

import java.io.File;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.tmatesoft.sqljet.core.SqlJetException;
import org.tmatesoft.sqljet.core.SqlJetTransactionMode;
import org.tmatesoft.sqljet.core.table.ISqlJetCursor;
import org.tmatesoft.sqljet.core.table.ISqlJetTable;
import org.tmatesoft.sqljet.core.table.SqlJetDb;

public class SQLJetDatastore extends DatastoreBase {
	
	private static final String MEASUREMENT_TABLE = "measurement";

	private static final String SENSOR_ID = "sensor_id";
	private static final String TIME = "timestamp";
	private static final String VALUE = "value";

	private static final String CREATE_MEASUREMENT = "CREATE TABLE " + MEASUREMENT_TABLE + " " +
			"(" +
			TIME + " INTEGER NOT NULL," +
			SENSOR_ID + " TEXT NOT NULL," +
			VALUE + " INTEGER NOT NULL" +
			")";

	private static final String MEASUREMENT_INDEX = "measurement_index";
	private static final String MEASUREMENT_SENSOR_INDEX = "measurement_sensor_index";
	private static final String MEASUREMENT_TIME_INDEX = "measurement_time_index";
	private static final String CREATE_MEASUREMENT_INDEX = "CREATE INDEX " + MEASUREMENT_INDEX + " ON " + MEASUREMENT_TABLE + "(" + TIME + ", " + SENSOR_ID + ")";
	private static final String CREATE_MEASUREMENT_SENSOR_INDEX = "CREATE INDEX " + MEASUREMENT_SENSOR_INDEX + " ON " + MEASUREMENT_TABLE + "(" + SENSOR_ID + ")";
	private static final String CREATE_MEASUREMENT_TIME_INDEX = "CREATE INDEX " + MEASUREMENT_TIME_INDEX + " ON " + MEASUREMENT_TABLE + "(" + TIME + ")";
	
	private File dbFile;

	public SQLJetDatastore(String fileName) {
		dbFile = new File(fileName);
		if (!dbFile.exists()) {
			// Create database
			SqlJetDb db = null;
			try {
				db = SqlJetDb.open(dbFile, true);
				db.getOptions().setAutovacuum(true);
			} catch (SqlJetException e) {
				e.printStackTrace();
				try {
					db.close();
				} catch (Exception e1) {
					e1.printStackTrace();
					return;
				}
			}
			try {
				db.beginTransaction(SqlJetTransactionMode.WRITE);
				db.createTable(CREATE_MEASUREMENT);
				db.createIndex(CREATE_MEASUREMENT_INDEX);
				db.createIndex(CREATE_MEASUREMENT_SENSOR_INDEX);
				db.createIndex(CREATE_MEASUREMENT_TIME_INDEX);
				db.commit();
				db.close();
				System.out.println("Database created successfully.");
			} catch (SqlJetException e) {
				e.printStackTrace();
				try {
					db.rollback();
					db.close();
				} catch (SqlJetException e1) {
					e1.printStackTrace();
				}
				System.err.println("Deleting inconsistent database file.");
				dbFile.delete();
			}
		}
	}
	
	@Override
	public List<String> getLocations() {
		return null;
	}
	
	public boolean saveMeasurement(String location, SensorType type, long time, long value) {
		SqlJetDb db = null;
		try {
			db = SqlJetDb.open(dbFile, true);
			db.beginTransaction(SqlJetTransactionMode.WRITE);
			ISqlJetTable table = db.getTable(MEASUREMENT_TABLE);
			long result = table.insert(time, getSensorId(type, location), value);
			System.out.println("Result of insert " + result);			
			db.commit();
			return true;
		} catch (SqlJetException e) {
			e.printStackTrace();
			try {
				db.rollback();
			} catch (SqlJetException e1) {
				e1.printStackTrace();
			}
			return false;
		} finally {
			try {
				db.close();
			} catch (SqlJetException e1) {
				e1.printStackTrace();
			}
		}
	}
	
	@Override
	public boolean bulkInster(List<Measurement> measurements) {
		SqlJetDb db = null;
		try {
			db = SqlJetDb.open(dbFile, true);
			db.beginTransaction(SqlJetTransactionMode.WRITE);
			ISqlJetTable table = db.getTable(MEASUREMENT_TABLE);
			int i = 0;
			for (Measurement measurement : measurements) {
				table.insert(measurement.getTime(), getSensorId(measurement.getType(), measurement.getLocation()), measurement.getValue());
				if (i++ % 1000 == 0) {
					System.out.println("Writing " + i + "th item.");
				}
			}
			db.commit();
			return true;
		} catch (SqlJetException e) {
			e.printStackTrace();
			try {
				db.rollback();
			} catch (SqlJetException e1) {
				e1.printStackTrace();
			}
			return false;
		} finally {
			try {
				db.close();
			} catch (SqlJetException e1) {
				e1.printStackTrace();
			}
		}
	}
	
	@Override
	public List<Measurement> getMeasurements(String location, SensorType type, long startTime, long endTime) {
		SqlJetDb db = null;
		System.out.println("Getting measurements " + location + " " + type + " " + startTime +" " + endTime);
		try {
			db = SqlJetDb.open(dbFile, false);
			db.beginTransaction(SqlJetTransactionMode.READ_ONLY);
			ISqlJetTable table = db.getTable(MEASUREMENT_TABLE);
			ISqlJetCursor cursor = table.scope(MEASUREMENT_TIME_INDEX, new Object[] {startTime}, new Object[] {endTime});
			List<Measurement> values = new LinkedList<Measurement>();
			if (cursor.first()) {
				do {
					String recordSensorId = cursor.getString(SENSOR_ID);
					if (recordSensorId.equals(getSensorId(type, location))) {
						Long time = cursor.getInteger(TIME);
						Long value = cursor.getInteger(VALUE);
						values.add(new Measurement(getLocation(recordSensorId), getType(recordSensorId), time, value));
					}
					
				} while(cursor.next());
			}
			db.close();
			return values;
		} catch (SqlJetException e) {
			e.printStackTrace();
		} finally {
			try {
				db.close();
			} catch (SqlJetException e1) {
				e1.printStackTrace();
			}
		}
		return null;
	}

	@Override
	public Measurement getLastMeasurement(String location, SensorType type) {
		SqlJetDb db = null;
		try {
			db = SqlJetDb.open(dbFile, false);
			db.beginTransaction(SqlJetTransactionMode.READ_ONLY);
			ISqlJetTable table = db.getTable(MEASUREMENT_TABLE);
			ISqlJetCursor cursor = table.lookup(MEASUREMENT_SENSOR_INDEX, getSensorId(type, location)).reverse();
			if (cursor.first()) {
				Long time = cursor.getInteger(TIME);
				Long value = cursor.getInteger(VALUE);
				return new Measurement(location, type, time, value);
			}
			return null;
		} catch (SqlJetException e) {
			e.printStackTrace();
		} finally {
			try {
				db.close();
			} catch (SqlJetException e1) {
				e1.printStackTrace();
			}
		}
		return null;
	}

	@Override
	public List<Measurement> getMeasurementAverages(String location, SensorType type,
			long startTime, long endTime, long window) {
		System.out.println("Start " + HungarianLanguageModule.timestampFormat.format(new Date(startTime)));
		System.out.println("End " + HungarianLanguageModule.timestampFormat.format(new Date(endTime)));
		System.out.println("Period " + window / 60000);
		List<Measurement> ret = new LinkedList<Measurement>();
		for (; startTime < endTime; startTime += window) {
			List<Measurement> measurements = getMeasurements(location, type, startTime, endTime);
			if (measurements.size() > 0) {
				double total = 0;
				for (Measurement measurement : measurements) {
					total = total + measurement.getValue();
				}
				long value = (long)(total / measurements.size());
				ret.add(new Measurement(location, type, startTime , value));
			}
		}
		return ret;
	}

	@Override
	public Measurement getMaximum(String location, SensorType type, long startTime, long endTime) {
		List<Measurement> measurements = getMeasurements(location, type, startTime, endTime);
		if (measurements.size() > 0) {
			Measurement max = measurements.get(0);
			for (Measurement measurement : measurements) {
				if (measurement.getValue() > max.getValue()) {
					max = measurement;
				}
			}
			return max;
		} else {
			return null;
		}
	}

	@Override
	public Measurement getMinimum(String location, SensorType type, long startTime, long endTime) {
		List<Measurement> measurements = getMeasurements(location, type, startTime, endTime);
		if (measurements.size() > 0) {
			Measurement min = measurements.get(0);
			for (Measurement measurement : measurements) {
				if (measurement.getValue() < min.getValue()) {
					min = measurement;
				}
			}
			return min;
		} else {
			return null;
		}
	}

	private String getSensorId(SensorType type, String location) {
		return location + "_" + type.getName();
	}

	private String getLocation(String recordSensorId) {
		String[] parts = recordSensorId.split("_");
		return parts[0];
	}

	private SensorType getType(String recordSensorId) {
		String[] parts = recordSensorId.split("_");
		return SensorType.getType(parts[1]);
	}

	@Override
	public List<SensorType> getAvailableSensorTypes(String location) {
		return null;
	}

	@Override
	public boolean setTarget(String location, int targetTemperature, int targetThreshold, boolean auto, long time) {
		return false;
	}

	@Override
	public AutoTarget getTarget(String location) {
		return null;
	}

	@Override
	public boolean heatUntil(String heater, long time, long til) {
		return false;
	}

	@Override
	public long getHeatingEnd(String heater) {
		return 0;
	}

	@Override
	public boolean stopHeating(String heater, long time) {
		return false;
	}

	@Override
	public boolean isSuperUser(String userName) {
		return false;
	}

	@Override
	public boolean addUser(String userName, boolean superUser) {
		return false;
	}

	@Override
	public List<String> getUsers() {
		return null;
	}

	@Override
	public boolean hasUser(String user) {
		return false;
	}

	@Override
	public String removeUser(String string) {
		// TODO Auto-generated method stub
		return null;
	}
}