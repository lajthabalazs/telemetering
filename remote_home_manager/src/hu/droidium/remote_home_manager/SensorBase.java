package hu.droidium.remote_home_manager;

import java.util.List;

public class SensorBase implements SensorInterface {

	@Override
	public String[] getLocations() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean saveMeasurement(String location, SensorType type, long time,
			long value) {
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
	public List<Measurement> getLastHoursMeasurements(String location,
			SensorType type) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Measurement> getMeasurements(String location, SensorType type,
			long startTime, long endTime) {
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
	public List<Measurement> getMeasurementAverages(String location,
			SensorType type, long startTime, long endTime, long window) {
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
	public Measurement getMaximum(String location, SensorType type,
			long startTime, long endTime) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Measurement getMinimum(String location, SensorType type,
			long startTime, long endTime) {
		// TODO Auto-generated method stub
		return null;
	}

}
