package hu.droidium.remote_home_manager;

import java.util.Calendar;

public class Utils {
	public static final long HOUR_MILLIS = 3600l * 1000l;
	public static final long DAY_MILLIS = 24l * 3600l * 1000l;

	public static void stripToDayStart(Calendar calendar) {
		calendar.set(Calendar.MILLISECOND, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
	}
	
	public static void stripToWeekStart(Calendar calendar) {
		calendar.set(Calendar.MILLISECOND, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		int daysPassedInWeek = 0;
		switch (calendar.get(Calendar.DAY_OF_WEEK)) {
		case Calendar.MONDAY: {
			daysPassedInWeek = 0;
			break;
		}
		case Calendar.TUESDAY: {
			daysPassedInWeek = 1;
			break;
		}
		case Calendar.WEDNESDAY: {
			daysPassedInWeek = 2;
			break;
		}
		case Calendar.THURSDAY: {
			daysPassedInWeek = 3;
			break;
		}
		case Calendar.FRIDAY: {
			daysPassedInWeek = 4;
			break;
		}
		case Calendar.SATURDAY: {
			daysPassedInWeek = 5;
			break;
		}
		case Calendar.SUNDAY: {
			daysPassedInWeek = 6;
			break;
		}
		}
		long endTime = calendar.getTimeInMillis() - daysPassedInWeek * 24 * HOUR_MILLIS;
		calendar.setTimeInMillis(endTime);
		
	}


}
