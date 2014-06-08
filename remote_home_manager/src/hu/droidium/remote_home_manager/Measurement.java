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

	public long getValue() {
		return value;
	}

	public String getSensorId() {
		return sensorId;
	}

	public long getTime() {
		return time;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Measurement) {
			Measurement other = (Measurement) obj;
			if (!other.sensorId.equals(sensorId)) return false;
			if (other.time != time) return false;
			if (other.value != value) return false;
			return true;
		}
		return false;
	}

}
