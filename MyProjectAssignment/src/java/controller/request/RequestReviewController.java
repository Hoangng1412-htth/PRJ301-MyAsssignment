package controller.request;

import controller.iam.BaseRequiredAuthorizationController;
import dal.RequestForLeaveDBContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Date;
import java.time.LocalDate;
import model.Employee;
import model.RequestForLeave;
import model.iam.Role;
import model.iam.User;

@WebServlet("/request/review")
public class RequestReviewController extends BaseRequiredAuthorizationController {

    @Override
    protected void processGet(HttpServletRequest req, HttpServletResponse resp, User user)
            throws ServletException, IOException {

        String rawId = req.getParameter("id");
        if (rawId == null || !rawId.matches("\\d+")) {
            req.getSession().setAttribute("flashError", "🚫 ID không hợp lệ!");
            resp.sendRedirect(req.getContextPath() + "/request/list");
            return;
        }

        int id = Integer.parseInt(rawId);
        RequestForLeaveDBContext db = new RequestForLeaveDBContext();
        RequestForLeave r = db.getById(id);

        if (r == null) {
            req.getSession().setAttribute("flashError", "🚫 Không tìm thấy đơn nghỉ!");
            resp.sendRedirect(req.getContextPath() + "/request/list");
            return;
        }

        // 🧩 Kiểm tra quyền truy cập
        if (!canAccessRequest(user, r)) {
            req.getSession().setAttribute("flashError", "🚫 Bạn không có quyền xem hoặc duyệt đơn này!");
            resp.sendRedirect(req.getContextPath() + "/request/list");
            return;
        }

        // Xác định cấp độ hiện tại và cấp người đã duyệt
        int currentLevel = getApprovalLevel(user);
        int processedLevel = getApprovalLevelFromProcessedBy(r.getProcessed_by());

        // ✅ Kiểm tra có thể duyệt không
        boolean isSelfProcessed = r.getProcessed_by() != null
                && r.getProcessed_by().getId() == user.getEmployee().getId();

        boolean canApprove = isSelfProcessed || canApproveRequest(currentLevel, processedLevel);

        // 🗓️ Ràng buộc ngày nghỉ: chỉ từ ngày mai
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        req.setAttribute("tomorrow", tomorrow.toString());

        // Gửi dữ liệu sang JSP
        req.setAttribute("canApprove", canApprove);
        req.setAttribute("request", r);
        req.setAttribute("pageTitle", "Chi tiết đơn nghỉ");
        req.setAttribute("contentPage", "/view/request/review.jsp");
        req.getRequestDispatcher("/view/layout/layout.jsp").forward(req, resp);
    }

    @Override
    protected void processPost(HttpServletRequest req, HttpServletResponse resp, User user)
            throws ServletException, IOException {

        try {
            int id = Integer.parseInt(req.getParameter("id"));
            int newStatus = Integer.parseInt(req.getParameter("status"));
            Date from = Date.valueOf(req.getParameter("from"));
            Date to = Date.valueOf(req.getParameter("to"));

            RequestForLeaveDBContext db = new RequestForLeaveDBContext();
            RequestForLeave r = db.getById(id);

            if (r == null) {
                req.getSession().setAttribute("flashError", "🚫 Không tìm thấy đơn nghỉ!");
                resp.sendRedirect(req.getContextPath() + "/request/list");
                return;
            }

            if (!canAccessRequest(user, r)) {
                req.getSession().setAttribute("flashError", "🚫 Bạn không có quyền duyệt đơn này!");
                resp.sendRedirect(req.getContextPath() + "/request/list");
                return;
            }

            // ❌ Không cho duyệt đơn chính mình tạo
            if (r.getCreated_by().getId() == user.getEmployee().getId()) {
                req.getSession().setAttribute("flashError", "⚠️ Bạn không thể duyệt đơn của chính mình!");
                resp.sendRedirect(req.getContextPath() + "/request/list");
                return;
            }

            int currentLevel = getApprovalLevel(user);
            int processedLevel = getApprovalLevelFromProcessedBy(r.getProcessed_by());

            boolean isSelfProcessed = r.getProcessed_by() != null
                    && r.getProcessed_by().getId() == user.getEmployee().getId();

// ✅ Nếu là người duyệt trước hoặc đủ cấp duyệt → được phép cập nhật
            boolean canApprove = isSelfProcessed || canApproveRequest(currentLevel, processedLevel);

            if (!canApprove) {
                req.getSession().setAttribute("flashError",
                        "🚫 Đơn đã được cấp cao hơn duyệt. Bạn không thể thay đổi!");
                resp.sendRedirect(req.getContextPath() + "/request/list");
                return;
            }

            // ✅ Cập nhật trạng thái
            r.setStatus(newStatus);
            r.setFrom(from);
            r.setTo(to);
            r.setProcessed_by(user.getEmployee());
            db.updateRequest(r);

            // Gửi thông báo thành công
            req.getSession().setAttribute("flashMessage", "✅ Cập nhật trạng thái đơn nghỉ thành công!");
            resp.sendRedirect(req.getContextPath() + "/request/list");

        } catch (Exception e) {
            e.printStackTrace();
            req.getSession().setAttribute("flashError", "⚠️ Dữ liệu không hợp lệ hoặc lỗi hệ thống!");
            resp.sendRedirect(req.getContextPath() + "/request/list");
        }
    }

    // ==================================================
    // 🔧 SUPPORT METHODS
    // ==================================================
    /**
     * Kiểm tra quyền xem đơn nghỉ
     */
    private boolean canAccessRequest(User user, RequestForLeave request) {
        RequestForLeaveDBContext db = new RequestForLeaveDBContext();
        int currentUserId = user.getEmployee().getId();
        int requesterId = request.getCreated_by().getId();

        // Director xem tất cả
        boolean isDirector = user.getRoles().stream()
                .anyMatch(r -> r.getName().equalsIgnoreCase("Director"));
        if (isDirector) {
            return true;
        }

        // Người tạo xem được đơn của chính mình
        if (currentUserId == requesterId) {
            return true;
        }

        // Hoặc là cấp trên trực tiếp
        var subs = db.getEmployeeAndSubordinates(currentUserId);
        return subs.stream().anyMatch(e -> e.getId() == requesterId);
    }

    /**
     * Xác định cấp độ duyệt: 1=TeamLeader, 2=DivisionManager, 3=Director
     */
    private int getApprovalLevel(User user) {
        RequestForLeaveDBContext db = new RequestForLeaveDBContext();
        return db.getApprovalLevelByUserId(user.getId());
    }

    /**
     * Lấy cấp độ của người đã duyệt (nếu có)
     */
    private int getApprovalLevelFromProcessedBy(Employee processedBy) {
        if (processedBy == null) {
            return 0;
        }
        RequestForLeaveDBContext db = new RequestForLeaveDBContext();
        return db.getApprovalLevelByEmployeeId(processedBy.getId());
    }

    /**
     * Quy tắc duyệt phân cấp: Director (3) > DivisionManager (2) > TeamLeader
     * (1) - Cấp cao có thể duyệt lại cấp thấp - Cấp thấp KHÔNG thể duyệt lại
     * cấp cao
     */
    private boolean canApproveRequest(int currentLevel, int processedLevel) {
        if (processedLevel == 0) {
            return true;               // chưa ai duyệt
        }
        if (processedLevel == 1 && currentLevel >= 2) {
            return true; // TL -> DM/Dir
        }
        if (processedLevel == 2 && currentLevel == 3) {
            return true; // DM -> Dir
        }
        if (processedLevel == 3) {
            return false;              // Dir -> khóa
        }
        return false;
    }
}
