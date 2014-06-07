package hu.droidium.telemetering;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.jsr107cache.Cache;
import net.sf.jsr107cache.CacheException;
import net.sf.jsr107cache.CacheFactory;
import net.sf.jsr107cache.CacheManager;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.jsr107cache.GCacheFactory;

public class ReceiveMeasurementServlet extends HttpServlet {
	private static final long serialVersionUID = -7817381622879870467L;

	@SuppressWarnings("unused")
	private static final Logger log = Logger.getLogger(ReceiveMeasurementServlet.class.getName());

	private static final long PERSIST_DELTA = 1800; // Delay between two persistent writes

	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {

		String deviceId = req.getParameter(Constants.DEVICE_ID);
		String time = req.getParameter(Constants.TIME);
		String value = req.getParameter(Constants.VALUE);
		long now = System.currentTimeMillis();
		if (deviceId == null) {
			resp.setContentType("text/plain");
			resp.getWriter().println("ERROR");
			return;
		}
		if (time == null) {
			resp.setContentType("text/plain");
			resp.getWriter().println("ERROR");
			return;
		}
		if (value == null) {
			resp.setContentType("text/plain");
			resp.getWriter().println("ERROR");
			return;
		}
		Cache cache = null;

        Map<Object, Object> props = new HashMap<Object, Object>();
        props.put(GCacheFactory.EXPIRATION_DELTA, 3600);
        props.put(MemcacheService.SetPolicy.ADD_ONLY_IF_NOT_PRESENT, true);
        try {
            CacheFactory cacheFactory = CacheManager.getInstance().getCacheFactory();
            cache = cacheFactory.createCache(props);
        } catch (CacheException e) {
        	cache = null;
        }
        if (cache != null) {
        	if (cache.containsKey(deviceId)) {
        		CachedSensorData cachedData = (CachedSensorData)cache.get(deviceId);
        		if (now - cachedData.lastWritten < PERSIST_DELTA) {
        			saveData(deviceId, time, value);
        		}
        		// Update cached data
        		cachedData.update(now, value);
    			cache.put(deviceId, cachedData);
        		Calendar calendar = Calendar.getInstance();
        		int min = calendar.get(Calendar.MINUTE);
        		cache.put(deviceId + " " + min, value);
        	}
        } else {
        	saveData(deviceId, time, value);
        }
		
		resp.setContentType("text/plain");
		resp.getWriter().println("OK");
	}
	
	private void saveData(String deviceId, String time, String value) {
		// Save data to datastore
		Key deivceKey = KeyFactory.createKey(Constants.READING_TABLE_NAME, deviceId);
        Date date = new Date();
        Entity reading = new Entity(Constants.READING_TABLE_NAME, deivceKey);
        reading.setProperty(Constants.DEVICE_ID, deviceId);
        reading.setProperty(Constants.TIME, time);
        reading.setProperty(Constants.DATE, date);
        reading.setProperty(Constants.VALUE, value);
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        datastore.put(reading);
	}
}