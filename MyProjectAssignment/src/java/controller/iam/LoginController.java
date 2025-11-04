/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller.iam;

import dal.FeatureDBContext;
import dal.RoleDBContext;
import dal.UserDBContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import model.iam.Feature;
import model.iam.Role;
import model.iam.User;

/**
 *
 * @author sonnt
 */
@WebServlet(urlPatterns = "/login")
public class LoginController extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String user = req.getParameter("username");
        String pass = req.getParameter("password");

        UserDBContext udb = new UserDBContext();
        User u = udb.get(user, pass);

        if (u == null) {
            req.setAttribute("error", "Invalid username or password!");
            req.getRequestDispatcher("view/auth/login.jsp").forward(req, resp);
        } else {
            // 🔹 Nạp role và feature cho user
            RoleDBContext rdb = new RoleDBContext();
            ArrayList<Role> roles = rdb.getByUserId(u.getId());
            u.setRoles(roles);
            FeatureDBContext fdb = new FeatureDBContext();
            ArrayList<Feature> allFeatures = fdb.list();
           req.getSession().setAttribute("features", allFeatures);

            // 🔹 Lưu vào session
            req.getSession().setAttribute("auth", u);

            // 🔹 Chuyển hướng sang trang home
            resp.sendRedirect(req.getContextPath() + "/home");
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.getRequestDispatcher("view/auth/login.jsp").forward(req, resp);
    }
}

