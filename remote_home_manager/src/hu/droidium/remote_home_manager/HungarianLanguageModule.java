package hu.droidium.remote_home_manager;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HungarianLanguageModule implements LanguageInterface {

	private static final String QUESTION = "(mennyi|hány|milyen|mi|mennyire)";
	private static final String SENSOR_TYPES = "(meleg|hideg|fok|idő|az idő|hőmérséklet|a hőmérséklet|a légnyomás|világos|sötét|a szél|mozgás|a mozgás)";
	private static final String DATE_PATTERN = "dddd[/\\-\\.]dd[/\\-\\.]dd";
	private static final String DATE_PATTERNS = "(ma|tegnap|tegnap előtt|a héten|a múlt héten|múlt héten|hétfőn|kedden|szerdán|csütörtökön|pénteken|szombaton|vasárnap|" + DATE_PATTERN + " )";
	private static final String DATE = DATE_PATTERNS + "(?:\\-[éá]n)*";
	
	@Override
	public String getResponse(String message, SensorInterface sensorDataStore) {
		message = message.toLowerCase();
		Pattern currentPattern = Pattern.compile(QUESTION + "( van | )" + SENSOR_TYPES + "( van |)(.*)\\?");
		Matcher currentMatcher = currentPattern.matcher(message.toLowerCase());
		if (currentMatcher.matches()) {
			int groups = currentMatcher.groupCount();
			if (groups == 5) {
				if (currentMatcher.group(2).equals(" van ") || currentMatcher.group(4).equals(" van ")) {
					for (int i = 1; i <= groups; i++) {
						System.out.println(">" + currentMatcher.group(i) + "<");
					}
					return null;
				}
			}
		}
		Pattern pastPattern = Pattern.compile(QUESTION + "( volt | )" + SENSOR_TYPES + "( volt | )(.*) " + DATE + "\\?");
		Matcher pastMatcher = pastPattern.matcher(message.toLowerCase());
		if (pastMatcher.matches()) {
			int groups = pastMatcher.groupCount();
			for (int i = 1; i <= groups; i++) {
				System.out.println(">" + pastMatcher.group(i) + "<");
			}
			return null;
		}
		pastPattern = Pattern.compile(QUESTION + "( volt | )" + SENSOR_TYPES + "( volt | )" + DATE + " (.*)\\?");
		pastMatcher = pastPattern.matcher(message.toLowerCase());
		if (pastMatcher.matches()) {
			int groups = pastMatcher.groupCount();
			for (int i = 1; i <= groups; i++) {
				System.out.println(">" + pastMatcher.group(i) + "<");
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
	}
}