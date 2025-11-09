package controller.request;

import controller.iam.BaseRequiredAuthorizationController;
import dal.RequestForLeaveDBContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import model.RequestForLeave;
import model.iam.User;

@WebServlet("/request/list")
public class RequestListController extends BaseRequiredAuthorizationController {


    @Override
    protected void processGet(HttpServletRequest req, HttpServletResponse resp, User user)
            throws ServletException, IOException {

        // 🟢 Nhận các tham số lọc từ form
        String searchName = req.getParameter("searchName");
        String fromDate = req.getParameter("fromDate");
        String toDate = req.getParameter("toDate");
        String status = req.getParameter("status");
        String division = req.getParameter("division");

        // Nếu null thì gán rỗng để tránh lỗi null
        if (searchName == null) searchName = "";
        if (fromDate == null) fromDate = "";
        if (toDate == null) toDate = "";
        if (status == null) status = "";
        if (division == null) division = "";

        // 🟢 Gọi DB
        RequestForLeaveDBContext db = new RequestForLeaveDBContext();

        // 🧩 Xác định có phải Director không
        boolean isDirector = user.getRoles().stream()
                .anyMatch(r -> r.getName().equalsIgnoreCase("Director"));

        // 🧩 Lấy danh sách đơn theo vai trò & bộ lọc (bỏ phân trang)
        ArrayList<RequestForLeave> requests = db.getFilteredRequests(
                user, searchName, fromDate, toDate, status, division
        );

        // 🧩 Nếu là Director thì lấy toàn bộ phòng ban cho dropdown
        ArrayList<String> divisions = new ArrayList<>();
        if (isDirector) {
            divisions = db.getAllDivisions();
        }

        // 🟢 Auto-approve cho Director nếu duyệt đơn của chính mình (giữ logic cũ)
        if (isDirector) {
            for (RequestForLeave request : requests) {
                if (request.getCreated_by().getId() == user.getEmployee().getId()
                        && request.getStatus() == 0) {
                    request.setStatus(1);
                    db.updateRequest(request);
                }
            }
        }

        // 🟢 Gửi dữ liệu sang JSP
        req.setAttribute("requests", requests);
        req.setAttribute("divisions", divisions);
        req.setAttribute("searchName", searchName);
        req.setAttribute("fromDate", fromDate);
        req.setAttribute("toDate", toDate);
        req.setAttribute("status", status);
        req.setAttribute("division", division);
        req.setAttribute("isDirector", isDirector);

        req.setAttribute("pageTitle", "Danh sách đơn nghỉ");
        req.setAttribute("contentPage", "../request/list.jsp");

        req.getRequestDispatcher("/view/layout/layout.jsp").forward(req, resp);
    }

    @Override
    protected void processPost(HttpServletRequest req, HttpServletResponse resp, User user)
            throws ServletException, IOException {
    }
  
}
