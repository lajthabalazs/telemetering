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

public class SQLJetDatastore implements SensorInterface {
	
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

	private static final long HOUR_MILLIS = 3600000l;

	
	
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
	public String[] getLocations() {
		return null;
	}
	
	public boolean saveMeasurement(String location, SensorType type, long time, long value) {
		SqlJetDb db = null;
		try {
			db = SqlJetDb.open(dbFile, true);
			db.beginTransaction(SqlJetTransactionMode.WRITE);
			ISqlJetTable table = db.getTable(MEASUREMENT_TABLE);
			table.insert(time, getSensorId(type, location), value);
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
	
	@Override
	public List<Measurement> getMeasurements(String location, SensorType type, long startTime, long endTime) {
		SqlJetDb db = null;
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
			db.close();
			return null;
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

	@Override
	public List<Measurement> getLastHoursMeasurements(String location, SensorType type) {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.MILLISECOND, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MINUTE, 0);
		long endTime = calendar.getTimeInMillis();
		long startTime = endTime - HOUR_MILLIS;
		return getMeasurements(location, type, startTime, endTime);
	}
	
	private long[] getLastHoursLimits() {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.MILLISECOND, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MINUTE, 0);
		long endTime = calendar.getTimeInMillis();
		long startTime = endTime - HOUR_MILLIS;
		return new long[] {startTime, endTime};
	}
	
	private long[] getLastDaysLimits() {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.MILLISECOND, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		long endTime = calendar.getTimeInMillis();
		long startTime = endTime - 24 * HOUR_MILLIS;
		return new long[] {startTime, endTime};
	}

	private long[] getLastWeeksLimits() {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.MILLISECOND, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		int daysPassedInWeek = 0;
		switch (calendar.get(Calendar.DAY_OF_WEEK)) {
		case Calendar.MONDAY: {
			daysPassedInWeek = 0;
			break;
		}
		case Calendar.TUESDAY: {
			daysPassedInWeek = 1;
			break;
		}
		case Calendar.WEDNESDAY: {
			daysPassedInWeek = 2;
			break;
		}
		case Calendar.THURSDAY: {
			daysPassedInWeek = 3;
			break;
		}
		case Calendar.FRIDAY: {
			daysPassedInWeek = 4;
			break;
		}
		case Calendar.SATURDAY: {
			daysPassedInWeek = 5;
			break;
		}
		case Calendar.SUNDAY: {
			daysPassedInWeek = 6;
			break;
		}
		}
		long endTime = calendar.getTimeInMillis() - daysPassedInWeek * 24 * HOUR_MILLIS;
		long startTime = endTime - 7 * 24 * HOUR_MILLIS;
		return new long[] {startTime, endTime};
	}

	private long[] getLastMonthsLimits() {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.MILLISECOND, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		long endTime = calendar.getTimeInMillis();
		calendar.setTimeInMillis(calendar.getTimeInMillis() - 37 * HOUR_MILLIS); // Just to make sure we step back more than a day
		calendar.set(Calendar.MILLISECOND, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		long startTime = calendar.getTimeInMillis();
		return new long[] {startTime, endTime};
	}

	@Override
	public Measurement getLastHoursAverage(String location, SensorType type) {
		long[] limits = getLastHoursLimits();
		List<Measurement> measurements = getMeasurements(location, type, limits[0], limits[1]);
		double total = 0;
		for (Measurement measurement : measurements) {
			total = total + measurement.getValue();
		}
		return new Measurement(location, type, (long)(total / measurements.size()), (limits[0] + limits[1] ) / 2);
	}

	@Override
	public Measurement getLastDaysAverage(String location, SensorType type) {
		long[] limits = getLastDaysLimits();
		List<Measurement> measurements = getMeasurements(location, type, limits[0], limits[1]);
		double total = 0;
		for (Measurement measurement : measurements) {
			total = total + measurement.getValue();
		}
		return new Measurement(location, type, (long)(total / measurements.size()), (limits[0] + limits[1]) / 2);
	}

	@Override
	public Measurement getLastWeeksAverage(String location, SensorType type) {
		long[] limits = getLastWeeksLimits();
		List<Measurement> measurements = getMeasurements(location, type, limits[0], limits[1]);
		double total = 0;
		for (Measurement measurement : measurements) {
			total = total + measurement.getValue();
		}
		return new Measurement(location, type, (long)(total / measurements.size()), (limits[0] + limits[1]) / 2);
	}

	@Override
	public Measurement getLastMonthsAverage(String location, SensorType type) {
		long[] limits = getLastMonthsLimits();
		List<Measurement> measurements = getMeasurements(location, type, limits[0], limits[1]);
		double total = 0;
		for (Measurement measurement : measurements) {
			total = total + measurement.getValue();
		}
		return new Measurement(location, type, (long)(total / measurements.size()), (limits[0] + limits[1]) / 2);
	}

	@Override
	public List<Measurement> getLastDayByHours(String location, SensorType type) {
		long[] limits = getLastDaysLimits();
		return getMeasurementAverages(location, type, limits[0], limits[1], HOUR_MILLIS);
	}

	@Override
	public List<Measurement> getLastWeekByDays(String location, SensorType type) {
		long[] limits = getLastWeeksLimits();
		return getMeasurementAverages(location, type, limits[0], limits[1], 24 * HOUR_MILLIS);
	}

	@Override
	public List<Measurement> getLastMonthByDays(String location, SensorType type) {
		long[] limits = getLastMonthsLimits();
		return getMeasurementAverages(location, type, limits[0], limits[1], 24 * HOUR_MILLIS);
	}

	@Override
	public List<Measurement> getMeasurementAverages(String location, SensorType type,
			long startTime, long endTime, long window) {
		List<Measurement> ret = new LinkedList<Measurement>();
		for (; startTime < endTime; startTime += window) {
			List<Measurement> measurements = getMeasurements(location, type, startTime, endTime);
			double total = 0;
			for (Measurement measurement : measurements) {
				total = total + measurement.getValue();
			}
			ret.add(new Measurement(location, type, (long)(total / measurements.size()), (startTime + endTime) / 2));
		}
		return ret;
	}

	@Override
	public Measurement getLastHoursMaximum(String location, SensorType type) {
		long[] limits = getLastHoursLimits();
		return getMaximum(location, type, limits[0], limits[1]);
	}

	@Override
	public Measurement getLastHoursMinimum(String location, SensorType type) {
		long[] limits = getLastHoursLimits();
		return getMinimum(location, type, limits[0], limits[1]);
	}

	@Override
	public Measurement getLastDaysMaximum(String location, SensorType type) {
		long[] limits = getLastDaysLimits();
		return getMaximum(location, type, limits[0], limits[1]);
	}

	@Override
	public Measurement getLastDaysMinimum(String location, SensorType type) {
		long[] limits = getLastDaysLimits();
		return getMinimum(location, type, limits[0], limits[1]);
	}

	@Override
	public Measurement getLastWeeksMaximum(String location, SensorType type) {
		long[] limits = getLastWeeksLimits();
		return getMaximum(location, type, limits[0], limits[1]);
	}

	@Override
	public Measurement getLastWeeksMinimum(String location, SensorType type) {
		long[] limits = getLastWeeksLimits();
		return getMinimum(location, type, limits[0], limits[1]);
	}

	@Override
	public Measurement getLastMonthsMaximum(String location, SensorType type) {
		long[] limits = getLastMonthsLimits();
		return getMaximum(location, type, limits[0], limits[1]);
	}

	@Override
	public Measurement getLastMonthsMinimum(String location, SensorType type) {
		long[] limits = getLastMonthsLimits();
		return getMinimum(location, type, limits[0], limits[1]);
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
}
