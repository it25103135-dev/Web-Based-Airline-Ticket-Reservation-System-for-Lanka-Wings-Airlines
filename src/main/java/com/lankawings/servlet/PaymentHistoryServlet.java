package com.lankawings.servlet;

import com.lankawings.dao.PaymentDAO;
import com.lankawings.model.PaymentOrder;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebServlet("/payment/history")
public class PaymentHistoryServlet extends HttpServlet {
    private final PaymentDAO dao = new PaymentDAO();
    @Override protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String ref = req.getParameter("ref");
        if (ref != null && !ref.isBlank()) {
            try {
                PaymentOrder order = dao.findOrderByReference(ref);
                if (order == null) req.setAttribute("error", "Booking reference was not found.");
                else {
                    req.setAttribute("order", order);
                    req.setAttribute("payments", dao.historyForOrder(order.getOrderId()));
                }
            } catch (Exception e) { req.setAttribute("error", e.getMessage()); }
        }
        req.getRequestDispatcher("/payment-history.jsp").forward(req, resp);
    }
}
