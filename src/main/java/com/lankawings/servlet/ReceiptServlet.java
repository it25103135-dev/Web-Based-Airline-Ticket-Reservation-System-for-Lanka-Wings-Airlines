package com.lankawings.servlet;

import com.lankawings.dao.PaymentDAO;
import com.lankawings.model.Payment;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebServlet("/receipt")
public class ReceiptServlet extends HttpServlet {
    private final PaymentDAO dao = new PaymentDAO();
    @Override protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            Payment payment = dao.findPaymentByRef(req.getParameter("tx"));
            if (payment == null) { resp.sendError(404); return; }
            req.setAttribute("payment", payment);
        } catch (Exception e) { req.setAttribute("error", e.getMessage()); }
        req.getRequestDispatcher("/receipt.jsp").forward(req, resp);
    }
}
