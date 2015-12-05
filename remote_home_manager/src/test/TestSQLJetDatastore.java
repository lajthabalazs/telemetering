package test;

import static org.junit.Assert.*;

import java.io.File;

import hu.droidium.remote_home_manager.database.SQLJetDatastore;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestSQLJetDatastore extends TestDatastore {

	private static final String TEST_DATASTORE_FILE = "test_datastore.sqlite";
	@Before
	public void init() {
		File file = new File(TEST_DATASTORE_FILE);
		if (file.exists()) {
			file.delete();
		}
		datastore = new SQLJetDatastore(TEST_DATASTORE_FILE);
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
		assertTrue(new File(TEST_DATASTORE_FILE).exists());
	}
}
