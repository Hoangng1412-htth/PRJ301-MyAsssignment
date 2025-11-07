package controller.request;

import controller.iam.BaseRequiredAuthorizationController;
import dal.RequestForLeaveDBContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Date;
import model.Employee;
import model.RequestForLeave;
import model.iam.User;

@WebServlet("/request/review")
public class RequestReviewController extends BaseRequiredAuthorizationController {

    @Override
    protected void processGet(HttpServletRequest req, HttpServletResponse resp, User user)
            throws ServletException, IOException {
        // Lấy ID từ parameter để lấy dữ liệu
        int id = Integer.parseInt(req.getParameter("id"));
        RequestForLeaveDBContext db = new RequestForLeaveDBContext();
        RequestForLeave r = db.getById(id);

        // Chuyển thông tin đơn vào request để hiển thị trong JSP
        req.setAttribute("request", r);
        req.setAttribute("pageTitle", "Chi tiết đơn nghỉ");
        req.setAttribute("contentPage", "/view/request/review.jsp");
        req.getRequestDispatcher("/view/layout/layout.jsp").forward(req, resp);
    }

   @Override
protected void processPost(HttpServletRequest req, HttpServletResponse resp, User user)
        throws ServletException, IOException {

    int id = Integer.parseInt(req.getParameter("id"));
    int newStatus = Integer.parseInt(req.getParameter("status"));
    
    RequestForLeaveDBContext db = new RequestForLeaveDBContext();
    RequestForLeave r = db.getById(id);

    // Kiểm tra nếu đơn đã duyệt hoặc từ chối
    if (r.getStatus() != 0) {
        req.setAttribute("msg", "⚠️ Đơn đã được duyệt hoặc từ chối. Bạn không thể thay đổi trạng thái này.");
    } 
    // Kiểm tra nếu người dùng không phải là người tạo đơn và có quyền duyệt
    else if (r.getCreated_by().getId() == user.getEmployee().getId()) {
        req.setAttribute("msg", "⚠️ Bạn không thể duyệt đơn do chính mình tạo!");
    } 
    else if (!userHasApprovalRole(user)) {
        req.setAttribute("msg", "🚫 Bạn không có quyền duyệt đơn nghỉ!");
    } 
    else {
        // Cập nhật trạng thái của đơn
        r.setStatus(newStatus);
        r.setProcessed_by(user.getEmployee());
        db.updateRequest(r);
        req.setAttribute("msg", "✅ Cập nhật trạng thái đơn nghỉ thành công!");
    }

    // Chuyển hướng về trang danh sách yêu cầu
    resp.sendRedirect(req.getContextPath() + "/request/list");
}


    private boolean userHasApprovalRole(User user) {
        return user.getRoles().stream().anyMatch(r -> {
            String name = r.getName().toLowerCase().replaceAll("\\s+", "");
            return name.toLowerCase().contains("teamleader") || 
       name.toLowerCase().contains("divisionmanager") || 
       name.toLowerCase().contains("director");

        });
    }
}

