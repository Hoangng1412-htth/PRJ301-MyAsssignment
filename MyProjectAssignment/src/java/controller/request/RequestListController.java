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

        // Get the list of requests for the employee and their subordinates
        RequestForLeaveDBContext db = new RequestForLeaveDBContext();
        ArrayList<RequestForLeave> requests = db.getByEmployeeAndSubodiaries(user.getEmployee().getId());

        // Automatically set the status to 'Approved' if the user is a Director
        if (user.getRoles().stream().anyMatch(role -> role.getName().equalsIgnoreCase("Director"))) {
            for (RequestForLeave request : requests) {
                // If the request is created by the Director, set status to approved
                if (request.getCreated_by().getId() == user.getEmployee().getId() && request.getStatus() == 0) {
                    request.setStatus(1); // Approved
                    db.updateRequest(request); // Update status in DB
                }
            }
        }

        // Set attributes for the request list page
        req.setAttribute("requests", requests);
        req.setAttribute("pageTitle", "Danh sách đơn nghỉ");
        req.setAttribute("contentPage", "../request/list.jsp");
        req.getRequestDispatcher("/view/layout/layout.jsp").forward(req, resp);
    }

    @Override
    protected void processPost(HttpServletRequest req, HttpServletResponse resp, User user)
            throws ServletException, IOException {
    
    }
}
