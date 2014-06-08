package hu.droidium.remote_home_manager;

public enum SensorType {
	TEMPERATURE("temp"),
	LIGHT("light"),
	NOISE("noise"),
	MOVEMENT("movement"),
	HUMIDITY("humidity"),
	PRESSURE("pressure"),
	OPEN("open")
	;
	private String name;
	private SensorType(String name) {
		this.name = name;
	}
	@Override
	public String toString() {
		return name;
	}
	
	public String getName() {
		return name;
	}
	public static SensorType getType(String string) {
		for (SensorType type : SensorType.values()) {
			if (type.name.equals(string)){
				return type;
			}
		}
		return null;
	}
}
