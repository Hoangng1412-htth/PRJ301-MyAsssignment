<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@page import="java.time.LocalDate"%>
<%
    String today = LocalDate.now().toString();
%>

<h2>Chỉnh sửa đơn nghỉ</h2>

<form action="${pageContext.request.contextPath}/request/view" method="post">
    <input type="hidden" name="id" value="${request.id}" />

    <!-- Loại nghỉ -->
    <div class="form-group">
        <label>Loại nghỉ:</label>
        <select name="type" required>
            <option value="">-- Chọn loại nghỉ --</option>
            <option value="personal" ${request.type eq 'personal' ? 'selected' : ''}>Nghỉ việc cá nhân</option>
            <option value="sick" ${request.type eq 'sick' ? 'selected' : ''}>Nghỉ ốm</option>
            <option value="vacation" ${request.type eq 'vacation' ? 'selected' : ''}>Nghỉ phép</option>
        </select>
    </div>

    <!-- Từ ngày -->
    <div class="form-group">
        <label>Từ ngày:</label>
        <input type="date" name="from"
               value="<fmt:formatDate value='${request.from}' pattern='yyyy-MM-dd'/>"
               min="<%=today%>" required />
    </div>

    <!-- Đến ngày -->
    <div class="form-group">
        <label>Đến ngày:</label>
        <input type="date" name="to"
               value="<fmt:formatDate value='${request.to}' pattern='yyyy-MM-dd'/>"
               min="<%=today%>" required />
    </div>

    <!-- Lý do nghỉ -->
    <div class="form-group">
        <label>Lý do nghỉ:</label>
        <textarea name="reason" required>${request.reason}</textarea>
    </div>

    <!-- Thông báo -->
    <c:if test="${not empty msg}">
        <div class="message 
            <c:choose>
                <c:when test="${msg.startsWith('✅')}">success</c:when>
                <c:when test="${msg.startsWith('⚠️')}">warning</c:when>
                <c:otherwise>error</c:otherwise>
            </c:choose>
        ">${msg}</div>
    </c:if>

    <!-- Nút hành động -->
  <!-- Nút hành động: Hiển thị nút "Cập nhật" chỉ khi trạng thái là "chờ duyệt" (status = 0) -->
    <c:choose>
        <c:when test="${request.status == 0}">
            <!-- Nút cập nhật chỉ hiển thị khi trạng thái là 'chờ duyệt' (status = 0) -->
            <div class="form-actions">
                <button type="submit" name="action" value="update" class="btn btn-update">💾 Cập nhật</button>
                <button type="button" class="btn btn-delete" onclick="openModal();">🗑 Xóa đơn</button>
            </div>
        </c:when>
        <c:otherwise>
            <!-- Nếu trạng thái không phải 'chờ duyệt', chỉ hiển thị nút "Xóa" -->
            <div class="form-actions">
                <button type="button" class="btn btn-delete" onclick="openModal();">🗑 Xóa đơn</button>
            </div>
        </c:otherwise>
    </c:choose>
</form>

<!-- Modal xác nhận xóa -->
<div id="confirmModal" class="modal">
    <div class="modal-content">
        <p>⚠️ Bạn có chắc chắn muốn xóa đơn nghỉ này không?</p>
        <div class="modal-buttons">
            <form id="deleteForm" action="${pageContext.request.contextPath}/request/view" method="post">
                <input type="hidden" name="id" value="${request.id}" />
                <input type="hidden" name="action" value="delete" />
                <button type="submit" id="confirmYes" class="btn btn-delete">Xóa</button>
            </form>
            <button id="confirmNo" class="btn btn-cancel">Hủy</button>
        </div>
    </div>
</div>

<script>
function openModal() {
    document.getElementById("confirmModal").style.display = "flex";
}
document.getElementById("confirmNo").onclick = function() {
    document.getElementById("confirmModal").style.display = "none";
};
</script>

<style>
h2 {
    margin-bottom: 15px;
}

/* Form layout */
.form-group {
    margin-bottom: 15px;
    display: flex;
    flex-direction: column;
}
.form-group label {
    font-weight: bold;
    margin-bottom: 5px;
}
input, select, textarea {
    padding: 8px;
    border: 1px solid #ccc;
    border-radius: 6px;
    font-size: 14px;
}

/* Buttons */
.form-actions {
    display: flex;
    gap: 10px;
    margin-top: 20px;
}
.btn {
    padding: 8px 16px;
    border: none;
    border-radius: 6px;
    font-weight: bold;
    cursor: pointer;
    color: white;
    transition: 0.3s;
}
.btn-update {
    background-color: #2a9d8f;
}
.btn-update:hover {
    background-color: #21867a;
}
.btn-delete {
    background-color: #e63946;
}
.btn-delete:hover {
    background-color: #c5303a;
}
.btn-cancel {
    background-color: #6c757d;
}
.btn-cancel:hover {
    background-color: #5a6268;
}

/* Messages */
.message {
    background: #f3f3f3;
    padding: 10px;
    border-radius: 6px;
    margin-top: 10px;
    font-weight: bold;
}
.message.success { color: #155724; background-color: #d4edda; }
.message.warning { color: #856404; background-color: #fff3cd; }
.message.error { color: #721c24; background-color: #f8d7da; }

/* Modal */
.modal {
    display: none;
    position: fixed;
    top: 0; left: 0;
    width: 100%; height: 100%;
    background-color: rgba(0,0,0,0.5);
    align-items: center;
    justify-content: center;
    z-index: 1000;
}
.modal-content {
    background: white;
    padding: 20px;
    border-radius: 8px;
    text-align: center;
    width: 350px;
}
.modal-buttons {
    display: flex;
    justify-content: center;
    gap: 15px;
    margin-top: 15px;
}
</style>
