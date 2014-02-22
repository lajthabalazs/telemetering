package hu.droidium.telemetering;

import java.io.IOException;
import java.util.Date;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

public class ReceiveMeasurementServlet extends HttpServlet {
	private static final long serialVersionUID = -7817381622879870467L;

	@SuppressWarnings("unused")
	private static final Logger log = Logger.getLogger(ReceiveMeasurementServlet.class.getName());

	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {

		String deviceId = req.getParameter(Constants.DEVICE_ID);
		String time = req.getParameter(Constants.TIME);
		String value = req.getParameter(Constants.VALUE);
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
		
		resp.setContentType("text/plain");
		resp.getWriter().println("OK");
	}
}