<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<div class="leave-form-container">
    <h2>Tạo đơn nghỉ</h2>

    <form action="${pageContext.request.contextPath}/request/create" method="post" class="leave-form">
        <div class="form-group">
            <label>Loại nghỉ:</label>
            <select name="type" required>
                <option value="">-- Chọn loại nghỉ --</option>
                <option value="personal">Nghỉ việc cá nhân</option>
                <option value="sick">Nghỉ ốm</option>
                <option value="vacation">Nghỉ phép</option>
            </select>
        </div>

        <div class="form-group">
            <label>Từ ngày:</label>
            <input type="date" name="from" required />
        </div>

        <div class="form-group">
            <label>Đến ngày:</label>
            <input type="date" name="to" required />
        </div>
        <c:if test="${not empty msg}">
            <div class="message">${msg}</div>
        </c:if>

        <div class="form-group">
            <label>Lý do nghỉ:</label>
            <textarea name="reason" placeholder="Nhập lý do..." required></textarea>
        </div>

        <button type="submit">Gửi đơn</button>

        <c:if test="${not empty msg}">
            <p class="message
                <c:out value='${msg.startsWith("✅") ? "success" : "error"}'/>">
                ${msg}
            </p>
        </c:if>
    </form>
</div>
