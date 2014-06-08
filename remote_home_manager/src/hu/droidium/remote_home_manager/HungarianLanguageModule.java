package hu.droidium.remote_home_manager;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HungarianLanguageModule implements LanguageInterface {

	private static final String QUESTION = "(mennyi|hány|milyen|mi|mennyire)";
	private static final String SENSOR_TYPES = "(meleg|hideg|fok|idő|az idő|hőmérséklet|a hőmérséklet|a légnyomás|világos|sötét|a szél|mozgás|a mozgás)";
	private static final String DATE_PATTERN = "\\d\\d\\d\\d[/\\-\\.]\\d\\d[/\\-\\.]\\d\\d";
	private static final String DATE_WITH_SUFFIX = "(ma|tegnap|tegnap előtt|a hét|a múlt hét|múlt hét|hétfő|kedd|szerda?|csütörtök|péntek|szombat|vasárnap|" + DATE_PATTERN + ")";
	private static final String DATE = DATE_WITH_SUFFIX + "(?:\\-?[eéáöo]?n)*";
	
	@Override
	public String getResponse(String message, SensorInterface sensorDataStore) {
		System.out.println("Question: " + message);
		message = message.toLowerCase();
		Pattern currentPattern = Pattern.compile(QUESTION + "( van | )" + SENSOR_TYPES + "( van |)(.*)\\?");
		Matcher currentMatcher = currentPattern.matcher(message.toLowerCase());
		if (currentMatcher.matches()) {
			int groups = currentMatcher.groupCount();
			if (groups == 5) {
				if (currentMatcher.group(2).equals(" van ") || currentMatcher.group(4).equals(" van ")) {
					System.out.println("ACTUAL");
					System.out.println("Sensor type: " + currentMatcher.group(3));
					System.out.println("Location: " + currentMatcher.group(5));
					return null;
				}
			}
		}
		Pattern pastPattern = Pattern.compile(QUESTION + "( volt | )" + SENSOR_TYPES + "( volt | )(.*) " + DATE + "\\?");
		Matcher pastMatcher = pastPattern.matcher(message.toLowerCase());
		if (pastMatcher.matches()) {
			int groups = pastMatcher.groupCount();
			if (groups == 6) {
				System.out.println("PAST");
				System.out.println("Sensor type: " + pastMatcher.group(3));
				System.out.println("Location: " + pastMatcher.group(5));
				System.out.println("Time: " + pastMatcher.group(6));
				return null;
			}
		}
		pastPattern = Pattern.compile(QUESTION + "( volt | )" + SENSOR_TYPES + "( volt | )" + DATE + " (.*)\\?");
		pastMatcher = pastPattern.matcher(message.toLowerCase());
		if (pastMatcher.matches()) {
			int groups = pastMatcher.groupCount();
			if (groups == 6) {
				System.out.println("PAST");
				System.out.println("Sensor type: " + pastMatcher.group(3));
				System.out.println("Location: " + pastMatcher.group(6));
				System.out.println("Time: " + pastMatcher.group(5));
				return null;
			}
			return null;
		}
		return null;
	}
	
	public static void main(String[] args) {
		new HungarianLanguageModule().getResponse("Milyen meleg van a nappaliban?", null);
		System.out.println();
		new HungarianLanguageModule().getResponse("Mennyire van meleg a nappaliban?", null);
		System.out.println();
		new HungarianLanguageModule().getResponse("Hány fok volt a nappaliban tegnap?", null);
		System.out.println();
		new HungarianLanguageModule().getResponse("Hány fok volt tegnap a nappaliban?", null);
		System.out.println();
		new HungarianLanguageModule().getResponse("Mennyire volt meleg tegnap a nappaliban?", null);
		System.out.println();
		new HungarianLanguageModule().getResponse("Milyen volt az idő tegnap a nappaliban?", null);
		System.out.println();
		new HungarianLanguageModule().getResponse("Mennyire volt hideg 2012.12.24-én a nappaliban?", null);
		System.out.println();
		new HungarianLanguageModule().getResponse("Mennyire volt hideg 2012/12/24-án a nappaliban?", null);
		System.out.println();
		new HungarianLanguageModule().getResponse("Mennyire volt hideg tegnap a nappaliban?", null);
		System.out.println();
		new HungarianLanguageModule().getResponse("Mennyire volt hideg szerdán a nappaliban?", null);
		System.out.println();
		new HungarianLanguageModule().getResponse("Mennyire volt hideg múlt héten a nappaliban?", null);
	}
}