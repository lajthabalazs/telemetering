package test;

import hu.droidium.remote_home_manager.HungarianLanguageModule;
import hu.droidium.remote_home_manager.SensorInterface;

import org.junit.Before;
import org.junit.Test;

public class TestHungarian {
	
	private SensorInterface mockDataStore;
	private HungarianLanguageModule languageModule;
	
	@Before
	public void init() {
		mockDataStore = new MockDataStore();
		languageModule = new HungarianLanguageModule(mockDataStore);
	}
	
	@Test
	public void testPresent() {
		languageModule.getResponse("Milyen meleg van a nappaliban?");
		languageModule.getResponse("Mennyire van meleg a nappaliban?");
	}

	@Test
	public void testPastDays() {
		languageModule.getResponse("Hány fok volt a nappaliban tegnap?");
		languageModule.getResponse("Hány fok volt tegnap a nappaliban?");
		languageModule.getResponse("Mennyire volt meleg a nappaliban tegnap előtt?");
		languageModule.getResponse("Milyen volt az idő tegnap a nappaliban?");
		languageModule.getResponse("Mennyire volt hideg tegnap a nappaliban?");
	}

	@Test
	public void testPastDaysOfWeek() {
		languageModule.getResponse("Mennyire volt hideg szerdán a nappaliban?");
	}

	@Test
	public void testPastWeeks() {
		languageModule.getResponse("Mennyire volt hideg múlt héten a nappaliban?");
	}
	
	@Test
	public void testPastTime() {
		languageModule.getResponse("Mennyire volt hideg 11-kor a nappaliban?");
		languageModule.getResponse("Mennyire volt hideg 11:15-kor a nappaliban?");
	}

	@Test
	public void testPastDate() {
		languageModule.getResponse("Mennyire volt hideg 2012.12.24-én a nappaliban?");
		languageModule.getResponse("Mennyire volt hideg 2012/12/24-án a nappaliban?");
	}
}