package hu.droidium.remote_home_manager;

public class Measurement {

	private String sensorId;
	private Long time;
	private Long value;

	public Measurement(String sensorId, Long time, Long value) {
		this.sensorId = sensorId;
		this.time = time;
		this.value = value;
	}

	public Object getValue() {
		return value;
	}

	public Object getSensorId() {
		return sensorId;
	}

}
