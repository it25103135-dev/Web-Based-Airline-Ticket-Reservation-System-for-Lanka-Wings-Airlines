package com.lankawings.servlet;

import com.lankawings.dao.PaymentDAO;
import com.lankawings.model.Payment;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/receipt/download")
public class ReceiptDownloadServlet extends HttpServlet {
    private final PaymentDAO dao = new PaymentDAO();
    @Override protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            Payment p = dao.findPaymentByRef(req.getParameter("tx"));
            if (p == null || !"SUCCESS".equals(p.getGatewayStatus())) { resp.sendError(404); return; }
            resp.setContentType("text/plain;charset=UTF-8");
            resp.setHeader("Content-Disposition", "attachment; filename=receipt-" + p.getTransactionRef() + ".txt");
            PrintWriter out = resp.getWriter();
            out.println("LANKA WINGS - PAYMENT RECEIPT");
            out.println("================================");
            out.println("Booking Reference: " + p.getBookingReference());
            out.println("Passenger: " + p.getPassengerName());
            out.println("Transaction: " + p.getTransactionRef());
            out.println("Amount: LKR " + p.getAmount());
            out.println("Method: " + p.getMethod());
            out.println("Card: **** **** **** " + p.getCardLast4());
            out.println("Status: " + p.getGatewayStatus());
            out.println("Paid At: " + p.getPaidAt());
            out.println("================================");
            out.println("Thank you for choosing Lanka Wings.");
        } catch (Exception e) { resp.sendError(500, e.getMessage()); }
    }
}
