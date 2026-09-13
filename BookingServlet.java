package com.lankawings.servlet;

import com.lankawings.dao.BookingDAO;
import com.lankawings.model.Booking;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@WebServlet("/bookings")
public class BookingServlet extends HttpServlet {
    protected void doGet(HttpServletRequest r, HttpServletResponse s) throws ServletException, IOException {
        BookingDAO dao=new BookingDAO();
        try {
            String passport=r.getParameter("passportNo");
            String pnr=r.getParameter("pnr");
            if(pnr!=null && !pnr.isBlank()) {
                Booking b=dao.findByPnr(pnr.trim());
                r.setAttribute("singleBooking", b);
                if(b==null) r.setAttribute("error", "No booking was found for that PNR.");
            } else if(passport!=null && !passport.isBlank()) {
                r.setAttribute("bookings", dao.listByPassport(passport.trim()));
                r.setAttribute("searchedPassport", passport.trim());
            }
        } catch(Exception e) {
            r.setAttribute("error", e.getMessage());
        }
        r.getRequestDispatcher("/bookings.jsp").forward(r,s);
    }

    protected void doPost(HttpServletRequest r, HttpServletResponse s) throws ServletException, IOException {
        BookingDAO dao=new BookingDAO();
        try {
            int id=Integer.parseInt(r.getParameter("id"));
            String action=r.getParameter("action");
            if("cancel".equals(action)) dao.cancel(id);
            else dao.updateDetails(id,r.getParameter("passengerName"),r.getParameter("passportNo"),r.getParameter("seatNumber"));

            String backPassport=r.getParameter("backPassport");
            String backPnr=r.getParameter("backPnr");
            if(backPassport!=null && !backPassport.isBlank())
                s.sendRedirect(r.getContextPath()+"/bookings?passportNo="+URLEncoder.encode(backPassport, StandardCharsets.UTF_8)+"&ok=1");
            else
                s.sendRedirect(r.getContextPath()+"/bookings?pnr="+URLEncoder.encode(backPnr==null?"":backPnr, StandardCharsets.UTF_8)+"&ok=1");
        } catch(Exception e) {
            r.setAttribute("error",e.getMessage());
            doGet(r,s);
        }
    }
}
