<%@ page import="com.augmentari.airline.alerter.servlet.EntryPoint" %>
<%@ page import="com.mongodb.DB" %>
<%@ page import="com.mongodb.DBCursor" %>
<%@ page import="com.mongodb.DBObject" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="x" uri="http://java.sun.com/jstl/xml" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>

<html>
<body>
<%
    DB db = EntryPoint.getDB();
    DBCursor cursor = db.getCollection("wizzair").find();
%>
<h2>Wizzair recordings:</h2>
<table>
    <%
        while (cursor.hasNext())
        {
            DBObject obj = cursor.next();
    %>
    <tr>
        <td>
            <%=obj.get("recording_date")%>
        </td>
        <td>
            <%=obj.get("price")%>
        </td>
    </tr>
    <%
        }
    %>
</table>
</body>
</html>
