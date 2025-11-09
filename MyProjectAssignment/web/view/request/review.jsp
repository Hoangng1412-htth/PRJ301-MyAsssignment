<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<h2>Chi tiết đơn nghỉ</h2>

<form action="${pageContext.request.contextPath}/request/review" method="post" class="leave-form">
    <input type="hidden" name="id" value="${request.id}">

    <div class="form-group">
        <label>Tên nhân viên:</label>
        <input type="text" value="${request.created_by.name}" readonly>
    </div>

    <div class="form-group">
        <label>Phòng ban:</label>
        <input type="text" value="${request.created_by['div'].dname}" readonly>
    </div>

    <div class="form-group">
        <label>Loại nghỉ:</label>
        <input type="text" value="${request.type}" readonly>
    </div>

    <div class="form-group">
        <label>Từ ngày:</label>
        <input type="date" name="from" 
               value="${request.from}" 
               min="${tomorrow}" 
               required>
    </div>

    <div class="form-group">
        <label>Đến ngày:</label>
        <input type="date" name="to" 
               value="${request.to}" 
               min="${tomorrow}" 
               required>
    </div>

    <div class="form-group">
        <label>Lý do:</label>
        <textarea readonly>${request.reason}</textarea>
    </div>

    <div class="form-group">
        <label>Trạng thái hiện tại:</label>
        <c:choose>
            <c:when test="${request.status == 0}">
                <span class="status inprogress">Đang chờ duyệt</span>
            </c:when>
            <c:when test="${request.status == 1}">
                <span class="status approved">Đã duyệt</span>
            </c:when>
            <c:when test="${request.status == 2}">
                <span class="status rejected">Đã từ chối</span>
            </c:when>
        </c:choose>
    </div>

    <!-- ✅ CHỈ HIỂN THỊ NÚT DUYỆT / TỪ CHỐI KHI CẤP CÓ THỂ DUYỆT -->
    <c:choose>
        <c:when test="${canApprove}">
            <div class="form-group action-buttons">
                <button type="submit" name="status" value="1" class="btn-action approve">✔ Duyệt</button>
                <button type="submit" name="status" value="2" class="btn-action reject">❌ Từ chối</button>
            </div>
        </c:when>
        <c:otherwise>
            <div class="form-group note">
                <p class="info-note">
                    ⚠️ Đơn này đã được cấp cao hơn duyệt hoặc từ chối. Bạn chỉ có thể xem chi tiết.
                </p>
            </div>
        </c:otherwise>
    </c:choose>

</form>

<style>
.leave-form {
    max-width: 600px;
    margin: 20px auto;
    padding: 20px;
    background: #fff;
    border-radius: 12px;
    box-shadow: 0 2px 8px rgba(0,0,0,0.1);
}
h2 {
    text-align: center;
    color: #333;
    margin-bottom: 20px;
}
.form-group {
    margin-bottom: 16px;
}
label {
    font-weight: 600;
    display: block;
    margin-bottom: 6px;
    color: #444;
}
input, textarea {
    width: 100%;
    padding: 8px 10px;
    border: 1px solid #ccc;
    border-radius: 6px;
    font-size: 14px;
}
textarea {
    resize: none;
    height: 80px;
}
.status {
    font-weight: bold;
    padding: 5px 12px;
    border-radius: 6px;
    font-size: 13px;
}
.status.inprogress { background-color: #fff3cd; color: #856404; }
.status.approved { background-color: #d4edda; color: #155724; }
.status.rejected { background-color: #f8d7da; color: #721c24; }

.action-buttons {
    display: flex;
    justify-content: space-between;
    gap: 10px;
}
.btn-action {
    flex: 1;
    font-weight: 600;
    border: none;
    padding: 10px 14px;
    border-radius: 6px;
    cursor: pointer;
    color: white;
    transition: 0.2s;
}
.btn-action.approve { background-color: #2a9d8f; }
.btn-action.reject { background-color: #e76f51; }
.btn-action:hover { opacity: 0.9; }

.info-note {
    font-style: italic;
    color: #888;
    background: #f9f9f9;
    padding: 8px;
    border-radius: 6px;
}
</style>
