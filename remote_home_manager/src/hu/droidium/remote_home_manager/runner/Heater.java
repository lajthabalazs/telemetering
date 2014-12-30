package hu.droidium.remote_home_manager.runner;

public class Heater {
	
	private int minTemperature = -100; // Safety min limit, switch on, no matter what
	private int maxTemperature = 2000; // Safety max limit, switch off, no matter what
	private int targetTemperature = 600; // A target temperature
	private int threshold = 200;
	private boolean auto = false;
	
	private long manualSwitchOnStart = 0;
	private long manualSwitchOnEnd = 0;
	
	private boolean autoHeatingState = false;
	
	public Heater() {
	}
	
	public boolean shouldHeat(long curretTime, int temperature) {
		// Safety limits
		if (temperature < minTemperature) {
			return true;
		}
		if (temperature > maxTemperature) {
			return false;
		}
		// Manual override
		if (curretTime < manualSwitchOnEnd) {
			return true;
		}
		// Automated program
		if (auto) {
			if (temperature < targetTemperature - threshold) {
				autoHeatingState = true;
				return true;
			}
			if (temperature > targetTemperature + threshold) {
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

	public int getTargetTemperature() {
		return targetTemperature;
	}

	public void setTargetTemperature(int targetTemperature) {
		this.targetTemperature = targetTemperature;
	}

	public long getManualSwitchOnEnd() {
		return manualSwitchOnEnd;
	}

	public void setManualSwitchOnEnd(long manualSwitchOnEnd) {
		this.manualSwitchOnEnd = manualSwitchOnEnd;
	}

	public long getManualSwitchOnStart() {
		return manualSwitchOnStart;
	}

	public void setManualSwitchOnStart(long manualSwitchOnStart) {
		this.manualSwitchOnStart = manualSwitchOnStart;
	}
	
	public int getThreshold(){
		return threshold;
	}
	
	public void setThreshold(int threshold){
		this.threshold = threshold;
	}
	
	public boolean getAuto() {
		return auto;
	}
	
	public void setAuto(boolean auto) {
		this.auto = auto;
	}
}