package hu.droidium.remote_home_manager;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HungarianLanguageModule implements LanguageInterface {

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
	private SensorInterface sensorDataStore;
	
	public HungarianLanguageModule( SensorInterface sensorDataStore) {
		this.sensorDataStore = sensorDataStore;
	}
	
	@Override
	public String getResponse(String message, long time) {
		log("Question: " + message);
		SimpleDateFormat format = new SimpleDateFormat("YYYY/MM/dd HH:mm");
		log("Now " + format.format(new Date(time)));
		message = message.toLowerCase();
		Pattern currentPattern = Pattern.compile(QUESTION + "( van | )" + SENSOR_TYPES + "( van | )(.*)\\?");
		Matcher currentMatcher = currentPattern.matcher(message.toLowerCase());
		if (currentMatcher.matches()) {
			int groups = currentMatcher.groupCount();
			if (groups == 5) {
				if (currentMatcher.group(2).equals(" van ") || currentMatcher.group(4).equals(" van ")) {
					String location = currentMatcher.group(5);
					SensorType type = SENSOR_TYPE_HASH.get(currentMatcher.group(3));
					log("ACTUAL");
					log("Sensor type: " + type);
					log("Location: " + location);
					Measurement m = sensorDataStore.getLastMeasurement(location, type);
					if (m != null) {
						return m.toString();
					} else {
						return "Nem tudom " + currentMatcher.group(1) + " " + currentMatcher.group(3) + " van " + location + ".";
					}
					 
				}
			}
		}
		Pattern pastPattern = Pattern.compile(QUESTION + "( volt | )" + SENSOR_TYPES + "( volt | )(.*) " + DATE + "\\?");
		Matcher pastMatcher = pastPattern.matcher(message.toLowerCase());
		if (pastMatcher.matches()) {
			int groups = pastMatcher.groupCount();
			if (groups == 6) {
				String location = pastMatcher.group(5);
				SensorType type = SENSOR_TYPE_HASH.get(pastMatcher.group(3));
				log("PAST");
				log("Sensor type: " + type);
				log("Location: " + location);
				log("Time: " + pastMatcher.group(6));
				long[] limits = getTimeLimitsAndWindow(pastMatcher.group(6), time);
				log("Processed time: " + toDate(limits));
				List<Measurement> m;
				if (limits[2] != -1) {
					m = sensorDataStore.getMeasurementAverages(location, type, limits[0], limits[1], limits[2]);
				} else {
					m = sensorDataStore.getMeasurements(location, type, limits[0], limits[1]);
				}
				if (m == null || m.size() == 0) {
					return "Nem tudom " + pastMatcher.group(1) + " volt " + pastMatcher.group(3) + " " + location + ".";
				} else {
					return m.toString();
				}
			}
		}
		pastPattern = Pattern.compile(QUESTION + "( volt | )" + SENSOR_TYPES + "( volt | )" + DATE + " (.*)\\?");
		pastMatcher = pastPattern.matcher(message.toLowerCase());
		if (pastMatcher.matches()) {
			int groups = pastMatcher.groupCount();
			if (groups == 6) {
				String location = pastMatcher.group(6);
				SensorType type = SENSOR_TYPE_HASH.get(pastMatcher.group(3));
				log("PAST");
				log("Sensor type: " + type);
				log("Location: " + location);
				log("Time: " + pastMatcher.group(5));
				long[] limits = getTimeLimitsAndWindow(pastMatcher.group(5), time);
				log("Processed time: " + toDate(limits));
				List<Measurement> m;
				if (limits[2] != -1) {
					m = sensorDataStore.getMeasurementAverages(location, type, limits[0], limits[1], limits[2]);
				} else {
					m = sensorDataStore.getMeasurements(location, type, limits[0], limits[1]);
				}
				if (m == null || m.size() == 0) {
					return "Nem tudom " + pastMatcher.group(1) + " volt " + pastMatcher.group(3) + " " + location + ".";
				} else {
					return m.toString();
				}
			}
		}
		return null;
	}
	
	private static final String toDate(long[] limits) {
		SimpleDateFormat format = new SimpleDateFormat("YYYY/MM/dd HH:mm");
		String ret = format.format(new Date(limits[0]));
		ret = ret + " - " + format.format(new Date(limits[1]));
		if (limits[2] != -1) {
			int secs = (int)(limits[2] / 1000);
			ret = ret + " period: " + secs / 3600 + ":" + ((secs % 60 < 10)?"0":"") + secs % 60;
		}
		return ret;
	}

	private static final long[] getTimeLimitsAndWindow(String date, long time) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(time);
		long[] ret = new long[3];
		if (date.equals("ma")) {
			ret[1] = calendar.getTimeInMillis();
			Utils.stripToDayStart(calendar);
			ret[0] = calendar.getTimeInMillis();
			ret[2] = Utils.HOUR_MILLIS;
		} else if (date.equals("tegnap")) {
			Utils.stripToDayStart(calendar);
			ret[0] = calendar.getTimeInMillis() - Utils.DAY_MILLIS;
			ret[1] = calendar.getTimeInMillis();
			ret[2] = Utils.HOUR_MILLIS;
		} else if (date.equals("tegnapelőtt")) {
			Utils.stripToDayStart(calendar);
			ret[0] = calendar.getTimeInMillis() - Utils.DAY_MILLIS * 2;
			ret[1] = calendar.getTimeInMillis() - Utils.DAY_MILLIS;
			ret[2] = Utils.HOUR_MILLIS;
		} else if (date.equals("a hét")) {
			ret[1] = calendar.getTimeInMillis();
			Utils.stripToWeekStart(calendar);
			ret[0] = calendar.getTimeInMillis();
			ret[2] = Utils.DAY_MILLIS;
		} else if (date.equals("a múlt hét") || date.equals("múlt hét")) {
			Utils.stripToWeekStart(calendar);
			ret[0] = calendar.getTimeInMillis() - Utils.DAY_MILLIS * 7;
			ret[1] = calendar.getTimeInMillis();
			ret[2] = Utils.DAY_MILLIS;
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
			ret[0] = calendar.getTimeInMillis();
			ret[1] = calendar.getTimeInMillis() + Utils.DAY_MILLIS;
			ret[2] = Utils.HOUR_MILLIS;
		} else if (date.matches(SHORT_TIME_PATTERN)) {
			int hour = calendar.get(Calendar.HOUR_OF_DAY);
			if (hour < Integer.parseInt(date)) {
				calendar.setTimeInMillis(calendar.getTimeInMillis() - Utils.DAY_MILLIS);
			}
			calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(date));
			calendar.set(Calendar.MINUTE, 0);
			calendar.set(Calendar.SECOND, 0);
			calendar.set(Calendar.MILLISECOND, 0);
			ret[0] = calendar.getTimeInMillis() - Utils.HOUR_MILLIS / 2;
			ret[1] = calendar.getTimeInMillis() + Utils.HOUR_MILLIS / 2;
			ret[2] = -1;
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
			ret[0] = calendar.getTimeInMillis() - Utils.HOUR_MILLIS / 8;
			ret[1] = calendar.getTimeInMillis() + Utils.HOUR_MILLIS / 8;
			ret[2] = -1;
		}
		return ret;
	}
	
	private static final long[] getLimitsForDayOfWeek(Calendar calendar, int dayIndex) {
		long[] ret = new long[3];
		int currentDay = Utils.getDayOfWeekIndex(calendar);
		if (currentDay > dayIndex) { // This week
			Utils.stripToWeekStart(calendar);
			ret[0] = calendar.getTimeInMillis() + dayIndex * Utils.DAY_MILLIS;
			ret[1] = calendar.getTimeInMillis() + (dayIndex + 1) * Utils.DAY_MILLIS;
			ret[2] = Utils.HOUR_MILLIS;
		} else { // Last week
			Utils.stripToWeekStart(calendar);
			ret[0] = calendar.getTimeInMillis() - (7 - dayIndex) * Utils.DAY_MILLIS;
			ret[1] = calendar.getTimeInMillis() - (6 - dayIndex) * Utils.DAY_MILLIS;
			ret[2] = Utils.HOUR_MILLIS;
		}
		return ret;
	}
	
	public static void main(String[] args) {
		SensorInterface sensors = new SQLJetDatastore("empty.sqlite");
		HungarianLanguageModule module = new HungarianLanguageModule(sensors);
		long now = System.currentTimeMillis();
		log(">> " + module.getResponse("Milyen meleg van a nappaliban?", now));
		log(">> " + module.getResponse("Mennyire van meleg a nappaliban?", now));
		log(">> " + module.getResponse("Hány fok volt a nappaliban tegnap?", now));
		log(">> " + module.getResponse("Hány fok volt tegnap a nappaliban?", now));
		log(">> " + module.getResponse("Mennyire volt meleg tegnap a nappaliban?", now));
		log(">> " + module.getResponse("Milyen volt az idő tegnap a nappaliban?", now));
		log(">> " + module.getResponse("Mennyire volt hideg 2012.12.24-én a nappaliban?", now));
		log(">> " + module.getResponse("Mennyire volt hideg 2012/12/24-án a nappaliban?", now));
		log(">> " + module.getResponse("Mennyire volt hideg tegnap a nappaliban?", now));
		log(">> " + module.getResponse("Mennyire volt hideg szerdán a nappaliban?", now));
		log(">> " + module.getResponse("Mennyire volt hideg múlt héten a nappaliban?", now));
		log(">> " + module.getResponse("Mennyire volt hideg 11-kor a nappaliban?", now));
		log(">> " + module.getResponse("Mennyire volt hideg 11:15-kor a nappaliban?", now));
	}
	
	private static final void log(String string){
		System.out.println(string);
	}
}