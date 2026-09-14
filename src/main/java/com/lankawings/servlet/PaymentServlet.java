package com.lankawings.servlet;

import com.lankawings.dao.PaymentDAO;
import com.lankawings.model.PaymentOrder;
import com.lankawings.util.CardUtil;
import com.lankawings.util.PaymentGateway;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/payment")
public class PaymentServlet extends HttpServlet {
    private final PaymentDAO dao = new PaymentDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String ref = req.getParameter("ref");
        if (ref != null && !ref.isBlank()) {
            try {
                PaymentOrder order = dao.findOrderByReference(ref);
                if (order == null) req.setAttribute("error", "Booking reference was not found.");
                else req.setAttribute("order", order);
            } catch (Exception e) {
                req.setAttribute("error", e.getMessage());
            }
        }
        req.getRequestDispatcher("/payment.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            int orderId = Integer.parseInt(req.getParameter("orderId"));
            PaymentOrder order = dao.findOrderById(orderId);
            if (order == null) throw new IllegalArgumentException("Payment order was not found.");
            if ("PAID".equals(order.getOrderStatus())) throw new IllegalArgumentException("This booking has already been paid.");
            if ("CANCELLED".equals(order.getOrderStatus())) throw new IllegalArgumentException("Cancelled bookings cannot be paid.");

            String method = req.getParameter("method");
            String cardNumber = req.getParameter("cardNumber");
            String expiry = req.getParameter("expiry");
            String cvv = req.getParameter("cvv");
            String digits = CardUtil.digitsOnly(cardNumber);
            String last4 = digits.length() >= 4 ? digits.substring(digits.length() - 4) : null;

            PaymentGateway.GatewayResult result = PaymentGateway.authorize(cardNumber, expiry, cvv);
            String tx = dao.recordPayment(orderId, method, last4, result.approved(), result.message());
            if (result.approved()) {
                resp.sendRedirect(req.getContextPath() + "/receipt?tx=" + tx);
                return;
            }

            req.setAttribute("order", order);
            req.setAttribute("error", result.message() + " Transaction logged as " + tx + ".");
            req.getRequestDispatcher("/payment.jsp").forward(req, resp);
        } catch (Exception e) {
            req.setAttribute("error", e.getMessage());
            doGet(req, resp);
        }
    }
}
