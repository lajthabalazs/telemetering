package test;

import static org.junit.Assert.*;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import hu.droidium.remote_home_manager.Datastore;
import hu.droidium.remote_home_manager.Measurement;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestDatastore {

	private static final String TEST_DATASTORE_FILE = "test_datastore.sqlite";
	@Before
	public void init() {
		File file = new File(TEST_DATASTORE_FILE);
		if (file.exists()) {
			file.delete();
		}
	}
	
	@After
	public void destroy() {
		/*
		File file = new File(TEST_DATASTORE_FILE);
		if (file.exists()) {
			file.delete();
		}
		*/
	}
	
	@Test
	public void testDatastoreInit() {
		new Datastore(TEST_DATASTORE_FILE);
		assertTrue(new File(TEST_DATASTORE_FILE).exists());
	}

	@Test
	public void testDatastoreWrite() {
		Datastore datastore = new Datastore(TEST_DATASTORE_FILE);
		boolean save = datastore.saveMeasuremet("temp", 35);
		assertTrue(save);
	}

	@Test
	public void testGetMeasurements() {
		Datastore datastore = new Datastore(TEST_DATASTORE_FILE);
		long startTime = System.currentTimeMillis();
		Random random = new Random();
		List<Long> values = new LinkedList<Long>();
		for (int i = 0; i < 100; i++) {
			long value = random.nextInt(30);
			datastore.saveMeasuremet("temp", value);
			values.add(value);
		}
		long endTime = System.currentTimeMillis();
		System.out.println("Data added");
		List<Measurement> result = datastore.getMeasurements("temp", startTime - 1, endTime + 1);
		assertEquals(100, result.size());
		for (int i = 0; i < values.size(); i++) {
			assertEquals(values.get(i), result.get(i).getValue());
			assertEquals("temp", result.get(i).getSensorId());
		}
	}

	
	@Test
	public void testFilterMeasurements() {
		Datastore datastore = new Datastore(TEST_DATASTORE_FILE);
		long startTime = System.currentTimeMillis();
		Random random = new Random();
		List<Long> values = new LinkedList<Long>();
		for (int i = 0; i < 100; i++) {
			long value = random.nextInt(30);
			datastore.saveMeasuremet("temp", value);
			values.add(value);
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		long endTime = System.currentTimeMillis();
		List<Measurement> result = datastore.getMeasurements("temp", startTime - 1, endTime + 1);
		assertEquals(100, result.size());
	}

}
