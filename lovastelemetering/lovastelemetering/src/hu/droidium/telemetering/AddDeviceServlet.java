package hu.droidium.telemetering;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

public class AddDeviceServlet extends HttpServlet {
	private static final long serialVersionUID = -7817381622879870467L;

	@SuppressWarnings("unused")
	private static final Logger log = Logger.getLogger(AddDeviceServlet.class.getName());

	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {

		UserService userService = UserServiceFactory.getUserService();
		User user = userService.getCurrentUser();
		if (user == null) {
			resp.setContentType("text/plain");
			resp.getWriter().println("ERROR: not logged in");
			return;
		}

		String deviceName = req.getParameter(Constants.DEVICE_NAME);
		String deviceId = req.getParameter(Constants.DEVICE_ID);
		if (deviceId == null) {
			resp.setContentType("text/plain");
			resp.getWriter().println("ERROR: no 'deviceid'.");
			return;
		}
		if (deviceName == null) {
			resp.setContentType("text/plain");
			resp.getWriter().println("ERROR: no 'devicename'");
			return;
		}
		
		// Save data to datastore
		Key userKey = KeyFactory.createKey(Constants.DEVICES_TABLE_NAME, user.getEmail());
        Entity device = new Entity(Constants.DEVICES_TABLE_NAME, userKey);
        device.setProperty(Constants.DEVICE_ID, deviceId);
        device.setProperty(Constants.DEVICE_NAME, deviceName);
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        datastore.put(device);
		resp.sendRedirect("/jsp/devicelist.jsp");
	}
}