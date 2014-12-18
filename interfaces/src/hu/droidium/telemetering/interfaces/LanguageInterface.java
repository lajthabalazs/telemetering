package hu.droidium.telemetering.interfaces;

public interface LanguageInterface {
	/**
	 * 
	 * @param message
	 * @param time Time of the question, used as current time
	 * @return
	 */
	public String getResponse(String message, long time);
}
