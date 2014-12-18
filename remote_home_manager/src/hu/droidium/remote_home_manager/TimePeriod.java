package hu.droidium.remote_home_manager;

public enum TimePeriod {
	HALF_HOUR(Utils.HOUR_MILLIS / 2),HOUR(Utils.HOUR_MILLIS), DAY(Utils.DAY_MILLIS), WEEK(Utils.DAY_MILLIS * 7);
	private final long length;
	private TimePeriod(long length){
		this.length = length;
	}
	
	public long getLength() {
		return length;
	}
}