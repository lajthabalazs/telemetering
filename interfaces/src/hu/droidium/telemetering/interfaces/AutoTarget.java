package hu.droidium.telemetering.interfaces;

public class AutoTarget {
	
	public final long time;
	public final String location;
	public final int target;
	public final int threshold;
	public final boolean autoModeEnabled;

	public AutoTarget(String location, long time, int target, int threshold, boolean auto) {
		this.location = location;
		this.time = time;
		this.target = target;
		this.threshold = threshold;
		this.autoModeEnabled = auto;
	}
}