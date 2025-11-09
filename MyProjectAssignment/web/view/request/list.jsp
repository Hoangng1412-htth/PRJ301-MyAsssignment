<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>


<!-- ✅ Thông báo lỗi -->
<c:if test="${not empty sessionScope.flashError}">
    <div class="alert alert-error">
        <span class="alert-msg">${sessionScope.flashError}</span>
        <span class="alert-close" onclick="this.parentElement.remove()">×</span>
    </div>
    <c:remove var="flashError" scope="session"/>
</c:if>

<!-- ✅ Thông báo thành công -->
<c:if test="${not empty sessionScope.flashSuccess}">
    <div class="alert alert-success">
        <span class="alert-msg">${sessionScope.flashSuccess}</span>
        <span class="alert-close" onclick="this.parentElement.remove()">×</span>
    </div>
    <c:remove var="flashSuccess" scope="session"/>
</c:if>

<h2>Danh sách đơn nghỉ</h2>

<form method="get" action="${pageContext.request.contextPath}/request/list" class="filter-form">
    <input type="text" name="searchName" placeholder="Tìm theo tên..." value="${searchName}" />
    <input type="date" name="fromDate" value="${fromDate}" />
    <input type="date" name="toDate" value="${toDate}" />
    <select name="status">
        <option value="">Tất cả trạng thái</option>
        <option value="0" ${status == '0' ? 'selected' : ''}>In Progress</option>
        <option value="1" ${status == '1' ? 'selected' : ''}>Approved</option>
        <option value="2" ${status == '2' ? 'selected' : ''}>Rejected</option>
    </select>
   <c:if test="${isDirector}">
    <select name="division">
        <option value="">Tất cả phòng ban</option>
        <c:forEach var="divName" items="${divisions}">
            <option value="${divName}" ${division == divName ? 'selected' : ''}>${divName}</option>
        </c:forEach>
    </select>
</c:if>
    <button type="submit">🔍 Lọc</button>
</form>

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
                <td>${r.created_by.name}</td>

                <td>
                    <c:choose>
                        <c:when test="${r.created_by['div'] != null}">
                            ${r.created_by['div'].dname}
                        </c:when>
                        <c:otherwise>-</c:otherwise>
                    </c:choose>
                </td>

                <td>${r.from}</td>
                <td>${r.to}</td>

                <td><c:out value="${(r.to.time - r.from.time) / (1000*60*60*24) + 1}" /></td>

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
                    </c:choose>
                </td>

                <td>
                    <c:choose>
                        <c:when test="${r.processed_by != null}">
                            ${r.processed_by.name}
                        </c:when>
                        <c:otherwise>-</c:otherwise>
                    </c:choose>
                </td>

                <td>
                    <!-- Nếu không phải người tạo & không phải Director -->
                    <c:choose>
                        <c:when test="${sessionScope.auth.employee.id != r.created_by.id && !sessionScope.auth.roles.contains('Director')}">
                            <form action="${pageContext.request.contextPath}/request/review" method="get" style="display:inline;">
                                <input type="hidden" name="id" value="${r.id}">
                                <button type="submit" class="btn-action view">👁 Chi tiết</button>
                            </form>
                        </c:when>
                    </c:choose>

                    <!-- Nếu là người tạo -->
                    <c:if test="${sessionScope.auth.employee.id == r.created_by.id}">
                        <form action="${pageContext.request.contextPath}/request/view" method="get" style="display:inline;">
                            <input type="hidden" name="id" value="${r.id}">
                            <button type="submit" class="btn-action edit">✏️ Sửa</button>
                        </form>
                    </c:if>
                </td>
            </tr>
        </c:forEach>
    </tbody>
</table>


<!-- Bảng dữ liệu giữ nguyên -->


<style>
.page-btn {
  display:inline-block;
  margin:0 3px;
  padding:6px 10px;
  border:1px solid #ccc;
  border-radius:5px;
  text-decoration:none;
}
.page-btn.active {
  background:#ff8c1a;
  color:white;
  font-weight:bold;
}
</style>




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
.btn-action.edit {
    background-color: #f4a261;
}
.btn-action.edit:hover {
    background-color: #e76f51;
}

</style>
<style>
.alert {
    position: relative;
    width: 100%;
    max-width: 850px;
    margin: 20px auto;
    padding: 14px 20px;
    border-radius: 10px;
    font-weight: 600;
    box-shadow: 0 2px 6px rgba(0,0,0,0.1);
    text-align: center;
    animation: fadeIn 0.6s ease-in-out;
    transition: opacity 0.6s ease-in-out;
}

.alert-error {
    background-color: #fdecea;
    color: #b71c1c;
    border: 1px solid #f5c6cb;
}

.alert-success {
    background-color: #e8f5e9;
    color: #256029;
    border: 1px solid #c8e6c9;
}

/* ✨ Hiệu ứng xuất hiện */
@keyframes fadeIn {
    from { opacity: 0; transform: translateY(-10px); }
    to { opacity: 1; transform: translateY(0); }
}

/* ✨ Hiệu ứng biến mất */
.fade-out {
    opacity: 0;
    pointer-events: none;
}

/* Nút đóng (×) */
.alert-close {
    position: absolute;
    right: 14px;
    top: 8px;
    cursor: pointer;
    font-size: 18px;
    line-height: 1;
    color: inherit;
    opacity: 0.7;
    transition: opacity 0.2s;
}
.alert-close:hover {
    opacity: 1;
}

/* Responsive: thu nhỏ hợp lý trên mobile */
@media (max-width: 768px) {
    .alert {
        width: 90%;
        font-size: 14px;
    }
}
</style>

<!-- ✅ Script tự ẩn sau 3 giây -->
<script>
    setTimeout(() => {
        document.querySelectorAll('.alert').forEach(el => {
            el.classList.add('fade-out');
            setTimeout(() => el.remove(), 600); // Xóa khỏi DOM sau khi mờ dần xong
        });
    }, 3000);
</script>
<style>
/* 🎯 FORM LỌC */
.filter-form {
    display: flex;
    flex-wrap: wrap;
    align-items: center;
    gap: 12px;
    background: #fff;
    padding: 16px 22px;
    border-radius: 10px;
    box-shadow: 0 2px 6px rgba(0,0,0,0.08);
    margin-bottom: 22px;
    font-family: 'Segoe UI', sans-serif;
}

/* 🔸 INPUT + SELECT CHUNG */
.filter-form input[type="text"],
.filter-form input[type="date"],
.filter-form select {
    padding: 8px 10px;
    border: 1px solid #ccc;
    border-radius: 8px;
    font-size: 14px;
    transition: all 0.25s ease-in-out;
    background-color: #fafafa;
    min-width: 130px;
}

/* Khi focus */
.filter-form input[type="text"]:focus,
.filter-form input[type="date"]:focus,
.filter-form select:focus {
    border-color: #ff8c1a;
    box-shadow: 0 0 5px rgba(255,140,26,0.4);
    background-color: #fff;
    outline: none;
}

/* 🔹 Dropdown đặc biệt (phòng ban) */
.filter-form select[name="division"] {
    border: 1px solid #ffb84d;
    background-color: #fff9f3;
    color: #444;
    font-weight: 500;
}
.filter-form select[name="division"]:hover {
    background-color: #fff3e6;
    border-color: #ff8c1a;
}

/* 🔸 NÚT LỌC */
.filter-form button {
    background-color: #ff8c1a;
    color: white;
    font-weight: 600;
    border: none;
    border-radius: 8px;
    padding: 9px 16px;
    cursor: pointer;
    transition: all 0.3s ease;
    display: flex;
    align-items: center;
    gap: 6px;
    letter-spacing: 0.3px;
}

.filter-form button:hover {
    background-color: #e67a00;
    transform: translateY(-1px);
    box-shadow: 0 3px 6px rgba(255,140,26,0.4);
}

/* 🔸 Responsive (mobile view) */
@media (max-width: 768px) {
    .filter-form {
        flex-direction: column;
        align-items: stretch;
    }
    .filter-form input,
    .filter-form select,
    .filter-form button {
        width: 100%;
    }
}
</style>
