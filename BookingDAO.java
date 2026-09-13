package com.lankawings.dao;

import com.lankawings.config.DBConnection;
import com.lankawings.model.Booking;
import java.sql.*;
import java.util.*;

public class BookingDAO {
    private static final String BASE =
        "SELECT b.*,f.FlightNo,f.Origin,f.Destination,f.DepartureTime,f.ArrivalTime,f.Fare " +
        "FROM Bookings b JOIN Flights f ON b.FlightID=f.FlightID";

    public List<Booking> listByPassport(String passportNo) throws SQLException {
        return list(BASE + " WHERE UPPER(b.PassportNo)=UPPER(?) ORDER BY b.BookedAt DESC", passportNo);
    }

    public List<Booking> all() throws SQLException {
        return list(BASE + " ORDER BY b.BookedAt DESC");
    }

    public Booking find(int id) throws SQLException {
        List<Booking> rows = list(BASE + " WHERE b.BookingID=?", id);
        return rows.isEmpty() ? null : rows.get(0);
    }

    public Booking findByPnr(String pnr) throws SQLException {
        List<Booking> rows = list(BASE + " WHERE UPPER(b.PNR)=UPPER(?)", pnr);
        return rows.isEmpty() ? null : rows.get(0);
    }

    public void updateDetails(int id, String passenger, String passport, String seat) throws SQLException {
        if (passenger == null || passenger.isBlank() || passport == null || passport.isBlank() || seat == null || seat.isBlank())
            throw new SQLException("Passenger name, passport/ID and seat are required.");
        String q = "UPDATE Bookings SET PassengerName=?, PassportNo=?, SeatNumber=?, UpdatedAt=SYSDATETIME() " +
                   "WHERE BookingID=? AND BookingStatus<>'CANCELLED'";
        try (Connection c=DBConnection.getConnection(); PreparedStatement p=c.prepareStatement(q)) {
            p.setString(1, passenger.trim());
            p.setString(2, passport.trim());
            p.setString(3, seat.trim().toUpperCase());
            p.setInt(4, id);
            int changed=p.executeUpdate();
            if(changed==0) throw new SQLException("Cancelled bookings cannot be modified.");
        } catch (SQLException e) {
            if (e.getMessage()!=null && e.getMessage().contains("UX_Bookings_ActiveSeat"))
                throw new SQLException("That seat is already assigned to another active booking on this flight.");
            throw e;
        }
    }

    public void cancel(int id) throws SQLException {
        String q="UPDATE Bookings SET BookingStatus='CANCELLED', UpdatedAt=SYSDATETIME() WHERE BookingID=? AND BookingStatus<>'CANCELLED'";
        try(Connection c=DBConnection.getConnection(); PreparedStatement p=c.prepareStatement(q)){
            p.setInt(1,id);
            p.executeUpdate();
        }
    }

    private List<Booking> list(String q, Object... args) throws SQLException {
        try(Connection c=DBConnection.getConnection(); PreparedStatement p=c.prepareStatement(q)){
            for(int i=0;i<args.length;i++) p.setObject(i+1,args[i]);
            try(ResultSet r=p.executeQuery()){
                List<Booking> out=new ArrayList<>();
                while(r.next()) out.add(map(r));
                return out;
            }
        }
    }

    private Booking map(ResultSet r) throws SQLException {
        Booking b=new Booking();
        b.setBookingId(r.getInt("BookingID"));
        b.setPnr(r.getString("PNR"));
        b.setFlightId(r.getInt("FlightID"));
        b.setPassengerName(r.getString("PassengerName"));
        b.setPassportNo(r.getString("PassportNo"));
        b.setSeatNumber(r.getString("SeatNumber"));
        b.setBookingStatus(r.getString("BookingStatus"));
        b.setBookedAt(r.getTimestamp("BookedAt"));
        b.setUpdatedAt(r.getTimestamp("UpdatedAt"));
        b.setFlightNo(r.getString("FlightNo"));
        b.setOrigin(r.getString("Origin"));
        b.setDestination(r.getString("Destination"));
        b.setDepartureTime(r.getTimestamp("DepartureTime"));
        b.setArrivalTime(r.getTimestamp("ArrivalTime"));
        b.setFare(r.getBigDecimal("Fare"));
        return b;
    }
}
