<%@page import="java.util.Locale"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.util.Date"%>
<%@page import="com.google.appengine.api.datastore.Query.Filter"%>
<%@page import="com.google.appengine.api.datastore.Query.FilterOperator"%>
<%@page import="com.google.appengine.api.datastore.FetchOptions.Builder"%>
<%@page import="com.google.appengine.api.datastore.Key"%>
<%@page import="com.google.appengine.api.datastore.DatastoreServiceFactory"%>
<%@page import="com.google.appengine.api.datastore.Query"%>
<%@page import="com.google.appengine.api.datastore.KeyFactory"%>
<%@page import="com.google.appengine.api.datastore.DatastoreService"%>
<%@page import="com.google.appengine.api.datastore.Entity"%>
<%@page import="com.google.appengine.api.datastore.Query.FilterPredicate"%>
<%@page import="hu.droidium.telemetering.Constants"%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="com.google.appengine.api.users.User" %>
<%@ page import="com.google.appengine.api.users.UserService" %>
<%@ page import="com.google.appengine.api.users.UserServiceFactory" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<html>

  <body>

<%
	UserService userService = UserServiceFactory.getUserService();
	User user = userService.getCurrentUser();
	if (user != null) {
		pageContext.setAttribute("user", user);
%>
<p>Hello, ${fn:escapeXml(user.nickname)}! (You can
<a href="<%= userService.createLogoutURL(request.getRequestURI()) %>">sign out</a>.)</p>
<%
		int limit = 20;
		// User is logged in
		String deviceId = request.getParameter(Constants.DEVICE_ID);
		String deviceName = "default";
	    if (deviceId == null) {
	    	deviceId = "default";
	    } else {
			DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		    Key userKey = KeyFactory.createKey(Constants.DEVICES_TABLE_NAME, user.getEmail());
		    Filter deviceFilter = new FilterPredicate(Constants.DEVICE_ID, FilterOperator.EQUAL, deviceId);

			Query query = new Query(Constants.DEVICES_TABLE_NAME, userKey).setFilter(deviceFilter);
			List<Entity> devices = datastore.prepare(query).asList(Builder.withLimit(1));
			if (devices.size() > 0) {
				deviceName = (String)devices.get(0).getProperty(Constants.DEVICE_NAME);
			}
	    }
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
	    Key deviceKey = KeyFactory.createKey(Constants.READING_TABLE_NAME, deviceId);
		// Run an ancestor query to ensure we see the most up-to-date
		// view of the Greetings belonging to the selected Guestbook.
		Query query = new Query(Constants.READING_TABLE_NAME, deviceKey).addSort(Constants.DATE, Query.SortDirection.DESCENDING);
		List<Entity> readings = datastore.prepare(query).asList(Builder.withLimit(limit));
		if (readings.isEmpty()) {
%>
<p>Device '<%=deviceName%>' has no readings.</p>
<%

		} else {
%>
<p>Last <%=limit %> values of '<%=deviceName%>'.</p>
<table>
<tr>
<th>Received date</th>
<th>Measurement time</th>
<th>Value</th>
</tr>
<%		
			SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault());
			for (Entity reading : readings) {
				String date = format.format((Date)reading.getProperty(Constants.DATE));
				String time = format.format(new Date(Long.parseLong((String)reading.getProperty(Constants.TIME))));
				String value = (String)reading.getProperty(Constants.VALUE);
%>
<tr>
<td><%=date%></td>
<td><%=time%></td>
<td><%=value%></td>
</tr>
<%				
			}
%>
</table>
<%
		}
	} else {
%>
<p>Hello!
<a href="<%= userService.createLoginURL(request.getRequestURI()) %>">Sign in</a>
to view your devices.</p>
<%
    }
%>

  </body>
</html>