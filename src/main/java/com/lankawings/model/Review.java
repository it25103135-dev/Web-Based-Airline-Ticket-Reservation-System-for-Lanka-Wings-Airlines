package com.lankawings.model;

import java.sql.Timestamp;

public class Review {
    private int reviewId;
    private String passengerName;
    private String email;
    private String bookingReference;
    private String flightNo;
    private int rating;
    private String title;
    private String comment;
    private String status;
    private String adminResponse;
    private Timestamp createdAt;
    private Timestamp respondedAt;

    public int getReviewId() { return reviewId; }
    public void setReviewId(int reviewId) { this.reviewId = reviewId; }
    public String getPassengerName() { return passengerName; }
    public void setPassengerName(String passengerName) { this.passengerName = passengerName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getBookingReference() { return bookingReference; }
    public void setBookingReference(String bookingReference) { this.bookingReference = bookingReference; }
    public String getFlightNo() { return flightNo; }
    public void setFlightNo(String flightNo) { this.flightNo = flightNo; }
    public int getRating() { return rating; }
    public void setRating(int rating) { this.rating = rating; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getAdminResponse() { return adminResponse; }
    public void setAdminResponse(String adminResponse) { this.adminResponse = adminResponse; }
    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
    public Timestamp getRespondedAt() { return respondedAt; }
    public void setRespondedAt(Timestamp respondedAt) { this.respondedAt = respondedAt; }
}
