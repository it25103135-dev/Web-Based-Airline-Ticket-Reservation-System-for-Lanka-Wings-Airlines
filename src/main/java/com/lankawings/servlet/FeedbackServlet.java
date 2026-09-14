package com.lankawings.servlet;

import com.lankawings.dao.ReviewDAO;
import com.lankawings.model.Review;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;

@WebServlet("/feedback")
public class FeedbackServlet extends HttpServlet {
    private final ReviewDAO dao = new ReviewDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try { req.setAttribute("reviews", dao.listPublished()); }
        catch (Exception e) { req.setAttribute("error", e.getMessage()); }
        req.getRequestDispatcher("/feedback.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        try {
            Review r = new Review();
            r.setPassengerName(req.getParameter("passengerName"));
            r.setEmail(req.getParameter("email"));
            r.setBookingReference(req.getParameter("bookingReference"));
            r.setFlightNo(req.getParameter("flightNo"));
            r.setRating(Integer.parseInt(req.getParameter("rating")));
            r.setTitle(req.getParameter("title"));
            r.setComment(req.getParameter("comment"));
            dao.create(r);
            resp.sendRedirect(req.getContextPath() + "/feedback?submitted=1");
            return;
        } catch (Exception e) {
            req.setAttribute("error", e.getMessage());
            try { req.setAttribute("reviews", dao.listPublished()); } catch (Exception ignored) {}
        }
        req.getRequestDispatcher("/feedback.jsp").forward(req, resp);
    }
}
