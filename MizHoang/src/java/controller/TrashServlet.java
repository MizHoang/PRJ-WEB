package controller;

import dal.AssignmentDAO;
import java.io.IOException;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import models.User;
import models.Assignment;

@WebServlet(name="TrashServlet", urlPatterns={"/trash"})
public class TrashServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();
        User user = (User) session.getAttribute("user");
        if (user == null || (!"teacher".equals(user.getRole()) && !"admin".equals(user.getRole()))) {
            resp.sendRedirect("login");
            return;
        }

        AssignmentDAO dao = new AssignmentDAO();
        boolean isAdmin = "admin".equals(user.getRole());
        int classId = user.getClassId() != null ? user.getClassId() : 0;
        
        List<Assignment> trashed = dao.getDeletedAssignments(isAdmin, classId);
        
        req.setAttribute("list", trashed);
        req.getRequestDispatcher("trashAssignments.jsp").forward(req, resp);
    }
}
