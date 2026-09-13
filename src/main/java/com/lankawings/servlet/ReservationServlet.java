package com.lankawings.servlet;
import com.lankawings.dao.*;import com.lankawings.model.Flight;import jakarta.servlet.*;import jakarta.servlet.annotation.WebServlet;import jakarta.servlet.http.*;import java.io.IOException;
@WebServlet("/reserve") public class ReservationServlet extends HttpServlet{
 protected void doGet(HttpServletRequest r,HttpServletResponse s)throws ServletException,IOException{try{Flight f=new FlightDAO().find(Integer.parseInt(r.getParameter("flightId")));if(f==null){s.sendError(404);return;}r.setAttribute("flight",f);r.setAttribute("bookedSeats",new BookingDAO().bookedSeats(f.getFlightId()));}catch(Exception e){r.setAttribute("error",e.getMessage());}r.getRequestDispatcher("/reserve.jsp").forward(r,s);}
 protected void doPost(HttpServletRequest r,HttpServletResponse s)throws ServletException,IOException{int fid=Integer.parseInt(r.getParameter("flightId"));try{String pnr=new BookingDAO().create(fid,r.getParameter("passengerName"),r.getParameter("passportNo"),r.getParameter("seatNumber"));s.sendRedirect(r.getContextPath()+"/ticket?pnr="+pnr+"&created=1");}catch(Exception e){r.setAttribute("error",e.getMessage());doGet(r,s);}}
}
