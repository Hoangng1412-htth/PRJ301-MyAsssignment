<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<div class="sidebar">

    <c:if test="${not empty sessionScope.features}">
        <c:forEach var="f" items="${sessionScope.features}">
            <a href="${pageContext.request.contextPath}${f.url}">
                ${f.fname}
            </a>
        </c:forEach>
    </c:if>

    <hr>

    <a href="${pageContext.request.contextPath}/logout" class="logout-btn">🚪 Sign Out</a>
</div>
