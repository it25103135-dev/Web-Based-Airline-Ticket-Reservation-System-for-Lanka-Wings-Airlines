package com.lankawings.servlet;
import com.lankawings.dao.BookingDAO;import com.lankawings.model.Booking;import jakarta.servlet.*;import jakarta.servlet.annotation.WebServlet;import jakarta.servlet.http.*;import java.io.IOException;
@WebServlet("/ticket") public class TicketServlet extends HttpServlet{
 protected void doGet(HttpServletRequest r,HttpServletResponse s)throws ServletException,IOException{try{Booking b=new BookingDAO().findByPnr(r.getParameter("pnr"));if(b==null){s.sendError(404);return;}r.setAttribute("booking",b);}catch(Exception e){r.setAttribute("error",e.getMessage());}r.getRequestDispatcher("/ticket.jsp").forward(r,s);}
}
