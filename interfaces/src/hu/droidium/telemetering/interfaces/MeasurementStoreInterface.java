package hu.droidium.telemetering.interfaces;

import java.util.List;

public interface MeasurementStoreInterface {

	// Measurements
	public boolean saveMeasurement(String location, SensorType type, long time, long value);
	public boolean bulkInster(List<Measurement> measurements);
	
	public Measurement getLastMeasurement(String location, SensorType type);
	public List<Measurement> getLastHoursMeasurements(String location, SensorType type);
	public List<Measurement> getMeasurements(String location, SensorType type, long startTime, long endTime);
	
	public Measurement getLastHoursAverage(String location, SensorType type);
	public Measurement getLastDaysAverage(String location, SensorType type);
	public Measurement getLastWeeksAverage(String location, SensorType type);
	public Measurement getLastMonthsAverage(String location, SensorType type);

	public List<Measurement> getLastDayByHours(String location, SensorType type);
	public List<Measurement> getLastWeekByDays(String location, SensorType type);
	public List<Measurement> getLastMonthByDays(String location, SensorType type);
	
	public List<Measurement> getMeasurementAverages(String location, SensorType type, long startTime, long endTime, long window);
	public Measurement getMeasurementAverage(String location, SensorType type, long startTime, long endTime);

	public Measurement getLastHoursMaximum(String location, SensorType type);
	public Measurement getLastHoursMinimum(String location, SensorType type);
	public Measurement getLastDaysMaximum(String location, SensorType type);
	public Measurement getLastDaysMinimum(String location, SensorType type);
	public Measurement getLastWeeksMaximum(String location, SensorType type);
	public Measurement getLastWeeksMinimum(String location, SensorType type);
	public Measurement getLastMonthsMaximum(String location, SensorType type);
	public Measurement getLastMonthsMinimum(String location, SensorType type);
	public Measurement getMaximum(String location, SensorType type, long startTime, long endTime);
	public Measurement getMinimum(String location, SensorType type, long startTime, long endTime);	
}