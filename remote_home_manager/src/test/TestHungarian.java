package test;

import static org.junit.Assert.assertEquals;
import hu.droidium.remote_home_manager.HungarianLanguageModule;
import hu.droidium.remote_home_manager.SensorType;

import java.util.Calendar;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;

import test.MockDataStore.Call;
import test.MockDataStore.Method;

public class TestHungarian {
	
	private MockDataStore mockDataStore;
	private HungarianLanguageModule languageModule;
	
	@Before
	public void init() {
		mockDataStore = new MockDataStore();
		languageModule = new HungarianLanguageModule(mockDataStore);
	}
	
	private Calendar getCalendar() {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.YEAR, 2014);
		calendar.set(Calendar.MONTH, 5);
		calendar.set(Calendar.DAY_OF_MONTH, 12);
		calendar.set(Calendar.HOUR_OF_DAY, 12);
		calendar.set(Calendar.MINUTE, 35);
		calendar.set(Calendar.SECOND, 55);
		return calendar;
	}
	@Test
	public void testPresent() {
		Calendar calendar = getCalendar();
		long time = calendar.getTimeInMillis();
		languageModule.getResponse("Milyen meleg van a nappaliban?", time);
		checkCall(mockDataStore, Method.GET_LAST_MEASUREMENT, "a nappaliban", SensorType.TEMPERATURE);		
		languageModule.getResponse("Mennyire van meleg a nappaliban?", time);
		checkCall(mockDataStore, Method.GET_LAST_MEASUREMENT, "a nappaliban", SensorType.TEMPERATURE);
	}

	@Test
	public void testYesterday() {
		Calendar calendar = getCalendar();
		long time = calendar.getTimeInMillis();
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		long endTime = calendar.getTimeInMillis();
		long startTime = endTime - 24l * 3600l * 1000l;
		long window = 3600l * 1000l;
		languageModule.getResponse("Hány fok volt a nappaliban tegnap?", time);
		checkCall(mockDataStore, Method.GET_AVERAGES, "a nappaliban", SensorType.TEMPERATURE, startTime, endTime, window);		
		languageModule.getResponse("Hány fok volt tegnap a nappaliban?", time);
		checkCall(mockDataStore, Method.GET_AVERAGES, "a nappaliban", SensorType.TEMPERATURE, startTime, endTime, window);		
	}

	@Test
	public void testDayBeforeYesterday() {
		Calendar calendar = getCalendar();
		long time = calendar.getTimeInMillis();
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		calendar.set(Calendar.DAY_OF_MONTH, 10);
		long startTime = calendar.getTimeInMillis();
		calendar.set(Calendar.DAY_OF_MONTH, 11);
		long endTime = calendar.getTimeInMillis();
		long window = 3600l * 1000l;
		languageModule.getResponse("Mennyire volt meleg a nappaliban tegnapelőtt?", time);
		checkCall(mockDataStore, Method.GET_AVERAGES, "a nappaliban", SensorType.TEMPERATURE, startTime, endTime, window);		
		languageModule.getResponse("Milyen volt az idő tegnapelőtt a nappaliban?", time);
		checkCall(mockDataStore, Method.GET_AVERAGES, "a nappaliban", SensorType.TEMPERATURE, startTime, endTime, window);		
	}

	@Test
	public void testMonday() {
		Calendar calendar = getCalendar();
		long time = calendar.getTimeInMillis();
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		calendar.set(Calendar.DAY_OF_MONTH, 9);
		long startTime = calendar.getTimeInMillis();
		long endTime = startTime + 24l * 3600l * 1000l;
		long window = 3600l * 1000l;
		languageModule.getResponse("Mennyire volt hideg hétfőn a nappaliban?", time);
		checkCall(mockDataStore, Method.GET_AVERAGES, "a nappaliban", SensorType.TEMPERATURE, startTime, endTime, window);		
		languageModule.getResponse("Mennyire volt meleg a nappaliban hétfőn?", time);
		checkCall(mockDataStore, Method.GET_AVERAGES, "a nappaliban", SensorType.TEMPERATURE, startTime, endTime, window);		
	}

	@Test
	public void testWednesday() {
		Calendar calendar = getCalendar();
		long time = calendar.getTimeInMillis();
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		calendar.set(Calendar.DAY_OF_MONTH, 11);
		long startTime = calendar.getTimeInMillis();
		long endTime = startTime + 24l * 3600l * 1000l;
		long window = 3600l * 1000l;
		languageModule.getResponse("Mennyire volt hideg szerdán a nappaliban?", time);
		checkCall(mockDataStore, Method.GET_AVERAGES, "a nappaliban", SensorType.TEMPERATURE, startTime, endTime, window);		
		languageModule.getResponse("Mennyire volt meleg a nappaliban szerdán?", time);
		checkCall(mockDataStore, Method.GET_AVERAGES, "a nappaliban", SensorType.TEMPERATURE, startTime, endTime, window);		
	}

	@Test
	public void testThursday() {
		Calendar calendar = getCalendar();
		long time = calendar.getTimeInMillis();
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		calendar.set(Calendar.DAY_OF_MONTH, 5);
		long startTime = calendar.getTimeInMillis();
		long endTime = startTime + 24l * 3600l * 1000l;
		long window = 3600l * 1000l;
		languageModule.getResponse("Mennyire volt hideg csütörtökön a nappaliban?", time);
		checkCall(mockDataStore, Method.GET_AVERAGES, "a nappaliban", SensorType.TEMPERATURE, startTime, endTime, window);		
		languageModule.getResponse("Mennyire volt meleg a nappaliban csütörtökön?", time);
		checkCall(mockDataStore, Method.GET_AVERAGES, "a nappaliban", SensorType.TEMPERATURE, startTime, endTime, window);		
	}

	@Test
	public void testFriday() {
		Calendar calendar = getCalendar();
		long time = calendar.getTimeInMillis();
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		calendar.set(Calendar.DAY_OF_MONTH, 6);
		long startTime = calendar.getTimeInMillis();
		long endTime = startTime + 24l * 3600l * 1000l;
		long window = 3600l * 1000l;
		languageModule.getResponse("Mennyire volt hideg pénteken a nappaliban?", time);
		checkCall(mockDataStore, Method.GET_AVERAGES, "a nappaliban", SensorType.TEMPERATURE, startTime, endTime, window);		
		languageModule.getResponse("Mennyire volt meleg a nappaliban pénteken?", time);
		checkCall(mockDataStore, Method.GET_AVERAGES, "a nappaliban", SensorType.TEMPERATURE, startTime, endTime, window);		
	}


	@Test
	public void testWeek() {
		Calendar calendar = getCalendar();
		long time = calendar.getTimeInMillis();
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		calendar.set(Calendar.DAY_OF_MONTH, 9);
		long startTime = calendar.getTimeInMillis();
		long endTime = time;
		long window = 24l * 3600l * 1000l;		
		languageModule.getResponse("Mennyire volt hideg a héten a nappaliban?", time);
		checkCall(mockDataStore, Method.GET_AVERAGES, "a nappaliban", SensorType.TEMPERATURE, startTime, endTime, window);		
		languageModule.getResponse("Mennyire volt hideg a nappaliban a héten?", time);
		checkCall(mockDataStore, Method.GET_AVERAGES, "a nappaliban", SensorType.TEMPERATURE, startTime, endTime, window);		
	}

	@Test
	public void testPastWeek() {
		Calendar calendar = getCalendar();
		long time = calendar.getTimeInMillis();
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		calendar.set(Calendar.DAY_OF_MONTH, 2);
		long startTime = calendar.getTimeInMillis();
		long endTime = startTime + 7l * 24l * 3600l * 1000l;
		long window = 24l * 3600l * 1000l;		
		languageModule.getResponse("Mennyire volt hideg múlt héten a nappaliban?", time);
		checkCall(mockDataStore, Method.GET_AVERAGES, "a nappaliban", SensorType.TEMPERATURE, startTime, endTime, window);		
		languageModule.getResponse("Mennyire volt hideg a múlt héten a nappaliban?", time);
		checkCall(mockDataStore, Method.GET_AVERAGES, "a nappaliban", SensorType.TEMPERATURE, startTime, endTime, window);		
	}
	
	@Test
	public void testDate() {
		Calendar calendar = getCalendar();
		long time = calendar.getTimeInMillis();
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		calendar.set(Calendar.YEAR, 2012);
		calendar.set(Calendar.MONTH, 11);
		calendar.set(Calendar.DAY_OF_MONTH, 24);
		long startTime = calendar.getTimeInMillis();
		long endTime = startTime + 24l * 3600l * 1000l;
		long window = 3600l * 1000l;
		languageModule.getResponse("Mennyire volt hideg 2012.12.24-én a nappaliban?", time);
		checkCall(mockDataStore, Method.GET_AVERAGES, "a nappaliban", SensorType.TEMPERATURE, startTime, endTime, window);		
		languageModule.getResponse("Mennyire volt hideg 2012/12/24-án a nappaliban?", time);
		checkCall(mockDataStore, Method.GET_AVERAGES, "a nappaliban", SensorType.TEMPERATURE, startTime, endTime, window);		
	}
	
	@Test
	public void testShortHourToday() {
		Calendar calendar = getCalendar();
		long time = calendar.getTimeInMillis();
		calendar.set(Calendar.HOUR_OF_DAY, 11);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		long startTime = calendar.getTimeInMillis() - 1800l * 1000l;
		long endTime = startTime + 3600l * 1000l;
		languageModule.getResponse("Mennyire volt hideg 11-kor a nappaliban?", time);
		checkCall(mockDataStore, Method.GET_MEASUREMENTS, "a nappaliban", SensorType.TEMPERATURE, startTime, endTime);		
	}

	@Test
	public void testShortHourYesterday() {
		Calendar calendar = getCalendar();
		long time = calendar.getTimeInMillis();
		calendar.set(Calendar.HOUR_OF_DAY, 17);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		long startTime = calendar.getTimeInMillis() - 24l * 3600l * 1000l - 1800l * 1000l;
		long endTime = startTime + 3600l * 1000l;
		languageModule.getResponse("Mennyire volt hideg 17-kor a nappaliban?", time);
		checkCall(mockDataStore, Method.GET_MEASUREMENTS, "a nappaliban", SensorType.TEMPERATURE, startTime, endTime);		
	}

	@Test
	public void testLongHourToday() {
		Calendar calendar = getCalendar();
		long time = calendar.getTimeInMillis();
		calendar.set(Calendar.HOUR_OF_DAY, 11);
		calendar.set(Calendar.MINUTE, 30);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		long startTime = calendar.getTimeInMillis() - 3600l * 1000l / 8;
		System.out.println(new Date(startTime));
		long endTime = startTime + 3600l * 1000l / 4;
		System.out.println(new Date(endTime));
		languageModule.getResponse("Mennyire volt hideg 11:30-kor a nappaliban?", time);
		checkCall(mockDataStore, Method.GET_MEASUREMENTS, "a nappaliban", SensorType.TEMPERATURE, startTime, endTime);		
	}

	public void testLongHourTodayJustMinutes() {
		Calendar calendar = getCalendar();
		long time = calendar.getTimeInMillis();
		calendar.set(Calendar.HOUR_OF_DAY, 11);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		long startTime = calendar.getTimeInMillis() - 3600l * 1000l / 8;
		long endTime = startTime + 3600l * 1000l / 4;
		languageModule.getResponse("Mennyire volt hideg 13:12-kor a nappaliban?", time);
		checkCall(mockDataStore, Method.GET_MEASUREMENTS, "a nappaliban", SensorType.TEMPERATURE, startTime, endTime);		
	}

	@Test
	public void testLongHourYesterday() {
		Calendar calendar = getCalendar();
		long time = calendar.getTimeInMillis();
		calendar.set(Calendar.DAY_OF_MONTH, 11);
		calendar.set(Calendar.HOUR_OF_DAY, 17);
		calendar.set(Calendar.MINUTE, 25);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		long startTime = calendar.getTimeInMillis() - 3600l * 1000l / 8;
		long endTime = startTime + 3600l * 1000l / 4;
		languageModule.getResponse("Mennyire volt hideg 17:25-kor a nappaliban?", time);
		checkCall(mockDataStore, Method.GET_MEASUREMENTS, "a nappaliban", SensorType.TEMPERATURE, startTime, endTime);		
	}

	@Test
	public void testLongHourYesterdayJustMinutes() {
		Calendar calendar = getCalendar();
		long time = calendar.getTimeInMillis();
		calendar.set(Calendar.DAY_OF_MONTH, 11);
		calendar.set(Calendar.HOUR_OF_DAY, 13);
		calendar.set(Calendar.MINUTE, 55);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		long startTime = calendar.getTimeInMillis() - 3600l * 1000l / 8;
		long endTime = startTime + 3600l * 1000l / 4;
		languageModule.getResponse("Mennyire volt hideg 13:55-kor a nappaliban?", time);
		checkCall(mockDataStore, Method.GET_MEASUREMENTS, "a nappaliban", SensorType.TEMPERATURE, startTime, endTime);		
	}

	private void checkCall(Call call, Method method, String location, SensorType type) {
		assertEquals(method, call.method);
		assertEquals(location, call.location);
		assertEquals(type, call.type);
	}

	private void checkCall(Call call, Method method, String location, SensorType type, long startTime, long endTime) {
		checkCall(call, method, location, type);
		assertEquals(startTime, call.startTime);
		assertEquals(endTime, call.endTime);
	}

	private void checkCall(Call call, Method method, String location, SensorType type, long startTime, long endTime, long window) {
		checkCall(call, method, location, type, startTime, endTime);
		assertEquals(window, call.window);
	}

	private void checkCall(MockDataStore mockDataStore, Method method, String location, SensorType type) {
		assertEquals(1, mockDataStore.getCalls().size());
		Call call = mockDataStore.getCalls().get(0);
		checkCall(call, method, location, type);
		mockDataStore.clear();
	}

	private void checkCall(MockDataStore mockDataStore, Method method, String location, SensorType type, long startTime, long endTime) {
		assertEquals(1, mockDataStore.getCalls().size());
		Call call = mockDataStore.getCalls().get(0);
		checkCall(call, method, location, type, startTime, endTime);
		mockDataStore.clear();
	}

	private void checkCall(MockDataStore mockDataStore,  Method method, String location, SensorType type, long startTime, long endTime, long window) {
		assertEquals(1, mockDataStore.getCalls().size());
		Call call = mockDataStore.getCalls().get(0);
		checkCall(call, method, location, type, startTime, endTime, window);
		mockDataStore.clear();
	}
}