package com.lankawings.servlet;

import com.lankawings.dao.BookingDAO;
import com.lankawings.model.Booking;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebServlet("/ticket")
public class TicketServlet extends HttpServlet {
    protected void doGet(HttpServletRequest r,HttpServletResponse s)throws ServletException,IOException{
        try{
            Booking b=null;
            String id=r.getParameter("bookingId");
            String pnr=r.getParameter("pnr");
            BookingDAO dao=new BookingDAO();
            if(id!=null && !id.isBlank()) b=dao.find(Integer.parseInt(id));
            else if(pnr!=null && !pnr.isBlank()) b=dao.findByPnr(pnr);
            if(b==null) r.setAttribute("error","Booking not found.");
            else r.setAttribute("booking",b);
        }catch(Exception e){ r.setAttribute("error",e.getMessage()); }
        r.getRequestDispatcher("/ticket.jsp").forward(r,s);
    }
}
