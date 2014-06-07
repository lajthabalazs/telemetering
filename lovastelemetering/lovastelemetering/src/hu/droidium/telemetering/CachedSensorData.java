package hu.droidium.telemetering;

import java.io.Serializable;

public class CachedSensorData implements Serializable{
	private static final long serialVersionUID = -5401513094810154136L;
	
	public long lastWritten = 0;
	public String[] minData = new String[60];
	public String[] hourData = new String[24];
	
	public CachedSensorData() {}

	public void update(long date, String value) {
		long min = (date / 1000 / 60) % 60;
		long hour = (date / 1000 / 60 / 60) % 24;
		
	}
}
