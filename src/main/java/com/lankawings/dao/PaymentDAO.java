package com.lankawings.dao;

import com.lankawings.config.DBConnection;
import com.lankawings.model.Payment;
import com.lankawings.model.PaymentOrder;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PaymentDAO {
    public PaymentOrder findOrderByReference(String reference) throws SQLException {
        String sql = "SELECT * FROM PaymentOrders WHERE BookingReference=?";
        try (Connection c = DBConnection.getConnection(); PreparedStatement p = c.prepareStatement(sql)) {
            p.setString(1, reference == null ? "" : reference.trim().toUpperCase());
            try (ResultSet r = p.executeQuery()) { return r.next() ? mapOrder(r) : null; }
        }
    }

    public PaymentOrder findOrderById(int orderId) throws SQLException {
        try (Connection c = DBConnection.getConnection(); PreparedStatement p = c.prepareStatement("SELECT * FROM PaymentOrders WHERE OrderID=?")) {
            p.setInt(1, orderId);
            try (ResultSet r = p.executeQuery()) { return r.next() ? mapOrder(r) : null; }
        }
    }

    public String recordPayment(int orderId, String method, String last4, boolean approved, String failureReason) throws SQLException {
        String ref = "TXN-" + UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase();
        try (Connection c = DBConnection.getConnection()) {
            c.setAutoCommit(false);
            try {
                PaymentOrder order;
                try (PreparedStatement p = c.prepareStatement("SELECT * FROM PaymentOrders WITH (UPDLOCK,HOLDLOCK) WHERE OrderID=?")) {
                    p.setInt(1, orderId);
                    try (ResultSet r = p.executeQuery()) {
                        if (!r.next()) throw new SQLException("Payment order not found.");
                        order = mapOrder(r);
                    }
                }
                if ("CANCELLED".equals(order.getOrderStatus())) throw new SQLException("Cancelled orders cannot be paid.");
                if ("PAID".equals(order.getOrderStatus()) && approved) throw new SQLException("This order is already paid.");

                int paymentId;
                String insert = "INSERT INTO Payments(OrderID,Amount,Method,CardLast4,TransactionRef,GatewayStatus,FailureReason) VALUES(?,?,?,?,?,?,?)";
                try (PreparedStatement p = c.prepareStatement(insert, Statement.RETURN_GENERATED_KEYS)) {
                    p.setInt(1, orderId);
                    p.setBigDecimal(2, order.getAmount());
                    p.setString(3, method);
                    p.setString(4, last4);
                    p.setString(5, ref);
                    p.setString(6, approved ? "SUCCESS" : "FAILED");
                    p.setString(7, approved ? null : failureReason);
                    p.executeUpdate();
                    try (ResultSet keys = p.getGeneratedKeys()) {
                        if (!keys.next()) throw new SQLException("Payment transaction could not be created.");
                        paymentId = keys.getInt(1);
                    }
                }

                if (approved) {
                    try (PreparedStatement p = c.prepareStatement("UPDATE PaymentOrders SET OrderStatus='PAID',UpdatedAt=SYSDATETIME() WHERE OrderID=?")) {
                        p.setInt(1, orderId);
                        p.executeUpdate();
                    }
                } else {
                    try (PreparedStatement p = c.prepareStatement("INSERT INTO PaymentAuditFlags(PaymentID,Issue) VALUES(?,?)")) {
                        p.setInt(1, paymentId);
                        p.setString(2, "Gateway authorization failed: " + (failureReason == null ? "Unknown reason" : failureReason));
                        p.executeUpdate();
                    }
                }
                c.commit();
                return ref;
            } catch (SQLException e) {
                c.rollback();
                throw e;
            } finally {
                c.setAutoCommit(true);
            }
        }
    }

    public Payment findPaymentByRef(String ref) throws SQLException {
        String sql = basePaymentQuery() + " WHERE p.TransactionRef=? ORDER BY p.PaymentID DESC";
        try (Connection c = DBConnection.getConnection(); PreparedStatement p = c.prepareStatement(sql)) {
            p.setString(1, ref);
            try (ResultSet r = p.executeQuery()) { return r.next() ? mapPayment(r) : null; }
        }
    }

    public List<Payment> historyForOrder(int orderId) throws SQLException {
        String sql = basePaymentQuery() + " WHERE p.OrderID=? ORDER BY p.PaidAt DESC";
        List<Payment> list = new ArrayList<>();
        try (Connection c = DBConnection.getConnection(); PreparedStatement p = c.prepareStatement(sql)) {
            p.setInt(1, orderId);
            try (ResultSet r = p.executeQuery()) { while (r.next()) list.add(mapPayment(r)); }
        }
        return list;
    }

    public List<Payment> allPayments() throws SQLException {
        List<Payment> list = new ArrayList<>();
        try (Connection c = DBConnection.getConnection(); PreparedStatement p = c.prepareStatement(basePaymentQuery() + " ORDER BY p.PaidAt DESC"); ResultSet r = p.executeQuery()) {
            while (r.next()) list.add(mapPayment(r));
        }
        return list;
    }

    public void flag(int paymentId, String issue) throws SQLException {
        if (issue == null || issue.isBlank()) throw new SQLException("Enter an audit issue before flagging the transaction.");
        try (Connection c = DBConnection.getConnection(); PreparedStatement p = c.prepareStatement("INSERT INTO PaymentAuditFlags(PaymentID,Issue) VALUES(?,?)")) {
            p.setInt(1, paymentId);
            p.setString(2, issue.trim());
            p.executeUpdate();
        }
    }

    public void resolveLatestOpenFlag(int paymentId) throws SQLException {
        String sql = "UPDATE PaymentAuditFlags SET Status='RESOLVED',ResolvedAt=SYSDATETIME() WHERE FlagID=(SELECT TOP 1 FlagID FROM PaymentAuditFlags WHERE PaymentID=? AND Status='OPEN' ORDER BY FlaggedAt DESC)";
        try (Connection c = DBConnection.getConnection(); PreparedStatement p = c.prepareStatement(sql)) {
            p.setInt(1, paymentId);
            p.executeUpdate();
        }
    }

    public void refund(int paymentId) throws SQLException {
        try (Connection c = DBConnection.getConnection()) {
            c.setAutoCommit(false);
            try {
                Integer orderId = null;
                String status = null;
                try (PreparedStatement p = c.prepareStatement("SELECT OrderID,GatewayStatus FROM Payments WITH (UPDLOCK,HOLDLOCK) WHERE PaymentID=?")) {
                    p.setInt(1, paymentId);
                    try (ResultSet r = p.executeQuery()) {
                        if (!r.next()) throw new SQLException("Transaction not found.");
                        orderId = r.getInt("OrderID");
                        status = r.getString("GatewayStatus");
                    }
                }
                if (!"SUCCESS".equals(status)) throw new SQLException("Only successful transactions can be refunded.");
                try (PreparedStatement p = c.prepareStatement("UPDATE Payments SET GatewayStatus='REFUNDED' WHERE PaymentID=?")) { p.setInt(1, paymentId); p.executeUpdate(); }
                try (PreparedStatement p = c.prepareStatement("UPDATE PaymentOrders SET OrderStatus='REFUNDED',UpdatedAt=SYSDATETIME() WHERE OrderID=?")) { p.setInt(1, orderId); p.executeUpdate(); }
                c.commit();
            } catch (SQLException e) {
                c.rollback();
                throw e;
            } finally { c.setAutoCommit(true); }
        }
    }

    private String basePaymentQuery() {
        return "SELECT p.*,o.BookingReference,o.PassengerName," +
                "CASE WHEN af.FlagID IS NULL THEN 0 ELSE 1 END AS Flagged,af.Issue AS AuditIssue,af.Status AS AuditStatus " +
                "FROM Payments p JOIN PaymentOrders o ON o.OrderID=p.OrderID " +
                "OUTER APPLY (SELECT TOP 1 f.FlagID,f.Issue,f.Status FROM PaymentAuditFlags f WHERE f.PaymentID=p.PaymentID ORDER BY CASE WHEN f.Status='OPEN' THEN 0 ELSE 1 END,f.FlaggedAt DESC) af";
    }

    private PaymentOrder mapOrder(ResultSet r) throws SQLException {
        PaymentOrder o = new PaymentOrder();
        o.setOrderId(r.getInt("OrderID"));
        o.setBookingReference(r.getString("BookingReference"));
        o.setPassengerName(r.getString("PassengerName"));
        o.setEmail(r.getString("Email"));
        o.setRouteSummary(r.getString("RouteSummary"));
        o.setAmount(r.getBigDecimal("Amount"));
        o.setOrderStatus(r.getString("OrderStatus"));
        o.setCreatedAt(r.getTimestamp("CreatedAt"));
        o.setUpdatedAt(r.getTimestamp("UpdatedAt"));
        return o;
    }

    private Payment mapPayment(ResultSet r) throws SQLException {
        Payment p = new Payment();
        p.setPaymentId(r.getInt("PaymentID"));
        p.setOrderId(r.getInt("OrderID"));
        p.setBookingReference(r.getString("BookingReference"));
        p.setPassengerName(r.getString("PassengerName"));
        p.setAmount(r.getBigDecimal("Amount"));
        p.setMethod(r.getString("Method"));
        p.setCardLast4(r.getString("CardLast4"));
        p.setTransactionRef(r.getString("TransactionRef"));
        p.setGatewayStatus(r.getString("GatewayStatus"));
        p.setFailureReason(r.getString("FailureReason"));
        p.setPaidAt(r.getTimestamp("PaidAt"));
        p.setFlagged(r.getInt("Flagged") == 1);
        p.setAuditIssue(r.getString("AuditIssue"));
        p.setAuditStatus(r.getString("AuditStatus"));
        return p;
    }
}
