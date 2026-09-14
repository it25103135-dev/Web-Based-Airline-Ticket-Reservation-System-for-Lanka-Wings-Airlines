package com.lankawings.dao;

import com.lankawings.config.DBConnection;
import com.lankawings.model.Review;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReviewDAO {
    public void create(Review review) throws SQLException {
        validate(review);
        String sql = "INSERT INTO FeedbackReviews(PassengerName,Email,BookingReference,FlightNo,Rating,Title,Comment) VALUES(?,?,?,?,?,?,?)";
        try (Connection c = DBConnection.getConnection(); PreparedStatement p = c.prepareStatement(sql)) {
            p.setString(1, review.getPassengerName().trim());
            p.setString(2, review.getEmail().trim().toLowerCase());
            p.setString(3, review.getBookingReference().trim().toUpperCase());
            p.setString(4, emptyToNull(review.getFlightNo()));
            p.setInt(5, review.getRating());
            p.setString(6, review.getTitle().trim());
            p.setString(7, review.getComment().trim());
            p.executeUpdate();
        }
    }

    public List<Review> listPublished() throws SQLException {
        return query("SELECT * FROM FeedbackReviews WHERE Status='PUBLISHED' ORDER BY CreatedAt DESC", null);
    }

    public List<Review> listAll() throws SQLException {
        return query("SELECT * FROM FeedbackReviews ORDER BY CreatedAt DESC", null);
    }

    public List<Review> findPassengerReviews(String email, String bookingReference) throws SQLException {
        String sql = "SELECT * FROM FeedbackReviews WHERE Email=? AND BookingReference=? ORDER BY CreatedAt DESC";
        return query(sql, p -> {
            p.setString(1, cleanEmail(email));
            p.setString(2, cleanReference(bookingReference));
        });
    }

    public void updatePassengerReview(int reviewId, String email, String bookingReference, int rating, String title, String comment) throws SQLException {
        if (rating < 1 || rating > 5) throw new SQLException("Rating must be between 1 and 5.");
        if (title == null || title.isBlank() || comment == null || comment.isBlank()) throw new SQLException("Title and feedback are required.");
        String sql = "UPDATE FeedbackReviews SET Rating=?,Title=?,Comment=?,UpdatedAt=SYSDATETIME() WHERE ReviewID=? AND Email=? AND BookingReference=?";
        try (Connection c = DBConnection.getConnection(); PreparedStatement p = c.prepareStatement(sql)) {
            p.setInt(1, rating);
            p.setString(2, title.trim());
            p.setString(3, comment.trim());
            p.setInt(4, reviewId);
            p.setString(5, cleanEmail(email));
            p.setString(6, cleanReference(bookingReference));
            if (p.executeUpdate() == 0) throw new SQLException("Review not found for the supplied passenger details.");
        }
    }

    public void deletePassengerReview(int reviewId, String email, String bookingReference) throws SQLException {
        String sql = "DELETE FROM FeedbackReviews WHERE ReviewID=? AND Email=? AND BookingReference=?";
        try (Connection c = DBConnection.getConnection(); PreparedStatement p = c.prepareStatement(sql)) {
            p.setInt(1, reviewId);
            p.setString(2, cleanEmail(email));
            p.setString(3, cleanReference(bookingReference));
            if (p.executeUpdate() == 0) throw new SQLException("Review not found for the supplied passenger details.");
        }
    }

    public void respond(int reviewId, String response) throws SQLException {
        if (response == null || response.isBlank()) throw new SQLException("Enter a response before submitting.");
        String sql = "UPDATE FeedbackReviews SET AdminResponse=?,RespondedAt=SYSDATETIME(),UpdatedAt=SYSDATETIME() WHERE ReviewID=?";
        try (Connection c = DBConnection.getConnection(); PreparedStatement p = c.prepareStatement(sql)) {
            p.setString(1, response.trim());
            p.setInt(2, reviewId);
            if (p.executeUpdate() == 0) throw new SQLException("Review not found.");
        }
    }

    public void setVisibility(int reviewId, boolean published) throws SQLException {
        try (Connection c = DBConnection.getConnection(); PreparedStatement p = c.prepareStatement("UPDATE FeedbackReviews SET Status=?,UpdatedAt=SYSDATETIME() WHERE ReviewID=?")) {
            p.setString(1, published ? "PUBLISHED" : "HIDDEN");
            p.setInt(2, reviewId);
            if (p.executeUpdate() == 0) throw new SQLException("Review not found.");
        }
    }

    public void deleteAdmin(int reviewId) throws SQLException {
        try (Connection c = DBConnection.getConnection(); PreparedStatement p = c.prepareStatement("DELETE FROM FeedbackReviews WHERE ReviewID=?")) {
            p.setInt(1, reviewId);
            if (p.executeUpdate() == 0) throw new SQLException("Review not found.");
        }
    }

    private List<Review> query(String sql, Binder binder) throws SQLException {
        List<Review> list = new ArrayList<>();
        try (Connection c = DBConnection.getConnection(); PreparedStatement p = c.prepareStatement(sql)) {
            if (binder != null) binder.bind(p);
            try (ResultSet r = p.executeQuery()) {
                while (r.next()) list.add(map(r));
            }
        }
        return list;
    }

    private void validate(Review r) throws SQLException {
        if (r.getPassengerName() == null || r.getPassengerName().isBlank()) throw new SQLException("Passenger name is required.");
        if (r.getEmail() == null || r.getEmail().isBlank() || !r.getEmail().contains("@")) throw new SQLException("Enter a valid email address.");
        if (r.getBookingReference() == null || r.getBookingReference().isBlank()) throw new SQLException("Booking reference is required.");
        if (r.getRating() < 1 || r.getRating() > 5) throw new SQLException("Rating must be between 1 and 5.");
        if (r.getTitle() == null || r.getTitle().isBlank()) throw new SQLException("Review title is required.");
        if (r.getComment() == null || r.getComment().isBlank()) throw new SQLException("Feedback comment is required.");
    }

    private Review map(ResultSet r) throws SQLException {
        Review v = new Review();
        v.setReviewId(r.getInt("ReviewID"));
        v.setPassengerName(r.getString("PassengerName"));
        v.setEmail(r.getString("Email"));
        v.setBookingReference(r.getString("BookingReference"));
        v.setFlightNo(r.getString("FlightNo"));
        v.setRating(r.getInt("Rating"));
        v.setTitle(r.getString("Title"));
        v.setComment(r.getString("Comment"));
        v.setStatus(r.getString("Status"));
        v.setAdminResponse(r.getString("AdminResponse"));
        v.setCreatedAt(r.getTimestamp("CreatedAt"));
        v.setRespondedAt(r.getTimestamp("RespondedAt"));
        return v;
    }

    private String cleanEmail(String value) throws SQLException {
        if (value == null || value.isBlank()) throw new SQLException("Email is required.");
        return value.trim().toLowerCase();
    }

    private String cleanReference(String value) throws SQLException {
        if (value == null || value.isBlank()) throw new SQLException("Booking reference is required.");
        return value.trim().toUpperCase();
    }

    private String emptyToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim().toUpperCase();
    }

    @FunctionalInterface
    private interface Binder { void bind(PreparedStatement p) throws SQLException; }
}
