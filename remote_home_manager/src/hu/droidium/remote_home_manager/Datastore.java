package hu.droidium.remote_home_manager;

import java.io.File;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

import org.tmatesoft.sqljet.core.SqlJetException;
import org.tmatesoft.sqljet.core.SqlJetTransactionMode;
import org.tmatesoft.sqljet.core.table.ISqlJetCursor;
import org.tmatesoft.sqljet.core.table.ISqlJetTable;
import org.tmatesoft.sqljet.core.table.SqlJetDb;

public class Datastore {
	
	private static final String MEASUREMENT_TABLE = "measurement";

	private static final String MEASUREMENT_SENSOR_ID = "sensor_id";

	private static final String MEASUREMENT_TIME = "measurement_time";

	private static final String MEASUREMENT_VALUE = "value";

	private static final String CREATE_MEASUREMENT = "CREATE TABLE " + MEASUREMENT_TABLE + " " +
			"(" +
			MEASUREMENT_TIME + " INTEGER NOT NULL," +
			MEASUREMENT_SENSOR_ID + " TEXT NOT NULL," +
			MEASUREMENT_VALUE + " INTEGER NOT NULL" +
			")";

	private static final String MEASUREMENT_INDEX = "measurement_index";
	private static final String MEASUREMENT_TIME_INDEX = "measurement_time_index";
	private static final String CREATE_MEASUREMENT_INDEX = "CREATE INDEX " + MEASUREMENT_INDEX + " ON " + MEASUREMENT_TABLE + "(" + MEASUREMENT_TIME + ", " + MEASUREMENT_SENSOR_ID + ")";
	private static final String CREATE_MEASUREMENT_TIME_INDEX = "CREATE INDEX " + MEASUREMENT_TIME_INDEX + " ON " + MEASUREMENT_TABLE + "(" + MEASUREMENT_TIME + ")";

	
	
	private File dbFile;

	public Datastore(String fileName) {
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
					// TODO Auto-generated catch block
					e1.printStackTrace();
					return;
				}
			}
			try {
				db.beginTransaction(SqlJetTransactionMode.WRITE);
				db.createTable(CREATE_MEASUREMENT);
				db.createIndex(CREATE_MEASUREMENT_INDEX);
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
	
	public boolean saveMeasuremet(String sensorId, long value) {
		SqlJetDb db = null;
		try {
			db = SqlJetDb.open(dbFile, true);
			db.beginTransaction(SqlJetTransactionMode.WRITE);
			ISqlJetTable table = db.getTable(MEASUREMENT_TABLE);
			Calendar calendar = Calendar.getInstance();
			table.insert(calendar.getTimeInMillis(), sensorId, value);
			db.commit();
			db.close();
			return true;
		} catch (SqlJetException e) {
			e.printStackTrace();
			try {
				db.rollback();
			} catch (SqlJetException e1) {
				e1.printStackTrace();
			}
			try {
				db.close();
			} catch (SqlJetException e1) {
				e1.printStackTrace();
			}
			return false;
		}
	}
	
	public List<Measurement> getMeasurements(String sensorId, long startTime, long endTime) {
		SqlJetDb db = null;
		try {
			db = SqlJetDb.open(dbFile, false);
			db.beginTransaction(SqlJetTransactionMode.READ_ONLY);
			ISqlJetTable table = db.getTable(MEASUREMENT_TABLE);
			ISqlJetCursor cursor = table.scope(MEASUREMENT_TIME_INDEX, new Object[] {startTime}, new Object[] {endTime});
			List<Measurement> values = new LinkedList<Measurement>();
			if (cursor.first()) {
				do {
					String recordSensorId = cursor.getString(MEASUREMENT_SENSOR_ID);
					if (recordSensorId.equals(sensorId)) {
						Long time = cursor.getInteger(MEASUREMENT_TIME);
						Long value = cursor.getInteger(MEASUREMENT_VALUE);
						values.add(new Measurement(recordSensorId, time, value));
					}
					
				} while(cursor.next());
			}
			db.commit();
			db.close();
			return values;
		} catch (SqlJetException e) {
			e.printStackTrace();
			try {
				db.rollback();
			} catch (SqlJetException e1) {
				e1.printStackTrace();
			}
			try {
				db.close();
			} catch (SqlJetException e1) {
				e1.printStackTrace();
			}
		}
		return null;
	}
}
