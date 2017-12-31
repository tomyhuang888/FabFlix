
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/Search")
public class Search extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		PrintWriter out = response.getWriter();
		
		out.println("<HTML><HEAD><TITLE>Search FabFlix</TITLE></HEAD><BODY BGCOLOR=\"#FDF5E6\">");
		out.println("<P ALIGN=\"CENTER\"> <H1>FabFlix</H1> </P>");
		out.println("<TABLE width=\"90%\"><TR><TD width=\"80%\"></TD><TD>");
		out.println("<a href=\"/FabFlix/servlet/ShoppingCart\">CHECKOUT</a>");
		out.println("<a href=\"/FabFlix/\">LOGOUT</a>");
		out.println("</TD></TR></TABLE>");
	    out.println("<TABLE><FORM ACTION=\"/FabFlix/Browse\" METHOD=\"get\">");
	    
		out.println("<tr><td>Title:</td><td><input type=\"text\" name=\"title\"></td></tr><br>");
		out.println("<tr><td>Year:</td><td><input type=\"text\" name=\"year\"></td></tr><br>");
		out.println("<tr><td>Director:</td><td><input type=\"text\" name=\"director\"></td></tr><br>");
		out.println("<tr><td>Star's First Name:</td><td><input type=\"text\" name=\"firstName\"></td></tr><br>");
		out.println("<tr><td>Star's Last Name:</td><td><input type=\"text\" name=\"lastName\"></td></tr><br>");
		out.println("<tr><td>Fuzzy Search</td><td><input type=\"checkbox\" name=\"fuzzy\" value=\"fuzzy\">(NOT IMPLEMENTED YET)</td></tr><br>"); // TODO
		out.println("<tr><td>Match Substring</td><td><input type=\"checkbox\" name=\"substring\" value=\"substring\" checked=\"true\"></td></tr><br>");
		out.println("<tr><td></td><td><input type=\"submit\" value=\"Submit\"</td></tr>");
		
	    out.println("</FORM></TABLE></BODY></HTML>");
	}
}
