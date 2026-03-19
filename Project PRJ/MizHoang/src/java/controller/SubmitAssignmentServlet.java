/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */

package controller;

import dal.SubmissionDAO;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;
import java.io.File;
import models.User;
import jakarta.servlet.annotation.MultipartConfig;

@MultipartConfig

public class SubmitAssignmentServlet extends HttpServlet {
   

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet SubmitAssignmentServlet</title>");  
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet SubmitAssignmentServlet at " + request.getContextPath () + "</h1>");
            out.println("</body>");
            out.println("</html>");
        }
    } 

 @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();
        User user = (User) session.getAttribute("user");
        if (user == null || !"student".equals(user.getRole())) {
            resp.sendRedirect("login");
            return;
        }

        int assignmentId = Integer.parseInt(req.getParameter("assignmentId"));
        Part part = req.getPart("file");

        String fileName = part.getSubmittedFileName();
        String uploadPath = getServletContext().getRealPath("/uploads");
        File uploadDir = new File(uploadPath);
        if (!uploadDir.exists()) uploadDir.mkdir();

        String savedName = System.currentTimeMillis() + "_" + fileName;
        String filePath = "uploads/" + savedName;
        part.write(uploadPath + "/" + savedName);

        SubmissionDAO dao = new SubmissionDAO();
        dao.submitAssignment(assignmentId, user.getUserId(), filePath);

        resp.sendRedirect("assignments");
    }

}
