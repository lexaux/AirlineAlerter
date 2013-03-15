<%@ page import="com.augmentari.airline.alerter.servlet.EntryPoint" %>
<%@ page import="com.mongodb.DB" %>
<%@ page import="com.mongodb.DBCursor" %>
<%@ page import="com.mongodb.DBObject" %>
<%@ page import="org.joda.time.format.ISODateTimeFormat" %>
<%@ page import="java.util.Date" %>
<%@ page import="org.joda.time.DateTime" %>
<%@ page import="org.joda.time.format.DateTimeFormatter" %>
<%@ page contentType="application/json;charset=UTF-8" language="java" %>
[
[null, null]
<%
    DateTimeFormatter fmt = ISODateTimeFormat.dateTime();

    DB db = EntryPoint.getDB();
    DBCursor cursor = db.getCollection("wizzair").find();
    while (cursor.hasNext())
    {
        DBObject obj = cursor.next();
        String stringPrice = obj.get("price").toString();
        float price = 0;
        if (stringPrice != null && !stringPrice.isEmpty())
        {
            price = Float.valueOf(obj.get("price").toString().substring(3));
        }

        Date date = (Date) obj.get("recording_date");
        DateTime dt = new DateTime(date);
%>

,["<%=fmt.print(dt)%>",<%=price%>]
<%
    }
%>
]