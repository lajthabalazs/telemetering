package test;

import static org.junit.Assert.*;

import java.io.File;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import hu.droidium.remote_home_manager.SQLJetDatastore;
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
		File file = new File(TEST_DATASTORE_FILE);
		if (file.exists()) {
			file.delete();
		}
	}
	
	@Test
	public void testDatastoreInit() {
		new SQLJetDatastore(TEST_DATASTORE_FILE);
		assertTrue(new File(TEST_DATASTORE_FILE).exists());
	}

	@Test
	public void testDatastoreWrite() {
		SQLJetDatastore datastore = new SQLJetDatastore(TEST_DATASTORE_FILE);
		boolean save = datastore.saveMeasurement("temp", 123241231l, 35);
		assertTrue(save);
	}

	@Test
	public void testGetMeasurements() {
		SQLJetDatastore datastore = new SQLJetDatastore(TEST_DATASTORE_FILE);
		long startTime = System.currentTimeMillis();
		Random random = new Random();
		List<Measurement> toInsert = new LinkedList<Measurement>();
		long time = startTime;
		for (int i = 0; i < 100000; i++) {
			toInsert.add( new Measurement("temp", time, (long)random.nextInt(30)));
			time += 50;
		}
		datastore.bulkInster(toInsert);
		List<Measurement> result = datastore.getMeasurements("temp", startTime - 1, time + 1);
		assertEquals(toInsert.size(), result.size());
		Iterator<Measurement> toInsertIterator = toInsert.iterator();
		int i = 0;
		for (Measurement m : result) {
			if (i++ % 1000 == 0) {
				System.out.println("Testing " + i);
			}
			Measurement inserted = toInsertIterator.next();
			assertEquals(inserted.getSensorId(), m.getSensorId());
			assertEquals(inserted.getValue(), m.getValue());
			assertEquals(inserted.getTime(), m.getTime());
		}
	}
}
