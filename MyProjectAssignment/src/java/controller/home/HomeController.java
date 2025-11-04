package controller.home;

import controller.iam.BaseRequiredAuthorizationController;
import dal.FeatureDBContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import model.iam.Feature;
import model.iam.User;

@WebServlet("/home")
public class HomeController extends BaseRequiredAuthorizationController {

@Override
protected void processGet(HttpServletRequest req, HttpServletResponse resp, User user)
        throws ServletException, IOException {
    req.setAttribute("pageTitle", "Trang chủ");

    // Không cần dấu / đầu vì file layout.jsp dùng jsp:include
    req.setAttribute("contentPage", "/view/home/home.jsp");
req.getRequestDispatcher("/view/layout/layout.jsp").forward(req, resp);

}



    @Override
    protected void processPost(HttpServletRequest req, HttpServletResponse resp, User user)
            throws ServletException, IOException {
        // Trang home thường không có xử lý POST, nên để trống
    }
}
