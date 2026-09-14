package com.lankawings.model;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class PaymentOrder {
    private int orderId;
    private String bookingReference;
    private String passengerName;
    private String email;
    private String routeSummary;
    private BigDecimal amount;
    private String orderStatus;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    public int getOrderId() { return orderId; }
    public void setOrderId(int orderId) { this.orderId = orderId; }
    public String getBookingReference() { return bookingReference; }
    public void setBookingReference(String bookingReference) { this.bookingReference = bookingReference; }
    public String getPassengerName() { return passengerName; }
    public void setPassengerName(String passengerName) { this.passengerName = passengerName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getRouteSummary() { return routeSummary; }
    public void setRouteSummary(String routeSummary) { this.routeSummary = routeSummary; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public String getOrderStatus() { return orderStatus; }
    public void setOrderStatus(String orderStatus) { this.orderStatus = orderStatus; }
    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
    public Timestamp getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }
}
