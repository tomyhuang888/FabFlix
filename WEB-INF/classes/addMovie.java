

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.sql.CallableStatement;
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

/**
 * Servlet implementation class addMovie
 */
@WebServlet("/addMovie")
public class addMovie extends HttpServlet {
	private static final long serialVersionUID = 1L;

	// Checks if our procedure already exists, if it does, drop it
	protected void dropProcedure()
	{
		try
		{
            String userName = "mytestuser";
            String userPassword = "mypassword";
            String loginUrl = "jdbc:mysql:///moviedb?autoReconnect=true&useSSL=false";
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            Connection dbcon = DriverManager.getConnection(loginUrl, userName, userPassword);
            Statement statement = dbcon.createStatement();
            statement.execute("DROP PROCEDURE IF EXISTS `moviedb`.`add_movie`");
            statement.close();
            dbcon.close();
		}
		catch(Exception e)
		{
			System.out.println(e);
		}
	}
	
	// Creates our complicated stored procedure
	protected void createProcedure()
	{
		try
		{
			// Before we begin creating our statement, let's drop old procedure if exists
			dropProcedure();
			
			String userName = "mytestuser";
			String userPassword = "mypassword";
			String loginUrl = "jdbc:mysql:///moviedb?autoReconnect=true&useSSL=false";
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			Connection dbcon = DriverManager.getConnection(loginUrl, userName, userPassword);
			Statement statement = dbcon.createStatement();
			
			// Do the hard work...
			statement.execute("");
		}
		catch(Exception e)
		{
			System.out.println(e);
		}
	}
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		PrintWriter out = response.getWriter();
		
		String movieTitle = request.getParameter("movieTitle");
		String movieYear = request.getParameter("movieYear");
		String movieDirector = request.getParameter("movieDirector");
		String movieTrailerURL = request.getParameter("movieTrailer");
		String movieBannerURL = request.getParameter("movieBanner");
		String genre = request.getParameter("movieGenre");
		String starName = request.getParameter("starName");
		String starDOB = request.getParameter("starDOB");
		String starPURL = request.getParameter("starPURL");
		String starFirstName = "";
		String starLastName = "";
		
		String [] splited = starName.split(" ");
		
		if(splited.length == 1)
		{
			starLastName = splited[0];
			starFirstName = "";
		}
		else if(splited.length == 2)
		{
			starFirstName = splited[0];
			starLastName = splited[1];
		}
		
		// CALL STORED PROCEDURE
		try
		{
            // Overwrite stored procedure on mySQL side before preparing statement
            //createProcedure();
            
            String userName = "mytestuser";
            String userPassword = "mypassword";
            String loginUrl = "jdbc:mysql:///moviedb?autoReconnect=true&useSSL=false";
            
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            
            Connection dbcon = DriverManager.getConnection(loginUrl, userName, userPassword);
            
            // Declare our statement
            CallableStatement cs = null;
            cs = dbcon.prepareCall("{call add_movie(?,?,?,?,?,?,?,?,?,?,?)}");
            cs.setString(1, movieTitle);
            if(movieYear == "" || movieYear == null)
            	cs.setString(2, "0000");
            else
            	cs.setString(2, movieYear);
            cs.setString(3, movieDirector);
            cs.setString(4, movieBannerURL);
            cs.setString(5, movieTrailerURL);
            cs.setString(6, genre);
            cs.setString(7, starFirstName);
            cs.setString(8, starLastName);
            if(starDOB != "")
            	cs.setString(9, starDOB);
            else
            	cs.setString(9, "1800-01-01");
            cs.setString(10, starPURL);
            cs.setString(11, "");
            cs.execute();
            String output = cs.getString(11);
            cs.close();
            
            // Redirect with output
			out.println("<HTML><HEAD><TITLE>FabFlix Redirect</TITLE>");
			out.println("</HEAD><BODY>");
			out.println("<form name='fr' action='/FabFlix/_dashboard' method='POST'>");
			out.println("<input type='hidden' name='logged' value='true'>");
			out.println("<input type='hidden' name='message' value='" + output + "'>");
			out.println("</form>");
			out.println("<script type='text/javascript'>");
			out.println("document.fr.submit();");
			out.println("</script>");
			out.println("</BODY></HTML>");
		}
		catch(Exception e)
		{
		}
	}
}
