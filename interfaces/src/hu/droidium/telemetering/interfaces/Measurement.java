package hu.droidium.telemetering.interfaces;

public class Measurement {

	private String location;
	private SensorType type;
	private Long time;
	private Long value;

	public Measurement(String location, SensorType type, Long time, Long value) {
		this.location = location;
		this.type = type;
		this.time = time;
		this.value = value;
	}

	public long getValue() {
		return value;
	}
	
	public String getLocation(){
		return location;
	}
	
	public SensorType getType() {
		return type;
	}

	public long getTime() {
		return time;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Measurement) {
			Measurement other = (Measurement) obj;
			if (!other.location.equals(location)) return false;
			if (!other.type.equals(type)) return false;
			if (other.time != time) return false;
			if (other.value != value) return false;
			return true;
		}
		return false;
	}

}
