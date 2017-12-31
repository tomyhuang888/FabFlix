
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/AdminLogin")
public class AdminLogin extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	
		PrintWriter out = response.getWriter();

		boolean reCaptchaValid = VerifyUtils.verify(request.getParameter("g-recaptcha-response"));
		if(!reCaptchaValid)
		{
			out.println("<HTML><HEAD><TITLE>FabFlix Admin Login Failure</TITLE>");
			out.println("<meta http-equiv=\"refresh\" content=\"0; url=/FabFlix/_dashboard\" />");
			out.println("</HEAD><BODY>");
			out.println("<p><a href=\"/FabFlix/_dashboard\">Recaptcha failed, redirect failed, click here to go back</a></p>");
			out.println("</BODY></HTML>");
		}
		else
		{
    	
			try
			{
			
				String userName = "mytestuser";
				String userPassword = "mypassword";
				String loginUrl = "jdbc:mysql:///moviedb?autoReconnect=true&useSSL=false";
				Class.forName("com.mysql.jdbc.Driver").newInstance();
				Connection dbcon = DriverManager.getConnection(loginUrl, userName, userPassword);

				String username = request.getParameter("username");
				String plaintextPasswordBADBOY = request.getParameter("password");
			
				// Check if key/value is in employees table in db
				Statement statement = dbcon.createStatement();
				statement.setQueryTimeout(15);
			
				String query = "SELECT * FROM employees WHERE email='" + username + "' AND "
					+ " password='" + plaintextPasswordBADBOY + "'";
			
				System.out.println("Searching employees for email='" + username + "' AND "
					+ " password='" + plaintextPasswordBADBOY + "'");
			
				ResultSet rs = statement.executeQuery(query);
		
				// If there isn't a result, redirect to _dashboard after displaying error
				if(!rs.isBeforeFirst())
				{
					out.println("<HTML><HEAD><TITLE>FabFlix Admin Login Failure</TITLE>");
					out.println("<meta http-equiv=\"refresh\" content=\"3; url=/FabFlix/_dashboard\" />");
					out.println("</HEAD><BODY>");
					out.println("<p><a href=\"/FabFlix/_dashboard\">Invalid Credentials, Click Here to Go Back (Redirected in 3 seconds).</a></p>");
					out.println("</BODY></HTML>");
				}
				else // Redirect to _dashboard with "logged=true" as post
				{
					out.println("<HTML><HEAD><TITLE>FabFlix Admin Login Success</TITLE>");
					out.println("</HEAD><BODY>");
					out.println("<form name='fr' action='/FabFlix/_dashboard' method='POST'>");
					out.println("<input type='hidden' name='logged' value='true'>");
					out.println("</form>");
					out.println("<script type='text/javascript'>");
					out.println("document.fr.submit();");
					out.println("</script>");
					out.println("</BODY></HTML>");
				}
			}
			catch(Exception e)
			{
				System.out.println(e);
			}
		}
	}

}
