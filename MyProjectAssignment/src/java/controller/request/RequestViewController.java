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

        try {
            if ("delete".equals(action)) {
                // 🗑 Xóa đơn
                RequestForLeave r = new RequestForLeave();
                r.setId(id);
                db.delete(r);
                resp.sendRedirect(req.getContextPath() + "/request/list");
                return;
            }

            // 💾 Cập nhật
            String fromStr = req.getParameter("from");
            String toStr = req.getParameter("to");
            String reason = req.getParameter("reason");
            String type = req.getParameter("type");

            if (fromStr == null || toStr == null || reason == null || type == null
                    || fromStr.trim().isEmpty() || toStr.trim().isEmpty() || reason.trim().isEmpty() || type.trim().isEmpty()) {
                
            } else {
                LocalDate today = LocalDate.now();
                LocalDate fromDate = LocalDate.parse(fromStr);
                LocalDate toDate = LocalDate.parse(toStr);

                if (fromDate.isBefore(today) || toDate.isBefore(today)) {
                    req.setAttribute("msg", "❌ Ngày bắt đầu hoặc kết thúc không được trước hôm nay!");
                } else if (toDate.isBefore(fromDate)) {
                    req.setAttribute("msg", "⚠️ Ngày kết thúc phải sau hoặc bằng ngày bắt đầu!");
                } else {
                    RequestForLeave r = new RequestForLeave();
                    r.setId(id);
                    r.setFrom(Date.valueOf(fromDate));
                    r.setTo(Date.valueOf(toDate));
                    r.setReason(reason);
                    r.setType(type);
                    r.setStatus(0); // 0 = chờ duyệt lại
                    r.setCreated_by(user.getEmployee());

                    db.updateRequest(r);

                    req.setAttribute("msg", "✅ Cập nhật đơn nghỉ thành công!");
                }
            }

            // ✅ Luôn reload lại dữ liệu mới nhất sau khi xử lý
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
