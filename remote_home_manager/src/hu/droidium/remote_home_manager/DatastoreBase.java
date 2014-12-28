package hu.droidium.remote_home_manager;

import hu.droidium.telemetering.interfaces.Measurement;
import hu.droidium.telemetering.interfaces.DatastoreInterface;
import hu.droidium.telemetering.interfaces.SensorType;

import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

public abstract class DatastoreBase implements DatastoreInterface {
	
	private static final long HOUR_MILLIS = 3600000l;
	
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
	
	public static long[] getLastHoursLimits() {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.MILLISECOND, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MINUTE, 0);
		long endTime = calendar.getTimeInMillis();
		long startTime = endTime - HOUR_MILLIS;
		return new long[] {startTime, endTime};
	}
	
	public static long[] getLastDaysLimits() {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.MILLISECOND, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		long endTime = calendar.getTimeInMillis();
		long startTime = endTime - 24 * HOUR_MILLIS;
		return new long[] {startTime, endTime};
	}

	public static long[] getLastWeeksLimits() {
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

	public static long[] getLastMonthsLimits() {
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
		return getMeasurementAverage(location, type, limits[0], limits[1]);
	}

	@Override
	public Measurement getLastDaysAverage(String location, SensorType type) {
		long[] limits = getLastDaysLimits();
		return getMeasurementAverage(location, type, limits[0], limits[1]);
	}

	@Override
	public Measurement getLastWeeksAverage(String location, SensorType type) {
		long[] limits = getLastWeeksLimits();
		return getMeasurementAverage(location, type, limits[0], limits[1]);
	}

	@Override
	public Measurement getLastMonthsAverage(String location, SensorType type) {
		long[] limits = getLastMonthsLimits();
		return getMeasurementAverage(location, type, limits[0], limits[1]);
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
			ret.add(getMeasurementAverage(location, type, startTime, startTime + window));
		}
		return ret;
	}


	@Override
	public Measurement getMeasurementAverage(String location, SensorType type, long startTime, long endTime) {
		List<Measurement> measurements = getMeasurements(location, type, startTime, endTime);
		if (measurements.size() > 0) {
			long total = 0;
			for (Measurement measurement : measurements) {
				total = total + measurement.getValue();
			}
			return new Measurement(location, type, startTime, total / measurements.size());
		} else {
			return null;
		}
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
}