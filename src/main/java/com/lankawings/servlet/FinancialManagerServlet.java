package com.lankawings.servlet;

import com.lankawings.dao.PaymentDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebServlet("/admin/payments")
public class FinancialManagerServlet extends HttpServlet {
    private final PaymentDAO dao = new PaymentDAO();

    @Override protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try { req.setAttribute("payments", dao.allPayments()); }
        catch (Exception e) { req.setAttribute("error", e.getMessage()); }
        req.getRequestDispatcher("/admin-payments.jsp").forward(req, resp);
    }

    @Override protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            int paymentId = Integer.parseInt(req.getParameter("paymentId"));
            String action = req.getParameter("action");
            if ("flag".equals(action)) dao.flag(paymentId, req.getParameter("issue"));
            else if ("resolve".equals(action)) dao.resolveLatestOpenFlag(paymentId);
            else if ("refund".equals(action)) dao.refund(paymentId);
        } catch (Exception e) {
            req.getSession().setAttribute("adminError", e.getMessage());
        }
        resp.sendRedirect(req.getContextPath() + "/admin/payments");
    }
}
