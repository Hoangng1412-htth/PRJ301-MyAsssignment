<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

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
                <td>${r.from}</td>

                <!-- Đến ngày -->
                <td>${r.to}</td>

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

                <!-- Chi tiết -->
                <td>
                    <form action="${pageContext.request.contextPath}/request/review"
                          method="get" style="display:inline;">
                        <input type="hidden" name="id" value="${r.id}">
                        <button type="submit" class="btn-action view">👁 Chi tiết</button>
                    </form>
                </td>
            </tr>
        </c:forEach>
    </tbody>
</table>

<style>
.request-table {
    width: 100%;
    border-collapse: collapse;
    background: #fff;
    margin-top: 20px;
    border-radius: 8px;
    box-shadow: 0 2px 6px rgba(0,0,0,0.1);
}

.request-table th {
    background-color: #ff8c1a;
    color: white;
    padding: 10px;
    text-align: left;
}

.request-table td {
    padding: 10px;
    border-bottom: 1px solid #eee;
}

.status {
    font-weight: bold;
    padding: 4px 10px;
    border-radius: 6px;
}

.status.inprogress { background-color: #fff3cd; color: #856404; }
.status.approved { background-color: #d4edda; color: #155724; }
.status.rejected { background-color: #f8d7da; color: #721c24; }

.btn-action {
    font-weight: bold;
    border: none;
    padding: 6px 12px;
    border-radius: 6px;
    cursor: pointer;
    color: white;
    background-color: #2a9d8f;
    transition: 0.3s;
}

.btn-action:hover { background-color: #21867a; }
</style>
