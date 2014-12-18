package hu.droidium.remote_home_manager;

public class TimeLimitsAndWindow {
	
	private long startTime;
	private long endTime;
	private TimePeriod period;

	public Long getStart() {
		return startTime;
	}

	public Long getEnd() {
		return endTime;
	}

	public Long getPeriod() {
		if (period == null) {
			return -1l;
		}
		return period.getLength();
	}

	public void setStart(long startTime) {
		this.startTime = startTime;
	}

	public void setEnd(long endTime) {
		this.endTime = endTime;
	}

	public void setPeriod(TimePeriod period) {
		this.period = period;
	}

	public TimePeriod getTimePeriod() {
		return period;
	}
}
