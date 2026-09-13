package com.lankawings.dao;
import com.lankawings.config.DBConnection;import com.lankawings.model.Booking;import java.sql.*;import java.util.*;
public class BookingDAO {
    private static final String BASE="SELECT b.*,f.FlightNo,f.Origin,f.Destination,f.DepartureTime,f.Fare FROM Bookings b JOIN Flights f ON b.FlightID=f.FlightID";
    public String create(int flightId,String passenger,String passport,String seat)throws SQLException{
        String cleanSeat=normalizeSeat(seat); String pnr="LW"+UUID.randomUUID().toString().replace("-","").substring(0,8).toUpperCase();
        try(Connection c=DBConnection.getConnection()){c.setAutoCommit(false);try{
            int totalSeats;
            try(PreparedStatement lock=c.prepareStatement("SELECT TotalSeats,Status,(SELECT COUNT(*) FROM Bookings WHERE FlightID=? AND BookingStatus<>'CANCELLED') Booked FROM Flights WITH (UPDLOCK,HOLDLOCK) WHERE FlightID=?")){lock.setInt(1,flightId);lock.setInt(2,flightId);try(ResultSet r=lock.executeQuery()){if(!r.next())throw new SQLException("Flight not found.");if("CANCELLED".equals(r.getString("Status")))throw new SQLException("This flight is cancelled.");if(r.getInt("Booked")>=r.getInt("TotalSeats"))throw new SQLException("No seats are available on this flight.");totalSeats=r.getInt("TotalSeats");}}
            validateSeat(cleanSeat,totalSeats);
            try(PreparedStatement s=c.prepareStatement("INSERT INTO Bookings(PNR,FlightID,PassengerName,PassportNo,SeatNumber,BookingStatus) VALUES(?,?,?,?,?,'CONFIRMED')")){s.setString(1,pnr);s.setInt(2,flightId);s.setString(3,passenger);s.setString(4,passport);s.setString(5,cleanSeat);s.executeUpdate();}
            c.commit();return pnr;
        }catch(SQLException e){c.rollback();if(e.getMessage()!=null&&(e.getMessage().contains("UX_Bookings_ActiveSeat")||e.getMessage().contains("duplicate key")))throw new SQLException("That seat is already booked. Please choose another seat.");throw e;}finally{c.setAutoCommit(true);}}
    }
    public Booking find(int id)throws SQLException{List<Booking> l=list(BASE+" WHERE b.BookingID=?",id);return l.isEmpty()?null:l.get(0);}
    public Booking findByPnr(String pnr)throws SQLException{if(pnr==null||pnr.isBlank())return null;List<Booking> l=list(BASE+" WHERE UPPER(b.PNR)=?",pnr.trim().toUpperCase());return l.isEmpty()?null:l.get(0);}
    public Set<String> bookedSeats(int flightId)throws SQLException{Set<String> seats=new HashSet<>();try(Connection c=DBConnection.getConnection();PreparedStatement p=c.prepareStatement("SELECT SeatNumber FROM Bookings WHERE FlightID=? AND BookingStatus<>'CANCELLED'")){p.setInt(1,flightId);try(ResultSet r=p.executeQuery()){while(r.next())seats.add(r.getString(1).toUpperCase());}}return seats;}
    public void updateDetails(int id,String passenger,String passport,String seat)throws SQLException{
        Booking b=find(id); if(b==null)throw new SQLException("Reservation not found."); if("CANCELLED".equals(b.getBookingStatus()))throw new SQLException("Cancelled reservations cannot be modified.");
        String cleanSeat=normalizeSeat(seat); int totalSeats;
        try(Connection c=DBConnection.getConnection();PreparedStatement f=c.prepareStatement("SELECT TotalSeats FROM Flights WHERE FlightID=?")){f.setInt(1,b.getFlightId());try(ResultSet r=f.executeQuery()){if(!r.next())throw new SQLException("Flight not found.");totalSeats=r.getInt(1);}}
        validateSeat(cleanSeat,totalSeats);
        try(Connection c=DBConnection.getConnection();PreparedStatement p=c.prepareStatement("UPDATE Bookings SET PassengerName=?,PassportNo=?,SeatNumber=? WHERE BookingID=?")){p.setString(1,passenger);p.setString(2,passport);p.setString(3,cleanSeat);p.setInt(4,id);p.executeUpdate();}
        catch(SQLException e){if(e.getMessage()!=null&&(e.getMessage().contains("UX_Bookings_ActiveSeat")||e.getMessage().contains("duplicate key")))throw new SQLException("That seat is already booked. Please choose another seat.");throw e;}
    }
    public void cancel(int id)throws SQLException{try(Connection c=DBConnection.getConnection();PreparedStatement p=c.prepareStatement("UPDATE Bookings SET BookingStatus='CANCELLED' WHERE BookingID=?")){p.setInt(1,id);if(p.executeUpdate()==0)throw new SQLException("Reservation not found.");}}
    private List<Booking> list(String q,Object...args)throws SQLException{try(Connection c=DBConnection.getConnection();PreparedStatement p=c.prepareStatement(q)){for(int i=0;i<args.length;i++)p.setObject(i+1,args[i]);try(ResultSet r=p.executeQuery()){List<Booking> l=new ArrayList<>();while(r.next())l.add(map(r));return l;}}}
    private String normalizeSeat(String seat)throws SQLException{if(seat==null||seat.isBlank())throw new SQLException("Please choose a seat.");return seat.trim().toUpperCase();}
    private void validateSeat(String seat,int totalSeats)throws SQLException{if(!seat.matches("[1-9][0-9]*[A-F]"))throw new SQLException("Invalid seat number.");int row=Integer.parseInt(seat.substring(0,seat.length()-1));int pos=seat.charAt(seat.length()-1)-'A'+1;int number=(row-1)*6+pos;if(number<1||number>totalSeats)throw new SQLException("That seat does not exist on this aircraft.");}
    private Booking map(ResultSet r)throws SQLException{Booking b=new Booking();b.setBookingId(r.getInt("BookingID"));b.setPnr(r.getString("PNR"));b.setFlightId(r.getInt("FlightID"));b.setPassengerName(r.getString("PassengerName"));b.setPassportNo(r.getString("PassportNo"));b.setSeatNumber(r.getString("SeatNumber"));b.setBookingStatus(r.getString("BookingStatus"));b.setBookedAt(r.getTimestamp("BookedAt"));b.setFlightNo(r.getString("FlightNo"));b.setOrigin(r.getString("Origin"));b.setDestination(r.getString("Destination"));b.setDepartureTime(r.getTimestamp("DepartureTime"));b.setFare(r.getBigDecimal("Fare"));return b;}
}
