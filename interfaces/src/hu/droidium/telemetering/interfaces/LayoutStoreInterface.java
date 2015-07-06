package hu.droidium.telemetering.interfaces;

import java.util.List;

public interface LayoutStoreInterface {

	// Meta data
	public List<String> getLocations();
	public List<SensorType> getAvailableSensorTypes(String location);
}