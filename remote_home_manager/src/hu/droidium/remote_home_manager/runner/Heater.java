package hu.droidium.remote_home_manager.runner;

import hu.droidium.telemetering.interfaces.AutoTarget;
import hu.droidium.telemetering.interfaces.LayoutStoreInterface;
import hu.droidium.telemetering.interfaces.MeasurementStoreInterface;
import hu.droidium.telemetering.interfaces.ProgramStoreInterface;
import hu.droidium.telemetering.interfaces.SensorType;

public class Heater {
	
	private int minTemperature = 100; // Safety min limit, switch on, no matter what
	private int maxTemperature = 2000; // Safety max limit, switch off, no matter what
	
	boolean autoHeatingState = false;
	private LayoutStoreInterface layoutStore;
	private MeasurementStoreInterface measurementStore;
	private ProgramStoreInterface programStore;
	
	public Heater(LayoutStoreInterface layoutStore, MeasurementStoreInterface measurementStore, ProgramStoreInterface programStore) {
		this.layoutStore = layoutStore;
		this.measurementStore = measurementStore;
		this.programStore = programStore;
	}
	
	public boolean shouldHeat(String heater, long curretTime) {
		String location = layoutStore.getLocations().get(0); // For now only check the single location
		int temperature = (int)measurementStore.getLastMeasurement(location, SensorType.TEMPERATURE).getValue();
		AutoTarget program = programStore.getTarget(location);
		long heatingEnd = programStore.getHeatingEnd(heater);
		// Safety limits
		if (temperature < minTemperature) {
			return true;
		}
		if (temperature > maxTemperature) {
			return false;
		}
		// Manual override
		if (curretTime < heatingEnd) {
			return true;
		}
		// Automated program
		if (program.autoModeEnabled) {
			if (temperature < program.target - program.threshold) {
				autoHeatingState = true;
				return true;
			}
			if (temperature > program.target + program.threshold) {
				autoHeatingState = false;
				return false;
			}
			// In auto mode, keep previous state when in threshold
			return autoHeatingState;
		}
		// No heating required
		return false;
	}

	public int getMinTemperature() {
		return minTemperature;
	}

	public void setMinTemperature(int minTemperature) {
		this.minTemperature = minTemperature;
	}

	public int getMaxTemperature() {
		return maxTemperature;
	}

	public void setMaxTemperature(int maxTemperature) {
		this.maxTemperature = maxTemperature;
	}
}