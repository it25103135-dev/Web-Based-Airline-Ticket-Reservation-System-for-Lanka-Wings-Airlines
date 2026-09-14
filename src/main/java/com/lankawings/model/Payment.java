package com.lankawings.model;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class Payment {
    private int paymentId;
    private int orderId;
    private String bookingReference;
    private String passengerName;
    private BigDecimal amount;
    private String method;
    private String cardLast4;
    private String transactionRef;
    private String gatewayStatus;
    private String failureReason;
    private Timestamp paidAt;
    private boolean flagged;
    private String auditIssue;
    private String auditStatus;

    public int getPaymentId() { return paymentId; }
    public void setPaymentId(int paymentId) { this.paymentId = paymentId; }
    public int getOrderId() { return orderId; }
    public void setOrderId(int orderId) { this.orderId = orderId; }
    public String getBookingReference() { return bookingReference; }
    public void setBookingReference(String bookingReference) { this.bookingReference = bookingReference; }
    public String getPassengerName() { return passengerName; }
    public void setPassengerName(String passengerName) { this.passengerName = passengerName; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public String getMethod() { return method; }
    public void setMethod(String method) { this.method = method; }
    public String getCardLast4() { return cardLast4; }
    public void setCardLast4(String cardLast4) { this.cardLast4 = cardLast4; }
    public String getTransactionRef() { return transactionRef; }
    public void setTransactionRef(String transactionRef) { this.transactionRef = transactionRef; }
    public String getGatewayStatus() { return gatewayStatus; }
    public void setGatewayStatus(String gatewayStatus) { this.gatewayStatus = gatewayStatus; }
    public String getFailureReason() { return failureReason; }
    public void setFailureReason(String failureReason) { this.failureReason = failureReason; }
    public Timestamp getPaidAt() { return paidAt; }
    public void setPaidAt(Timestamp paidAt) { this.paidAt = paidAt; }
    public boolean isFlagged() { return flagged; }
    public void setFlagged(boolean flagged) { this.flagged = flagged; }
    public String getAuditIssue() { return auditIssue; }
    public void setAuditIssue(String auditIssue) { this.auditIssue = auditIssue; }
    public String getAuditStatus() { return auditStatus; }
    public void setAuditStatus(String auditStatus) { this.auditStatus = auditStatus; }
}
