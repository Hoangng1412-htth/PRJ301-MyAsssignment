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

    if (from == null || to == null || reason == null || type == null
            || from.isEmpty() || to.isEmpty() || reason.isEmpty() || type.isEmpty()) {
        req.setAttribute("msg", "⚠️ Vui lòng điền đầy đủ thông tin!");
    } else {
        try {
            LocalDate today = LocalDate.now();
            LocalDate fromDate = LocalDate.parse(from);
            LocalDate toDate = LocalDate.parse(to);

            if (fromDate.isBefore(today) || toDate.isBefore(today)) {
                req.setAttribute("msg", "❌ Ngày bắt đầu và kết thúc không được trước ngày hiện tại!");
            } else if (toDate.isBefore(fromDate)) {
                req.setAttribute("msg", "⚠️ Ngày kết thúc không thể trước ngày bắt đầu!");
            } else {
                // ✅ Hợp lệ: tạo đơn nghỉ
                RequestForLeave r = new RequestForLeave();
                r.setFrom(Date.valueOf(from));
                r.setTo(Date.valueOf(to));
                r.setReason(reason);
                r.setType(type);
                r.setStatus(0);
                r.setCreated_by(user.getEmployee());

                RequestForLeaveDBContext db = new RequestForLeaveDBContext();
                db.insert(r);

                req.setAttribute("msg", "✅ Gửi đơn thành công!");
            }

        } catch (Exception e) {
            e.printStackTrace();
            req.setAttribute("msg", "❌ Lỗi khi gửi đơn nghỉ!");
        }
    }

    req.setAttribute("pageTitle", "Tạo đơn nghỉ");
    req.setAttribute("contentPage", "/view/request/create.jsp");
    req.getRequestDispatcher("/view/layout/layout.jsp").forward(req, resp);
}
}

