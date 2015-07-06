package hu.droidium.telemetering.interfaces;

public interface ProgramStoreInterface {

	// Heating
	public boolean setTarget(String location, int targetTemperature, int targetThreshold, boolean auto, long time);
	public AutoTarget getTarget(String location);
	public boolean heatUntil(String heater, long time, long til);
	public long getHeatingEnd(String heater);
	public boolean stopHeating(String heater, long time);
}