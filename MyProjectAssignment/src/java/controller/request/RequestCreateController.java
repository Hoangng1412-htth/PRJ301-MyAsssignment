/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
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
import model.iam.User;

@WebServlet("/request/create")
public class RequestCreateController extends BaseRequiredAuthorizationController {

@Override
protected void processGet(HttpServletRequest req, HttpServletResponse resp, User user)
        throws ServletException, IOException {
    req.setAttribute("pageTitle", "Tạo đơn nghỉ");
    req.setAttribute("contentPage", "/view/request/create.jsp");
    req.getRequestDispatcher("/view/layout/layout.jsp").forward(req, resp);
}
@Override
protected void processPost(HttpServletRequest req, HttpServletResponse resp, User user)
        throws ServletException, IOException {

    String from = req.getParameter("from");
    String to = req.getParameter("to");
    String reason = req.getParameter("reason");
    String type = req.getParameter("type");

    // Kiểm tra dữ liệu bắt buộc
    if (from == null || to == null || reason == null || type == null
            || from.isEmpty() || to.isEmpty() || reason.isEmpty() || type.isEmpty()) {

        req.setAttribute("msg", "⚠️ Vui lòng điền đầy đủ thông tin!");

    } else {
        try {
            Date fromDate = Date.valueOf(from);
            Date toDate = Date.valueOf(to);

            // ✅ Kiểm tra ngày kết thúc phải >= ngày bắt đầu
            if (toDate.before(fromDate)) {
                req.setAttribute("msg", "⚠️ Ngày kết thúc phải sau hoặc bằng ngày bắt đầu!");
            } else {
                // Tạo đối tượng RequestForLeave
                RequestForLeave r = new RequestForLeave();
                r.setFrom(fromDate);
                r.setTo(toDate);
                r.setReason(reason);
                r.setType(type);
                r.setStatus(0); // 0: chờ duyệt
                r.setCreated_by(user.getEmployee());

                // Lưu vào DB
                RequestForLeaveDBContext db = new RequestForLeaveDBContext();
                db.insert(r);

                req.setAttribute("msg", "✅ Gửi đơn nghỉ thành công!");
            }

        } catch (IllegalArgumentException ex) {
            req.setAttribute("msg", "⚠️ Định dạng ngày không hợp lệ!");
        } catch (Exception ex) {
            ex.printStackTrace();
            req.setAttribute("msg", "❌ Có lỗi xảy ra khi lưu đơn nghỉ!");
        }
    }

    req.setAttribute("pageTitle", "Tạo đơn nghỉ");
    req.setAttribute("contentPage", "/view/request/create.jsp");
    req.getRequestDispatcher("/view/layout/layout.jsp").forward(req, resp);
}

}

