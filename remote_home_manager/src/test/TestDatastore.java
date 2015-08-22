package test;

import static org.junit.Assert.*;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import hu.droidium.telemetering.interfaces.MeasurementStoreInterface;
import hu.droidium.telemetering.interfaces.Measurement;
import hu.droidium.telemetering.interfaces.SensorType;

import org.junit.Test;

public abstract class TestDatastore {
	
	MeasurementStoreInterface datastore;

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

	@Test
	public void testGetAverage() {
		long startTime = 1000;
		long endTime = 2000;
		long total = 0;
		long measured = 0;
		int measurementCount = 0;
		List<Measurement> toInsert = new LinkedList<Measurement>();
		
		for (int i = 0; i < 101; i++) {
			// Measurements before start time
			toInsert.add( new Measurement("sarok", SensorType.TEMPERATURE, (long) i, 1000l));
		}
		
		for (int i = 0; i < 101; i++) {
			// Measurements in range
			measured = i;
			toInsert.add( new Measurement("sarok", SensorType.TEMPERATURE, (long)(i + 1500), measured));
			total = total + measured;
			measurementCount ++;
		}
		
		for (int i = 0; i < 101; i++) {
			// Measurements after end time
			toInsert.add( new Measurement("sarok", SensorType.TEMPERATURE, (long)(i + 3000),(long) i + 1000));
		}
		long expected = total / measurementCount;
		datastore.bulkInster(toInsert);
		Measurement result = datastore.getMeasurementAverage("sarok", SensorType.TEMPERATURE, startTime - 1, endTime + 1);
		System.out.println("Expected " + expected + ", received: " + result.getValue());		
		assertEquals(expected, result.getValue());
	}
}