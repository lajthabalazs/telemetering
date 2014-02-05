<%@page import="com.google.appengine.api.datastore.FetchOptions.Builder"%>
<%@page import="com.google.appengine.api.datastore.Key"%>
<%@page import="com.google.appengine.api.datastore.DatastoreServiceFactory"%>
<%@page import="com.google.appengine.api.datastore.Query"%>
<%@page import="com.google.appengine.api.datastore.KeyFactory"%>
<%@page import="com.google.appengine.api.datastore.DatastoreService"%>
<%@page import="com.google.appengine.api.datastore.Entity"%>
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
		// User is logged in
		String deviceId = request.getParameter(Constants.DEVICE_ID);
	    if (deviceId == null) {
	    	deviceId = "default";
	    }
	    pageContext.setAttribute("deviceId", deviceId);
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
	    Key deviceKey = KeyFactory.createKey(Constants.READING_TABLE_NAME, deviceId);
		// Run an ancestor query to ensure we see the most up-to-date
		// view of the Greetings belonging to the selected Guestbook.
		Query query = new Query(Constants.READING_TABLE_NAME, deviceKey).addSort(Constants.DATE, Query.SortDirection.DESCENDING);
		List<Entity> readings = datastore.prepare(query).asList(Builder.withLimit(5));
		if (readings.isEmpty()) {
%>
<p>Device '${fn:escapeXml(deviceId)}' has no readings.</p>
<%

		} else {
%>
<p>Last 5 values of '${fn:escapeXml(deviceId)}'.</p>
<table>
<tr>
<th>Time</th>
<th>Value</th>
</tr>
<%		
			for (Entity reading : readings) {
				pageContext.setAttribute("date", reading.getProperty(Constants.DATE));
				pageContext.setAttribute("time", reading.getProperty(Constants.TIME));
				pageContext.setAttribute("value", reading.getProperty(Constants.VALUE));
%>
<tr>
<td>${fn:escapeXml(time)}</td>
<td>${fn:escapeXml(value)}</td>
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
to include your name with greetings you post.</p>
<%
    }
%>

  </body>
</html>