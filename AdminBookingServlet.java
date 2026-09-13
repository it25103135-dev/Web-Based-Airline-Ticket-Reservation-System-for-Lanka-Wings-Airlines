package com.lankawings.servlet;

import com.lankawings.dao.BookingDAO;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebServlet("/admin/bookings")
public class AdminBookingServlet extends HttpServlet {
    protected void doGet(HttpServletRequest r,HttpServletResponse s)throws ServletException,IOException{
        try { r.setAttribute("bookings",new BookingDAO().all()); }
        catch(Exception e){ r.setAttribute("error",e.getMessage()); }
        r.getRequestDispatcher("/admin-bookings.jsp").forward(r,s);
    }

    protected void doPost(HttpServletRequest r,HttpServletResponse s)throws ServletException,IOException{
        BookingDAO dao=new BookingDAO();
        try{
            int id=Integer.parseInt(r.getParameter("id"));
            if("cancel".equals(r.getParameter("action"))) dao.cancel(id);
            else dao.updateDetails(id,r.getParameter("passengerName"),r.getParameter("passportNo"),r.getParameter("seatNumber"));
            s.sendRedirect(r.getContextPath()+"/admin/bookings?ok=1");
        }catch(Exception e){
            r.setAttribute("error",e.getMessage());
            doGet(r,s);
        }
    }
}
