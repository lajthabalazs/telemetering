<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<html>
	<body>
		<form action="/receivemeasurement" method="post">
			<div><input type="text" name="deviceid" cols="60"></input></div>
			<div><input type="text" name="time" cols="60"></input></div>
			<div><input type="text" name="value" cols="60"></input></div>
			<div><input type="submit" value="Post data" /></div>
		</form>  
	</body>
</html>