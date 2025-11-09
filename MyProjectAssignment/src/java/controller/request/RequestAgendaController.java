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
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashMap;
import model.Division;
import model.Employee;
import model.RequestForLeave;
import model.iam.User;

@WebServlet("/request/agenda")
public class RequestAgendaController extends BaseRequiredAuthorizationController {

    @Override
    protected void processGet(HttpServletRequest req, HttpServletResponse resp, User user)
            throws ServletException, IOException {

        // --- 1️⃣ Xác định tháng, năm ---
        LocalDate today = LocalDate.now();
        int month = req.getParameter("month") != null
                ? Integer.parseInt(req.getParameter("month"))
                : today.getMonthValue();
        int year = req.getParameter("year") != null
                ? Integer.parseInt(req.getParameter("year"))
                : today.getYear();

        // Rollover tháng
        if (month < 1) {
            month = 12;
            year--;
        } else if (month > 12) {
            month = 1;
            year++;
        }

        YearMonth ym = YearMonth.of(year, month);
        LocalDate start = ym.atDay(1);
        LocalDate end = ym.atEndOfMonth();

        RequestForLeaveDBContext db = new RequestForLeaveDBContext();

        // --- 2️⃣ Xác định vai trò ---
        boolean isDirector = user.getRoles().stream()
                .anyMatch(r -> r.getName().equalsIgnoreCase("Director"));
        boolean isDivisionManager = user.getRoles().stream()
                .anyMatch(r -> r.getName().equalsIgnoreCase("DivisionManager"));

        ArrayList<Employee> employees = new ArrayList<>();
        ArrayList<Division> divisions = null;

        // --- 3️⃣ Director: có thể chọn toàn công ty hoặc từng division ---
        if (isDirector) {
            divisions = db.getAllDivisionsForAgenda();
            String divisionId = req.getParameter("division");

            if (divisionId == null || divisionId.isEmpty()) {
                // Toàn công ty
                employees = db.getEmployeesByDivision(null);
            } else {
                // Chỉ 1 phòng ban
                employees = db.getEmployeesByDivision(divisionId);
            }

            req.setAttribute("divisions", divisions);
            req.setAttribute("selectedDivision", divisionId);
        }
        // --- 4️⃣ DivisionManager hoặc TeamLeader ---
        else if (isDivisionManager) {
            employees = db.getEmployeeAndSubordinates(user.getEmployee().getId());
        }

        // --- 5️⃣ Lấy đơn nghỉ trong tháng ---
        HashMap<Integer, ArrayList<RequestForLeave>> leaves =
                db.getLeavesInRange(employees, Date.valueOf(start), Date.valueOf(end));

        // --- 6️⃣ Đưa dữ liệu sang JSP ---
        req.setAttribute("employees", employees);
        req.setAttribute("leaves", leaves);
        req.setAttribute("daysInMonth", ym.lengthOfMonth());
        req.setAttribute("month", month);
        req.setAttribute("year", year);
        req.setAttribute("isDirector", isDirector);

        req.setAttribute("pageTitle", "Lịch làm việc");
        req.setAttribute("contentPage", "/view/request/agenda.jsp");
        req.getRequestDispatcher("/view/layout/layout.jsp").forward(req, resp);
    }

    @Override
    protected void processPost(HttpServletRequest req, HttpServletResponse resp, User user)
            throws ServletException, IOException {
        resp.sendRedirect(req.getContextPath() + "/request/agenda");
    }
}
