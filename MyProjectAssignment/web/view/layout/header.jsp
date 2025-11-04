<%@page contentType="text/html" pageEncoding="UTF-8"%>

<div class="header">
    <div class="header-right">
        <div class="user-section">
            <img src="${pageContext.request.contextPath}/img/avatar.jpg" class="user-img" alt="User">
            <div class="user-name">${sessionScope.auth.displayname}</div>
        </div>
    </div>
</div>
