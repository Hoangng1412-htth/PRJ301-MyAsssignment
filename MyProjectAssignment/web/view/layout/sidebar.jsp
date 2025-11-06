<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<div class="sidebar">

    <!-- Danh sách chức năng theo quyền -->
    <c:if test="${not empty sessionScope.auth.roles}">
        <!-- Tạo 1 danh sách featureId đã hiển thị để tránh trùng -->
        <c:set var="shownFeatures" value="" />

        <c:forEach var="role" items="${sessionScope.auth.roles}">
            <c:forEach var="feature" items="${role.features}">
                 <c:if test="${feature.url ne '/request/review' and feature.url ne '/request/view'}">
                <!-- Nếu feature chưa được hiển thị -->
               <c:if test="${not fn:contains(shownFeatures, feature.id)}">
    <a href="${pageContext.request.contextPath}${feature.url}">
        ${feature.fname}
    </a>
    <c:set var="shownFeatures" value="${shownFeatures},${feature.id}" />
</c:if>
                </c:if>

            </c:forEach>
        </c:forEach>
    </c:if>

    <hr>

    <!-- Nút Sign Out -->
    <a href="${pageContext.request.contextPath}/logout" class="logout-btn">🚪 Sign Out</a>
</div>
