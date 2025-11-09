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
import model.RequestForLeave;
import model.iam.User;

@WebServlet("/request/view")
public class RequestViewController extends BaseRequiredAuthorizationController {

    @Override
    protected void processGet(HttpServletRequest req, HttpServletResponse resp, User user)
            throws ServletException, IOException {
        int id = Integer.parseInt(req.getParameter("id"));
        RequestForLeaveDBContext db = new RequestForLeaveDBContext();
        RequestForLeave request = db.getById(id);

        if (request == null) {
            req.setAttribute("msg", "❌ Không tìm thấy đơn nghỉ!");
        } else if (request.getCreated_by() != null
                && request.getCreated_by().getId() != user.getEmployee().getId()) {
            req.getSession().setAttribute("flashError", "🚫 Bạn không có quyền sửa hoặc xem đơn này!");
            resp.sendRedirect(req.getContextPath() + "/request/list");
            return;

        }

        req.setAttribute("request", request);
        req.setAttribute("pageTitle", "Chỉnh sửa đơn nghỉ");
        req.setAttribute("contentPage", "/view/request/view.jsp");
        req.getRequestDispatcher("/view/layout/layout.jsp").forward(req, resp);
    }

    @Override
    protected void processPost(HttpServletRequest req, HttpServletResponse resp, User user)
            throws ServletException, IOException {

        int id = Integer.parseInt(req.getParameter("id"));
        String action = req.getParameter("action");
        RequestForLeaveDBContext db = new RequestForLeaveDBContext();

        RequestForLeave existing = db.getById(id);
        if (existing == null || existing.getCreated_by() == null
                || existing.getCreated_by().getId() != user.getEmployee().getId()) {
            req.getSession().setAttribute("flashError", "🚫 Bạn không có quyền sửa hoặc xem đơn này!");
            resp.sendRedirect(req.getContextPath() + "/request/list");
            return;

        }

        try {
            if ("delete".equals(action)) {
                db.delete(existing);
                resp.sendRedirect(req.getContextPath() + "/request/list");
                return;
            }

            String fromStr = req.getParameter("from");
            String toStr = req.getParameter("to");
            String reason = req.getParameter("reason");
            String type = req.getParameter("type");

            if (fromStr == null || toStr == null || reason == null || type == null
                    || fromStr.trim().isEmpty() || toStr.trim().isEmpty()
                    || reason.trim().isEmpty() || type.trim().isEmpty()) {

                req.setAttribute("msg", "⚠️ Vui lòng nhập đầy đủ thông tin!");
            } else {
                LocalDate today = LocalDate.now();
                LocalDate fromDate = LocalDate.parse(fromStr);
                LocalDate toDate = LocalDate.parse(toStr);

                if (fromDate.isBefore(today) || toDate.isBefore(today)) {
                    req.setAttribute("msg", "❌ Ngày bắt đầu hoặc kết thúc không được trước hôm nay!");
                } else if (toDate.isBefore(fromDate)) {
                    req.setAttribute("msg", "⚠️ Ngày kết thúc phải sau hoặc bằng ngày bắt đầu!");
                } else {
                    existing.setFrom(Date.valueOf(fromDate));
                    existing.setTo(Date.valueOf(toDate));
                    existing.setReason(reason);
                    existing.setType(type);
                    existing.setStatus(0);

                    db.updateRequest(existing);
                    req.setAttribute("msg", "✅ Cập nhật đơn nghỉ thành công!");
                }
            }

            RequestForLeave updated = db.getById(id);
            req.setAttribute("request", updated);
            req.setAttribute("pageTitle", "Chỉnh sửa đơn nghỉ");
            req.setAttribute("contentPage", "/view/request/view.jsp");
            req.getRequestDispatcher("/view/layout/layout.jsp").forward(req, resp);

        } catch (Exception ex) {
            ex.printStackTrace();
            req.setAttribute("msg", "❌ Có lỗi xảy ra khi lưu đơn nghỉ!");
            RequestForLeave r = db.getById(id);
            req.setAttribute("request", r);
            req.setAttribute("pageTitle", "Chỉnh sửa đơn nghỉ");
            req.setAttribute("contentPage", "/view/request/view.jsp");
            req.getRequestDispatcher("/view/layout/layout.jsp").forward(req, resp);
        }
    }
}
