
<%@page contentType="text/html" pageEncoding="UTF-8"%>

<h2>Chi tiết đơn nghỉ</h2>

<form action="${pageContext.request.contextPath}/request/review" method="post" class="leave-form">
    <!-- Truyền id vào form dưới dạng trường ẩn -->
    <input type="hidden" name="id" value="${request.id}">

    <div class="form-group">
        <label>Phòng ban:</label>
        <input type="text" value="${request.created_by['div'].dname}" readonly>
    </div>

    <div class="form-group">
        <label>Từ ngày:</label>
        <input type="date" name="from" value="${request.from}" readonly>
    </div>

    <div class="form-group">
        <label>Đến ngày:</label>
        <input type="date" name="to" value="${request.to}" readonly>
    </div>

    <div class="form-group">
        <label>Lý do:</label>
        <textarea name="reason" readonly>${request.reason}</textarea>
    </div>

    <!-- Hiển thị nút Duyệt và Từ chối nếu trạng thái là 0 (chưa duyệt) -->
    <c:if test="${request.status == 0}">
        <div class="form-group">
            <button type="submit" name="status" value="1" class="btn-action approve">✔ Duyệt</button>
            <button type="submit" name="status" value="2" class="btn-action delete">❌ Từ chối</button>
        </div>
    </c:if>
</form>
