package com.lankawings.model;
import java.sql.Timestamp;
import java.math.BigDecimal;
public class Booking {
    private int bookingId,flightId; private String pnr,passengerName,passportNo,seatNumber,bookingStatus,flightNo,origin,destination; private Timestamp bookedAt,departureTime; private BigDecimal fare;
    public int getBookingId(){return bookingId;} public void setBookingId(int v){bookingId=v;}
    public int getFlightId(){return flightId;} public void setFlightId(int v){flightId=v;}
    public String getPnr(){return pnr;} public void setPnr(String v){pnr=v;}
    public String getPassengerName(){return passengerName;} public void setPassengerName(String v){passengerName=v;}
    public String getPassportNo(){return passportNo;} public void setPassportNo(String v){passportNo=v;}
    public String getSeatNumber(){return seatNumber;} public void setSeatNumber(String v){seatNumber=v;}
    public String getBookingStatus(){return bookingStatus;} public void setBookingStatus(String v){bookingStatus=v;}
    public String getFlightNo(){return flightNo;} public void setFlightNo(String v){flightNo=v;}
    public String getOrigin(){return origin;} public void setOrigin(String v){origin=v;}
    public String getDestination(){return destination;} public void setDestination(String v){destination=v;}
    public Timestamp getBookedAt(){return bookedAt;} public void setBookedAt(Timestamp v){bookedAt=v;}
    public Timestamp getDepartureTime(){return departureTime;} public void setDepartureTime(Timestamp v){departureTime=v;}
    public BigDecimal getFare(){return fare;} public void setFare(BigDecimal v){fare=v;}
}
