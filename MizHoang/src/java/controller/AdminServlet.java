package controller;

import dal.UserDAO;
import java.io.IOException;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import models.User;

public class AdminServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();
        User loggedInUser = (User) session.getAttribute("user");
        if (loggedInUser == null || !"admin".equals(loggedInUser.getRole())) {
            resp.sendRedirect("login");
            return;
        }

        UserDAO userDAO = new UserDAO();
        List<User> users = userDAO.getAllUsers();
        req.setAttribute("users", users);
        
        String errorMsg = (String) session.getAttribute("errorMsg");
        if (errorMsg != null) {
            req.setAttribute("errorMsg", errorMsg);
            session.removeAttribute("errorMsg");
        }
        String successMsg = (String) session.getAttribute("successMsg");
        if (successMsg != null) {
            req.setAttribute("successMsg", successMsg);
            session.removeAttribute("successMsg");
        }

        req.getRequestDispatcher("adminPanel.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();
        User loggedInUser = (User) session.getAttribute("user");
        if (loggedInUser == null || !"admin".equals(loggedInUser.getRole())) {
            resp.sendRedirect("login");
            return;
        }

        String action = req.getParameter("action");
        int targetUserId = Integer.parseInt(req.getParameter("targetUserId"));
        UserDAO userDAO = new UserDAO();

        if ("approve".equals(action)) {
            userDAO.updateUserStatus(targetUserId, "ACTIVE");
            session.setAttribute("successMsg", "Đã duyệt tài khoản thành công!");
        } else if ("reject".equals(action)) {
            userDAO.updateUserStatus(targetUserId, "REJECTED");
            session.setAttribute("successMsg", "Đã từ chối tài khoản thành công!");
        } else if ("delete".equals(action)) {
            if (userDAO.deleteUser(targetUserId)) {
                session.setAttribute("successMsg", "Đã xóa tài khoản thành công!");
            } else {
                session.setAttribute("errorMsg", "Không thể xóa tài khoản này! Người dùng có thể đang liên kết với dữ liệu bài tập hoặc bài nộp.");
            }
        } else if ("reset_password".equals(action)) {
            String newPassword = req.getParameter("newPassword");
            userDAO.updatePassword(targetUserId, newPassword);
            session.setAttribute("successMsg", "Đã đặt lại mật khẩu thành công!");
        }

        resp.sendRedirect("admin");
    }
}
