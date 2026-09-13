package com.lankawings.dao;
import com.lankawings.config.DBConnection;
import com.lankawings.model.Flight;
import java.sql.*;import java.util.*;
public class FlightDAO {
    private static final String SELECT="SELECT f.*, (SELECT COUNT(*) FROM Bookings b WHERE b.FlightID=f.FlightID AND b.BookingStatus<>'CANCELLED') AS BookedSeats FROM Flights f";
    public List<Flight> search(String origin,String destination,String date) throws SQLException{
        StringBuilder q=new StringBuilder(SELECT+" WHERE f.Status<>'CANCELLED'"); List<Object> a=new ArrayList<>();
        if(origin!=null&&!origin.isBlank()){q.append(" AND Origin LIKE ?");a.add("%"+origin.trim()+"%");}
        if(destination!=null&&!destination.isBlank()){q.append(" AND Destination LIKE ?");a.add("%"+destination.trim()+"%");}
        if(date!=null&&!date.isBlank()){q.append(" AND CAST(DepartureTime AS DATE)=?");a.add(java.sql.Date.valueOf(date));}
        q.append(" ORDER BY DepartureTime");
        try(Connection c=DBConnection.getConnection();PreparedStatement p=c.prepareStatement(q.toString())){for(int i=0;i<a.size();i++)p.setObject(i+1,a.get(i));try(ResultSet r=p.executeQuery()){List<Flight> l=new ArrayList<>();while(r.next())l.add(map(r));return l;}}
    }
    public Flight find(int id)throws SQLException{try(Connection c=DBConnection.getConnection();PreparedStatement p=c.prepareStatement(SELECT+" WHERE f.FlightID=?")){p.setInt(1,id);try(ResultSet r=p.executeQuery()){return r.next()?map(r):null;}}}
    private Flight map(ResultSet r)throws SQLException{Flight f=new Flight();f.setFlightId(r.getInt("FlightID"));f.setFlightNo(r.getString("FlightNo"));f.setOrigin(r.getString("Origin"));f.setDestination(r.getString("Destination"));f.setDepartureTime(r.getTimestamp("DepartureTime"));f.setArrivalTime(r.getTimestamp("ArrivalTime"));f.setFare(r.getBigDecimal("Fare"));f.setTotalSeats(r.getInt("TotalSeats"));f.setBookedSeats(r.getInt("BookedSeats"));f.setStatus(r.getString("Status"));f.setAircraft(r.getString("Aircraft"));return f;}
}
