<%-- 
    Document   : index
    Created on : Nov 6, 2025, 5:46:08 AM
    Author     : ng hoang
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>JSP Page</title>
    </head>
    <body>
       <%
response.sendRedirect(request.getContextPath() + "/login");
%>

    </body>
</html>
