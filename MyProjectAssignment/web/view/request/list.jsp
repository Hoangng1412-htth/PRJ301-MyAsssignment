<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<h2>Danh sách đơn nghỉ</h2>

<table class="request-table">
    <thead>
        <tr>
            <th>Người gửi đơn</th>
            <th>Phòng ban</th>
            <th>Từ ngày</th>
            <th>Đến ngày</th>
            <th>Thời lượng (ngày)</th>
            <th>Trạng thái</th>
            <th>Người duyệt</th>
            <th>Chi tiết</th>
        </tr>
    </thead>
    <tbody>
        <c:forEach var="r" items="${requests}">
            <tr>
                <!-- Người gửi -->
                <td>${r.created_by.name}</td>

                <!-- Phòng ban -->
                <td>
                    <c:choose>
                        <c:when test="${r.created_by['div'] != null}">
                            ${r.created_by['div'].dname}
                        </c:when>
                        <c:otherwise>-</c:otherwise>
                    </c:choose>
                </td>

                <!-- Từ ngày -->
                <td><fmt:formatDate value="${r.from}" pattern="yyyy-MM-dd" /></td>

                <!-- Đến ngày -->
                <td><fmt:formatDate value="${r.to}" pattern="yyyy-MM-dd" /></td>

                <!-- Thời lượng -->
                <td>
                    <c:out value="${(r.to.time - r.from.time) / (1000*60*60*24) + 1}" />
                </td>

                <!-- Trạng thái -->
                <td>
                    <c:choose>
                        <c:when test="${r.status == 0}">
                            <span class="status inprogress">In Progress</span>
                        </c:when>
                        <c:when test="${r.status == 1}">
                            <span class="status approved">Approved</span>
                        </c:when>
                        <c:when test="${r.status == 2}">
                            <span class="status rejected">Rejected</span>
                        </c:when>
                        <c:otherwise>-</c:otherwise>
                    </c:choose>
                </td>

                <!-- Người duyệt -->
                <td>
                    <c:choose>
                        <c:when test="${r.processed_by != null}">
                            ${r.processed_by.name}
                        </c:when>
                        <c:otherwise>-</c:otherwise>
                    </c:choose>
                </td>

                <!-- Chi tiết và sửa -->
                <td>
                    <!-- Hiển thị nút Chi tiết -->
                    <form action="${pageContext.request.contextPath}/request/view" method="get" style="display:inline;">
                        <input type="hidden" name="id" value="${r.id}">
                        <button type="submit" class="btn-action view">👁 Chi tiết</button>
                    </form>

                    <!-- Hiển thị nút Sửa -->
                    <form action="${pageContext.request.contextPath}/request/view" method="get" style="display:inline;">
                        <input type="hidden" name="id" value="${r.id}">
                        <button type="submit" class="btn-action edit">✏️ Sửa</button>
                    </form>

                    <!-- Nếu không có quyền, hiển thị thông báo -->
                    <c:choose>
                        <c:when test="${r.status == 1}">
                            <div class="message">❌ Bạn không thể sửa đơn đã duyệt!</div>
                        </c:when>
                        <c:when test="${r.created_by.id == sessionScope.auth.employee.id}">
                            <div class="message">❌ Bạn không thể duyệt đơn của chính mình!</div>
                        </c:when>
                    </c:choose>
                </td>
            </tr>
        </c:forEach>
    </tbody>
</table>

<style>
/* Định dạng nút */
.btn-action {
    font-weight: bold;
    border: none;
    padding: 6px 12px;
    border-radius: 6px;
    cursor: pointer;
    color: white;
    transition: background-color 0.3s;
    margin-right: 4px;
}
.btn-action.view {
    background-color: #0077b6;
}
.btn-action.view:hover {
    background-color: #005f8d;
}
.btn-action.edit {
    background-color: #ffb703;
    color: #333;
}
.btn-action.edit:hover {
    background-color: #e0a800;
}
.message {
    font-weight: bold;
    color: #721c24;
    background-color: #f8d7da;
    padding: 10px;
    border-radius: 6px;
    margin-top: 10px;
}
</style>
