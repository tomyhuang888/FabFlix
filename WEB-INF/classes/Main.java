

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.http.*;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.Connection;
import java.sql.ResultSet;

import javax.servlet.ServletException;
// import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class Main
 */
// @WebServlet("/Main")
public class Main extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String loginUser = "mytestuser"; // TODO
        String loginPasswd = "mypassword"; // TODO
        String loginUrl = "jdbc:mysql:///moviedb?autoReconnect=true&useSSL=false";
        int id = -1;
        String username = null;
        String cookieValue = null;

        Cookie[] cookies = request.getCookies();
        if(cookies == null){
                response.sendRedirect("/FabFlix/Login");
        }else{
            for (int i = 0; i < cookies.length; i++) {
                if(cookies[i].getName().equals("fabflix_curruser")){
                    cookieValue = cookies[i].getValue();
                }
            }
        }
        try{
            Class.forName("com.mysql.jdbc.Driver").newInstance();

            // Connection dbcon = DriverManager.getConnection(loginUrl, loginUser, loginPasswd);
            Connection dbcon = DriverManager.getConnection(loginUrl, loginUser, loginPasswd);
            // Declare our statement
            Statement statement = dbcon.createStatement();
            if(cookieValue != null){
                String[] split = cookieValue.split("/");
                id = Integer.parseInt(split[0]);
                username = split[1];
                String session_query = "SELECT * FROM sessions WHERE customer_id = " + id + " AND email = \'" + username + "\'";
                ResultSet session_result = statement.executeQuery(session_query);
                if(!session_result.next()){
                    response.sendRedirect("/FabFlix/Login");
                }
            }else{
                response.sendRedirect("/FabFlix/Login");
            }
        }catch(Exception e)
		{
			System.out.println(e.getMessage());
		}

		PrintWriter out = response.getWriter();
		out.println("<HTML><HEAD><TITLE>FabFlix</TITLE></HEAD><BODY>");
		
		out.println("<P ALIGN=\"CENTER\"> <H1>FabFlix</H1> </P>");

		out.println("<TABLE width=\"90%\"><TR><TD width=\"80%\"></TD><TD>");
		out.println("<a href=\"/FabFlix/ShoppingCart\">CHECKOUT</a>");
		out.println("<a href=\"/FabFlix/Logout\">LOGOUT</a>");
		out.println("</TD></TR></TABLE>");
		if(request.getSession().getAttribute("login_auth") != null){
			if(request.getSession().getAttribute("login_auth").equals("success")){
			// String first_name = request.getSession().getAttribute("first_name");
			// String last_name = request.getSession().getAttribute("last_name");
			out.println("<p>You have successfully logged on.  Welcome, " + 
				request.getSession().getAttribute("first_name") + " " 
				+ request.getSession().getAttribute("last_name") + "!</p>");
			request.getSession().setAttribute("login_auth", "none");
			}
		}
		if(request.getSession().getAttribute("cc_auth") != null){
			if(request.getSession().getAttribute("cc_auth").equals("success")){
				out.println("<p>Purchase Successful</p>");
				request.getSession().setAttribute("cc_auth", "none");
			}
		}

		out.println("<a href=\"Browse\"> Browse </a><br>");
		out.println("<a href=\"Search\"> Search </a><br>");
		
		out.println("</BODY></HTML>");
	}
}
