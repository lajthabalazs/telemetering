package hu.droidium.remote_home_manager;

import java.util.List;

public interface SensorInterface {
	
	public boolean saveMeasurement(String sensorId, long time, long value);
	public boolean bulkInster(List<Measurement> measurements);
	
	public Measurement getLastMeasurement(String sensorId);
	public List<Measurement> getLastHoursMeasurements(String sensorId);
	public List<Measurement> getMeasurements(String sensorId, long startTime, long endTime);
	
	public Measurement getLastHoursAverage(String sensorId);
	public Measurement getLastDaysAverage(String sensorId);
	public Measurement getLastWeeksAverage(String sensorId);
	public Measurement getLastMonthsAverage(String sensorId);

	public List<Measurement> getLastDayByHours(String sensorId);
	public List<Measurement> getLastWeekByDays(String sensorId);
	public List<Measurement> getLastMonthByDays(String sensorId);
	public List<Measurement> getMeasurementAverages(String sensorId, long startTime, long endTime, long window);

	public Measurement getLastHoursMaximum(String sensorId);
	public Measurement getLastHoursMinimum(String sensorId);
	public Measurement getLastDaysMaximum(String sensorId);
	public Measurement getLastDaysMinimum(String sensorId);
	public Measurement getLastWeeksMaximum(String sensorId);
	public Measurement getLastWeeksMinimum(String sensorId);
	public Measurement getLastMonthsMaximum(String sensorId);
	public Measurement getLastMonthsMinimum(String sensorId);
	public Measurement getMaximum(String sensorId, long startTime, long endTime);
	public Measurement getMinimum(String sensorId, long startTime, long endTime);
}