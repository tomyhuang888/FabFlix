

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class addStar
 */
@WebServlet("/addStar")
public class addStar extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		// Add the star based on what's posted  
		String starName = request.getParameter("name");
        String starFName = "";
        String starLName = "";
		
		String [] splited = starName.split(" ");
		
		if(splited.length == 1)
		{
			starLName = splited[0];
			starFName = "";
		}
		else if(splited.length == 2)
		{
			starFName = splited[0];
			starLName = splited[1];
		}
		
        //String starFName = request.getParameter("first_name");
        //String starLName = request.getParameter("last_name");
        String starDOB = request.getParameter("date_of_birth");
        String starPURL = request.getParameter("photo_url");
        
        PrintWriter out = response.getWriter();
        
        // If any of the star information is invalid, redirect back to page w/ error
        if(starFName == "" || starLName == "" || starDOB == "" || starPURL == "")
        {
			out.println("<HTML><HEAD><TITLE>FabFlix Redirect</TITLE>");
			out.println("</HEAD><BODY>");
			out.println("<form name='fr' action='/FabFlix/_dashboard' method='POST'>");
			out.println("<input type='hidden' name='logged' value='true'>");
			out.println("<input type='hidden' name='message' value='Error: Not all necessary fields filled in, please make sure name and birthdate are provided.'>");
			out.println("</form>");
			out.println("<script type='text/javascript'>");
			out.println("document.fr.submit();");
			out.println("</script>");
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
                
                // Declare our statement
                Statement statement = dbcon.createStatement();
                statement.setQueryTimeout(15);
                
        		// Check if star already exists
                String query = "SELECT * FROM stars WHERE first_name='" + starFName +"' AND "
                		+ "last_name='"+ starLName +"' AND dob='"+ starDOB +"'";
                
                // Perform query, store in result set
                ResultSet rs = statement.executeQuery(query);
                
                // If there are no results, perform insertion query, display success and redirect
                if(!rs.isBeforeFirst())
            	{
                	String insertQuery = "INSERT INTO stars(first_name, last_name, dob, photo_url) VALUES("
                			+ "'" + starFName + "', '" + starLName + "', '" + starDOB + "', '" 
                			+ starPURL + "')";
                	
                	statement.executeUpdate(insertQuery);

        			out.println("<HTML><HEAD><TITLE>FabFlix Redirect</TITLE>");
        			out.println("</HEAD><BODY>");
        			out.println("<form name='fr' action='/FabFlix/_dashboard' method='POST'>");
        			out.println("<input type='hidden' name='logged' value='true'>");
        			out.println("<input type='hidden' name='message' value='Star Insertion Successful: " + starFName + " " + starLName +"'>");
        			out.println("</form>");
        			out.println("<script type='text/javascript'>");
        			out.println("document.fr.submit();");
        			out.println("</script>");
        			out.println("</BODY></HTML>");
            	}
                else // Otherwise display error and redirect back to dashboard
                {
        			out.println("<HTML><HEAD><TITLE>FabFlix Redirect</TITLE>");
        			out.println("</HEAD><BODY>");
        			out.println("<form name='fr' action='/FabFlix/_dashboard' method='POST'>");
        			out.println("<input type='hidden' name='logged' value='true'>");
        			out.println("<input type='hidden' name='message' value='Error: Star Already Exists: " + starFName + " " + starLName +"'>");
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
