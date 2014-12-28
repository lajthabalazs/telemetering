package test;

import static org.junit.Assert.*;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import hu.droidium.telemetering.interfaces.DatastoreInterface;
import hu.droidium.telemetering.interfaces.Measurement;
import hu.droidium.telemetering.interfaces.SensorType;

import org.junit.Test;

public abstract class TestDatastore {
	
	DatastoreInterface datastore;

	@Test
	public void testDatastoreWrite() {
		boolean save = datastore.saveMeasurement("sarok", SensorType.TEMPERATURE, 123241231l, 35);
		assertTrue(save);
	}

	@Test
	public void testGetMeasurements() {
		long startTime = System.currentTimeMillis();
		Random random = new Random();
		List<Measurement> toInsert = new LinkedList<Measurement>();
		long time = startTime;
		for (int i = 0; i < 100000; i++) {
			toInsert.add( new Measurement("sarok", SensorType.TEMPERATURE, time, (long)random.nextInt(30)));
			time += 50;
		}
		datastore.bulkInster(toInsert);
		List<Measurement> result = datastore.getMeasurements("sarok", SensorType.TEMPERATURE, startTime - 1, time + 1);
		assertEquals(toInsert.size(), result.size());
		Iterator<Measurement> toInsertIterator = toInsert.iterator();
		int i = 0;
		System.out.println("Results " + result.size());
		for (Measurement m : result) {
			if (i % 1000 == 0) {
				System.out.println("Testing " + i);
			}
			i++;
			Measurement inserted = toInsertIterator.next();
			assertEquals(inserted.getLocation(), m.getLocation());
			assertEquals(inserted.getType(), m.getType());
			assertEquals(inserted.getValue(), m.getValue());
			assertEquals(inserted.getTime(), m.getTime());
		}
	}
}