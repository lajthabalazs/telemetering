package hu.droidium.remote_home_manager;

import hu.droidium.remote_home_manager.runner.LovasRaspberryModularSingleNode;
import hu.droidium.telemetering.interfaces.LanguageInterface;
import hu.droidium.telemetering.interfaces.LayoutStoreInterface;
import hu.droidium.telemetering.interfaces.Measurement;
import hu.droidium.telemetering.interfaces.MeasurementStoreInterface;
import hu.droidium.telemetering.interfaces.ProgramStoreInterface;
import hu.droidium.telemetering.interfaces.SensorType;
import hu.droidium.telemetering.interfaces.TimeFrame;
import hu.droidium.telemetering.interfaces.TimeLimitsAndWindow;
import hu.droidium.telemetering.interfaces.TimePeriod;
import hu.droidium.telemetering.interfaces.Utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HungarianLanguageModule implements LanguageInterface {

	// Formats for lines
	public static final SimpleDateFormat timestampFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
	public static final SimpleDateFormat minuteFormat = new SimpleDateFormat("HH:mm");
	public static final SimpleDateFormat halfHourFormat = new SimpleDateFormat("dd. HH:mm");
	public static final SimpleDateFormat hourFormat = new SimpleDateFormat("MM/dd HH'h'");
	public static final SimpleDateFormat dayFormatLong = new SimpleDateFormat("yyyy/MM/dd");
	public static final SimpleDateFormat dayFormat = new SimpleDateFormat("MM/dd");
	public static final SimpleDateFormat weeFormat = new SimpleDateFormat("yyyy 'w. hét'");
	
	
	private static final String QUESTION = "(mennyi|hány|milyen|mi|mennyire)";
	private static final String SENSOR_TYPES = "(meleg|hideg|fok|idő|az idő|hőmérséklet|a hőmérséklet|a légnyomás|világos|sötét|a szél|mozgás|a mozgás)";
	private static final String DATE_PATTERN = "\\d\\d\\d\\d[/\\-\\.]\\d\\d[/\\-\\.]\\d\\d";
	private static final String SHORT_TIME_PATTERN = "\\d\\d";
	private static final String TIME_PATTERN = "\\d\\d\\:\\d\\d";
	private static final String DATE_WITH_SUFFIX = "(ma|tegnap|tegnapelőtt|a hét|a múlt hét|múlt hét|hétfő|kedd|szerda?|csütörtök|péntek|szombat|vasárnap|" + DATE_PATTERN + "|" + TIME_PATTERN + "|" + SHORT_TIME_PATTERN + ")";
	private static final String DATE = DATE_WITH_SUFFIX + "(?:\\-?[eéáöo]?n)*(?:\\-kor)*";
	private static final HashMap<String, SensorType> SENSOR_TYPE_HASH = new HashMap<String,SensorType>();
	static {
		SENSOR_TYPE_HASH.put("meleg", SensorType.TEMPERATURE);
		SENSOR_TYPE_HASH.put("hideg", SensorType.TEMPERATURE);
		SENSOR_TYPE_HASH.put("fok", SensorType.TEMPERATURE);
		SENSOR_TYPE_HASH.put("idő", SensorType.TEMPERATURE);
		SENSOR_TYPE_HASH.put("az idő", SensorType.TEMPERATURE);
		SENSOR_TYPE_HASH.put("hőmérséklet", SensorType.TEMPERATURE);
		SENSOR_TYPE_HASH.put("a hőmérséklet", SensorType.TEMPERATURE);
		SENSOR_TYPE_HASH.put("a légnyomás", SensorType.PRESSURE);
		SENSOR_TYPE_HASH.put("világos", SensorType.LIGHT);
		SENSOR_TYPE_HASH.put("sötét", SensorType.LIGHT);
		SENSOR_TYPE_HASH.put("a szél", SensorType.WIND);
		SENSOR_TYPE_HASH.put("mozgás", SensorType.MOVEMENT);
		SENSOR_TYPE_HASH.put("a mozgás", SensorType.MOVEMENT);
		
	}
	private static final HashMap<SensorType, String> SENSOR_TYPE_TO_STRING_HASH = new HashMap<SensorType, String>();
	static {
		SENSOR_TYPE_TO_STRING_HASH.put(SensorType.TEMPERATURE, "fok");
		SENSOR_TYPE_TO_STRING_HASH.put(SensorType.PRESSURE, "a légnyomás");
		SENSOR_TYPE_TO_STRING_HASH.put(SensorType.LIGHT, "világos");
		SENSOR_TYPE_TO_STRING_HASH.put(SensorType.LIGHT, "sötét");
		SENSOR_TYPE_TO_STRING_HASH.put(SensorType.WIND,"a szél");
		SENSOR_TYPE_TO_STRING_HASH.put(SensorType.MOVEMENT, "mozgás");
		SENSOR_TYPE_TO_STRING_HASH.put(SensorType.MOVEMENT, "a mozgás");
	}

	private MeasurementStoreInterface sensorDataStore;
	@SuppressWarnings("unused")
	private LayoutStoreInterface layoutStore;
	@SuppressWarnings("unused")
	private ProgramStoreInterface programStore;
	private RelayController relayController;
	
	public HungarianLanguageModule( LayoutStoreInterface layoutStore, MeasurementStoreInterface sensorDataStore, ProgramStoreInterface programStore, RelayController relayController) {
		this.sensorDataStore = sensorDataStore;
		this.layoutStore = layoutStore;
		this.programStore = programStore;
		this.relayController = relayController;
	}
	
	public HungarianLanguageModule(LovasRaspberryModularSingleNode node) {
		this.sensorDataStore = node.getSensorDataStore();
		this.layoutStore = node.getLayoutStore();
		this.programStore = node.getProgramStore();
		this.relayController = node.getRelayController();
	}

	@Override
	public String getResponse(String message, long time) {
		log("Question: " + message);
		SimpleDateFormat format = new SimpleDateFormat("YYYY/MM/dd HH:mm");
		log("Now " + format.format(new Date(time)));
		message = message.toLowerCase();
		
		// Matching direct command
		
		if (message.startsWith("kapcsold be")) {
			Relay relay = getRelay(message);
			relayController.setState(relay, RelayState.ON);
			return "Bal konnektor " + RelayState.ON.toResultStateString() + ".";
			
		} else if (message.startsWith("kapcsold ki")) {
			Relay relay = getRelay(message);
			relayController.setState(relay, RelayState.OFF);
			return "Jobb konnektor " + RelayState.OFF.toResultStateString() + ".";
		}
		
		// Matching rule
		
		// Matching queries
		Pattern currentPattern = Pattern.compile(QUESTION + "( van | )" + SENSOR_TYPES + "( van | )(.*)\\?");
		Matcher currentMatcher = currentPattern.matcher(message.toLowerCase());
		if (currentMatcher.matches()) {
			System.out.println("Current matcher matches pattern.");
			int groups = currentMatcher.groupCount();
			if (groups == 5) {
				if (currentMatcher.group(2).equals(" van ") || currentMatcher.group(4).equals(" van ")) {
					String location = currentMatcher.group(5);
					SensorType type = SENSOR_TYPE_HASH.get(currentMatcher.group(3));
					log("ACTUAL");
					log("Sensor type: " + type);
					log("Location: " + location);
					log("Time: Last measurement");
					Measurement m = sensorDataStore.getLastMeasurement(location, type);
					if (m != null) {
						return presentMeasurementToString(m);
					} else {
						return "Nem tudom " + currentMatcher.group(1) + " " + currentMatcher.group(3) + " van " + location + ".";
					}
					 
				} else {
					System.out.println("Not van: >" + currentMatcher.group(2) + "< or this >" + currentMatcher.group(4) + "< ");
				}
			} else {
				System.out.println("Invalid number of groups " + groups);				
			}
		}
		Pattern firstPastPattern = Pattern.compile(QUESTION + "( volt | )" + SENSOR_TYPES + "( volt | )(.*) " + DATE + "\\?");
		Matcher firstPastMatcher = firstPastPattern.matcher(message.toLowerCase());
		Pattern	secondPastPattern = Pattern.compile(QUESTION + "( volt | )" + SENSOR_TYPES + "( volt | )" + DATE + " (.*)\\?");
		Matcher secondPastMatcher = secondPastPattern.matcher(message.toLowerCase());
		if (firstPastMatcher.matches() || secondPastMatcher.matches()) {
			log("PAST");
			String question = null;
			String location = null;
			SensorType type = null;
			String queryTime = null;
			boolean ok = false;
			if (firstPastMatcher.matches()) {
				if (firstPastMatcher.groupCount() == 6) {
					System.out.println("First past pattern.");
					question =  firstPastMatcher.group(1);
					location = firstPastMatcher.group(5);
					type = SENSOR_TYPE_HASH.get(firstPastMatcher.group(3));
					queryTime = firstPastMatcher.group(6);
					ok = true;
				} else {
					System.out.println("First not 6 groups " + firstPastMatcher.groupCount());
				}
			}
			if (secondPastMatcher.matches()){
				if (secondPastMatcher.groupCount() == 6) {
					System.out.println("Second past pattern.");
					question =  secondPastMatcher.group(1);
					location = secondPastMatcher.group(6);
					type = SENSOR_TYPE_HASH.get(secondPastMatcher.group(3));
					queryTime = secondPastMatcher.group(5);
					ok = true;
				} else {
					System.out.println("Second not 6 groups " + firstPastMatcher.groupCount());
				}
			}
			if (ok) {
				log("Sensor type: " + type);
				log("Location: " + location);
				log("Time: " + queryTime);
				TimeLimitsAndWindow limits = getTimeLimitsAndWindow(queryTime, time);
				log("Processed time: " + limitsToReadableString(limits));
				List<Measurement> m;
				if (limits.getPeriod() != -1) {
					System.out.println("Limits with periond");
					m = sensorDataStore.getMeasurementAverages(location, type, limits.getStart(), limits.getEnd(), limits.getPeriod());
				} else {
					System.out.println("Limits without period");
					m = sensorDataStore.getMeasurements(location, type, limits.getStart(), limits.getEnd());
				}
				System.out.println("Found measurements " + m);
				if (m == null || m.size() == 0) {
					return "Nem tudom " + question + " meleg volt " + queryTime + " " + location + ".";
				} else {
					TimeFrame frame = TimeFrame.DAY;
					return measurementsToString(m, location, limits.getStart(), frame, limits.getTimePeriod());
				}
			}
		}
		return null;
	}
	
	private Relay getRelay(String message) {
		if (message.contains("bal") || message.contains("első")||message.contains("elso")) {
			return Relay.RELAY_ONE;
		} else {
			return Relay.RELAY_TWO;
		}
	}

	/**
	 * 
	 * @param limits
	 * @return
	 */
	private static final String limitsToReadableString(TimeLimitsAndWindow limits) {
		SimpleDateFormat format = new SimpleDateFormat("YYYY/MM/dd HH:mm");
		String ret = format.format(new Date(limits.getStart()));
		ret = ret + " - " + format.format(new Date(limits.getEnd()));
		if (limits.getPeriod() != null) {
			int secs = (int)(limits.getPeriod() / 1000);
			ret = ret + " period: " + secs / 3600 + ":" + ((secs % 60 < 10)?"0":"") + secs % 60;
		}
		return ret;
	}

	private static final TimeLimitsAndWindow getTimeLimitsAndWindow(String date, long time) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(time);
		TimeLimitsAndWindow limits = new TimeLimitsAndWindow();
		if (date.equals("ma")) {
			limits.setEnd( calendar.getTimeInMillis());
			Utils.stripToDayStart(calendar);
			limits.setStart(calendar.getTimeInMillis());
			limits.setPeriod(TimePeriod.HOUR);
		} else if (date.equals("tegnap")) {
			Utils.stripToDayStart(calendar);
			limits.setStart(calendar.getTimeInMillis() - Utils.DAY_MILLIS);
			limits.setEnd(calendar.getTimeInMillis());
			limits.setPeriod(TimePeriod.HOUR);
		} else if (date.equals("tegnapelőtt")) {
			Utils.stripToDayStart(calendar);
			limits.setStart(calendar.getTimeInMillis() - Utils.DAY_MILLIS * 2);
			limits.setEnd(calendar.getTimeInMillis() - Utils.DAY_MILLIS);
			limits.setPeriod(TimePeriod.HOUR);
		} else if (date.equals("a hét")) {
			limits.setEnd(calendar.getTimeInMillis());
			Utils.stripToWeekStart(calendar);
			limits.setStart(calendar.getTimeInMillis());
			limits.setPeriod(TimePeriod.DAY);
		} else if (date.equals("a múlt hét") || date.equals("múlt hét")) {
			Utils.stripToWeekStart(calendar);
			limits.setStart(calendar.getTimeInMillis() - Utils.DAY_MILLIS * 7);
			limits.setEnd(calendar.getTimeInMillis());
			limits.setPeriod(TimePeriod.DAY);
		} else if (date.equals("hétfő")) {
			return getLimitsForDayOfWeek(calendar, 0);
		} else if (date.equals("kedd")) {
			return getLimitsForDayOfWeek(calendar, 1);
		} else if (date.equals("szerd")) {
			return getLimitsForDayOfWeek(calendar, 2);
		} else if (date.equals("csütörtök")) {
			return getLimitsForDayOfWeek(calendar, 3);
		} else if (date.equals("péntek")) {
			return getLimitsForDayOfWeek(calendar, 4);
		} else if (date.equals("szombat")) {
			return getLimitsForDayOfWeek(calendar, 5);
		} else if (date.equals("vasárnap")) {
			return getLimitsForDayOfWeek(calendar, 6);
		} else if (date.matches(DATE_PATTERN)) {
			Utils.stripToDayStart(calendar);
			int year = Integer.parseInt(date.substring(0,4));
			int month = Integer.parseInt(date.substring(5,7)) - 1;
			int day = Integer.parseInt(date.substring(8,10));
			Utils.stripToDayStart(calendar);
			calendar.set(Calendar.YEAR, year);
			calendar.set(Calendar.MONTH, month);
			calendar.set(Calendar.DAY_OF_MONTH, day);
			limits.setStart(calendar.getTimeInMillis());
			limits.setEnd(calendar.getTimeInMillis() + Utils.DAY_MILLIS);
			limits.setPeriod(TimePeriod.HOUR);
		} else if (date.matches(SHORT_TIME_PATTERN)) {
			int hour = calendar.get(Calendar.HOUR_OF_DAY);
			if (hour < Integer.parseInt(date)) {
				calendar.setTimeInMillis(calendar.getTimeInMillis() - Utils.DAY_MILLIS);
			}
			calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(date));
			calendar.set(Calendar.MINUTE, 0);
			calendar.set(Calendar.SECOND, 0);
			calendar.set(Calendar.MILLISECOND, 0);
			limits.setStart(calendar.getTimeInMillis() - Utils.HOUR_MILLIS / 2);
			limits.setEnd(calendar.getTimeInMillis() + Utils.HOUR_MILLIS / 2);
			limits.setPeriod(TimePeriod.FIVE_MINUTE);
		} else if (date.matches(TIME_PATTERN)) {
			int hour = calendar.get(Calendar.HOUR_OF_DAY);
			int minute = calendar.get(Calendar.MINUTE);
			int requestedHour = Integer.parseInt(date.substring(0,2));
			int requestedMinute = Integer.parseInt(date.substring(3,5));
			calendar.set(Calendar.MILLISECOND, 0);
			calendar.set(Calendar.SECOND, 0);
			calendar.set(Calendar.MINUTE, 0);
			if (hour < requestedHour || (hour == requestedHour && minute > requestedMinute)) {
				calendar.setTimeInMillis(calendar.getTimeInMillis() - Utils.DAY_MILLIS);
			}
			calendar.set(Calendar.HOUR_OF_DAY, requestedHour);
			calendar.set(Calendar.MINUTE, requestedMinute);
			limits.setStart(calendar.getTimeInMillis() - Utils.HOUR_MILLIS / 8);
			limits.setEnd(calendar.getTimeInMillis() + Utils.HOUR_MILLIS / 8);
			limits.setPeriod(TimePeriod.MINUTE);
		}
		return limits;
	}
	
	private static final TimeLimitsAndWindow getLimitsForDayOfWeek(Calendar calendar, int dayIndex) {
		TimeLimitsAndWindow limits = new TimeLimitsAndWindow();
		int currentDay = Utils.getDayOfWeekIndex(calendar);
		if (currentDay > dayIndex) { // This week
			Utils.stripToWeekStart(calendar);
			limits.setStart(calendar.getTimeInMillis() + dayIndex * Utils.DAY_MILLIS);
			limits.setEnd(calendar.getTimeInMillis() + (dayIndex + 1) * Utils.DAY_MILLIS);
		} else { // Last week
			Utils.stripToWeekStart(calendar);
			limits.setStart(calendar.getTimeInMillis() - (7 - dayIndex) * Utils.DAY_MILLIS);
			limits.setEnd(calendar.getTimeInMillis() - (6 - dayIndex) * Utils.DAY_MILLIS);
		}
		limits.setPeriod(TimePeriod.HOUR);
		return limits;
	}

	private static String presentMeasurementToString(Measurement m) {
		return m.getLocation() + " " + m.getValueString() + " fok van.";
	}

	private static String pastMeasurementToString(Measurement m, TimePeriod timePeriod) {
		if (timePeriod != null) {
			switch(timePeriod) {
			case MINUTE: case FIVE_MINUTE:
				return minuteFormat.format(new Date(m.getTime())) + " " + m.getValueString() + " C";
			case HALF_HOUR:
				return halfHourFormat.format(new Date(m.getTime())) + " " + m.getValueString() + " C";
			case HOUR:
				return hourFormat.format(new Date(m.getTime())) + " " + m.getValueString() + " C";
			case DAY:
				return dayFormat.format(new Date(m.getTime())) + " " + m.getValueString() + " C";
			case WEEK:
				return weeFormat.format(new Date(m.getTime())) + " " + m.getValueString() + " C";
			}
		}
		return m.getLocation() + " " + timestampFormat.format(new Date(m.getTime())) + " " + m.getValueString() + " fok volt.";
	}

	private static String measurementsToString(List<Measurement> ms, String location, long startTime, TimeFrame timeFrame, TimePeriod timePeriod) {
		String ret = "";
		for (Measurement m : ms) {
			if (ret.length() > 0) {
				ret = ret + "\n";
			}
			ret = ret + pastMeasurementToString(m, timePeriod);
		}
		String label = dayFormatLong.format(new Date(startTime));
		return label + ", " + location + " : \n" + ret;
	}

	private static final void log(String string){
		System.out.println(string);
	}
}