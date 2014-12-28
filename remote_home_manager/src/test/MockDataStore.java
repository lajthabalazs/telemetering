package test;

import java.util.LinkedList;
import java.util.List;

import hu.droidium.remote_home_manager.DatastoreBase;
import hu.droidium.telemetering.interfaces.Measurement;
import hu.droidium.telemetering.interfaces.SensorType;

public class MockDataStore extends DatastoreBase {
	
	public enum Method {
		GET_LAST_MEASUREMENT, GET_MEASUREMENTS, GET_AVERAGES
	}

	public class Call {
		public final Method method;
		public final String location;
		public final SensorType type;
		public final long startTime;
		public final long endTime;
		public final long window;
		
		public Call(Method method, String location, SensorType type, long startTime, long endTime, long window) {
			this.method = method;
			this.location = location;
			this.type = type;
			this.startTime = startTime;
			this.endTime = endTime;
			this.window = window;
		}
		public Call(Method method, String location, SensorType type, long startTime, long endTime) {
			this.method = method;
			this.location = location;
			this.type = type;
			this.startTime = startTime;
			this.endTime = endTime;
			this.window = -1;
		}
		public Call(Method method, String location, SensorType type) {
			this.method = method;
			this.location = location;
			this.type = type;
			this.startTime = -1;
			this.endTime = -1;
			this.window = -1;
		}

	}

	private List<Call> calls = new LinkedList<Call>();
	
	public void clear() {
		calls.clear();
	}
	
	public List<Call> getCalls() {
		return new LinkedList<Call>(calls);
	}

	@Override
	public Measurement getLastMeasurement(String location, SensorType type) {
		calls.add(new Call(Method.GET_LAST_MEASUREMENT, location, type));
		return null;
	}

	@Override
	public List<Measurement> getMeasurementAverages(String location, SensorType type, long startTime, long endTime,
			long window) {
		calls.add(new Call(Method.GET_AVERAGES, location, type, startTime, endTime, window));
		return null;
	}
	
	@Override
	public Measurement getMeasurementAverage(String location, SensorType type, long startTime, long endTime) {
		calls.add(new Call(Method.GET_AVERAGES, location, type, startTime, endTime));
		return null;
	}

	@Override
	public List<Measurement> getMeasurements(String location, SensorType type,
			long startTime, long endTime) {
		calls.add(new Call(Method.GET_MEASUREMENTS, location, type, startTime, endTime));
		return null;
	}

	@Override
	public boolean saveMeasurement(String location, SensorType type, long time,
			long value) {
		return false;
	}

	@Override
	public boolean bulkInster(List<Measurement> measurements) {
		return false;
	}
}