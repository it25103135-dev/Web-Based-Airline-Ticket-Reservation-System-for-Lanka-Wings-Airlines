package com.lankawings.servlet;
import com.lankawings.dao.FlightDAO;import jakarta.servlet.*;import jakarta.servlet.annotation.WebServlet;import jakarta.servlet.http.*;import java.io.IOException;
@WebServlet("/flights") public class FlightServlet extends HttpServlet{
 protected void doGet(HttpServletRequest r,HttpServletResponse s)throws ServletException,IOException{try{r.setAttribute("flights",new FlightDAO().search(r.getParameter("origin"),r.getParameter("destination"),r.getParameter("date")));}catch(Exception e){r.setAttribute("error",e.getMessage());}r.getRequestDispatcher("/flights.jsp").forward(r,s);}
}
