package com.lankawings.servlet;

import com.lankawings.dao.ReviewDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebServlet("/admin/feedback")
public class AdminFeedbackServlet extends HttpServlet {
    private final ReviewDAO dao = new ReviewDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try { req.setAttribute("reviews", dao.listAll()); }
        catch (Exception e) { req.setAttribute("error", e.getMessage()); }
        req.getRequestDispatcher("/admin-feedback.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        try {
            int id = Integer.parseInt(req.getParameter("reviewId"));
            String action = req.getParameter("action");
            if ("respond".equals(action)) dao.respond(id, req.getParameter("response"));
            else if ("hide".equals(action)) dao.setVisibility(id, false);
            else if ("publish".equals(action)) dao.setVisibility(id, true);
            else if ("delete".equals(action)) dao.deleteAdmin(id);
            req.getSession().setAttribute("adminMessage", "Feedback record updated successfully.");
        } catch (Exception e) {
            req.getSession().setAttribute("adminError", e.getMessage());
        }
        resp.sendRedirect(req.getContextPath() + "/admin/feedback");
    }
}
