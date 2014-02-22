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
	    Key userKey = KeyFactory.createKey(Constants.DEVICES_TABLE_NAME, user.getEmail());
		Query query = new Query(Constants.DEVICES_TABLE_NAME, userKey).addSort(Constants.DEVICE_NAME, Query.SortDirection.ASCENDING);
		List<Entity> devices = datastore.prepare(query).asList(Builder.withLimit(100));
		if (devices.isEmpty()) {
%>
<p>You don't have any registered devices.</p>
<%

		} else {
%>
<p>Your devices:</p>
<table>
<tr>
<th>Device name</th>
</tr>
<%		
			for (Entity device : devices) {
				pageContext.setAttribute("name", device.getProperty(Constants.DEVICE_NAME));
				pageContext.setAttribute("id", device.getProperty(Constants.DEVICE_ID));
%>
<tr>
<td><a href="/jsp/dataview.jsp?deviceid=${fn:escapeXml(id)}">${fn:escapeXml(name)}</a></td>
</tr>
<%				
			}
%>
</table>
<%
		}
%>
<form action="/adddevice" method="post">
	<div><label>Device name<input type="text" name="devicename" cols="60"></input></label></div>
	<div><label>Device id<input type="text" name="deviceid" cols="60"></input></label></div>
	<div><input type="submit" value="Add device" /></div>
</form>  

<%
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