package com.lankawings.servlet;

import com.lankawings.dao.ReviewDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebServlet("/my-reviews")
public class MyReviewsServlet extends HttpServlet {
    private final ReviewDAO dao = new ReviewDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String email = req.getParameter("email");
        String ref = req.getParameter("bookingReference");
        if (email != null && !email.isBlank() && ref != null && !ref.isBlank()) {
            try { req.setAttribute("reviews", dao.findPassengerReviews(email, ref)); }
            catch (Exception e) { req.setAttribute("error", e.getMessage()); }
        }
        req.getRequestDispatcher("/my-reviews.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String email = req.getParameter("email");
        String ref = req.getParameter("bookingReference");
        try {
            int id = Integer.parseInt(req.getParameter("reviewId"));
            String action = req.getParameter("action");
            if ("update".equals(action)) {
                dao.updatePassengerReview(id, email, ref,
                        Integer.parseInt(req.getParameter("rating")),
                        req.getParameter("title"), req.getParameter("comment"));
            } else if ("delete".equals(action)) {
                dao.deletePassengerReview(id, email, ref);
            }
            resp.sendRedirect(req.getContextPath() + "/my-reviews?email=" + java.net.URLEncoder.encode(email, java.nio.charset.StandardCharsets.UTF_8) + "&bookingReference=" + java.net.URLEncoder.encode(ref, java.nio.charset.StandardCharsets.UTF_8) + "&ok=1");
            return;
        } catch (Exception e) {
            req.setAttribute("error", e.getMessage());
            try { req.setAttribute("reviews", dao.findPassengerReviews(email, ref)); } catch (Exception ignored) {}
        }
        req.getRequestDispatcher("/my-reviews.jsp").forward(req, resp);
    }
}
