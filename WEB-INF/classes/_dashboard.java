
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
// import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

// @WebServlet("/_dashboard")
public class _dashboard extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	
		PrintWriter out = response.getWriter();
		
		boolean loggedIn = Boolean.parseBoolean(request.getParameter("logged"));
		String message = request.getParameter("message");
		
		if(loggedIn)
		{		
			String userName = "mytestuser";
			String userPassword = "mypassword";
			String loginUrl = "jdbc:mysql:///moviedb?autoReconnect=true&useSSL=false";
        
			try {            
				Class.forName("com.mysql.jdbc.Driver").newInstance();
            
				Connection dbcon = DriverManager.getConnection(loginUrl, userName, userPassword);
            
				// Declare our statement
				Statement statement = dbcon.createStatement();
				statement.setQueryTimeout(15);
            
				out.println("<HTML><HEAD><TITLE>FabFlix Dashboard</TITLE></HEAD><BODY>");
				out.println("<P ALIGN=\"CENTER\"> <H1>FabFlix (Admin Dashboard)</H1> </P>");
            
				// Display options
				// Add Star
				out.println("<input type=\"button\" value=\"Add Star\" onclick=\"showAddStarDiv()\" />");
            
				// Show Metadata
				out.println("<input type=\"button\" value=\"Show Metadata\" onclick=\"showMetadataDiv()\" />");
            
				// Add Movie Info
				out.println("<input type=\"button\" value=\"Add Movie Info\" onclick=\"showAddMovieDiv()\" />");

				// DISPLAY MESSAGE IF ANY
				if(message != null)
					out.println("<br>" + message + "<br>");
				
				// INSERT NEW STAR
				out.println("<div id=\"addStarDiv\"  style=\"display:none;\">");
				out.println("<TABLE><TR><TD>");
				out.println("INSERT NEW STAR</TD></TR><TR><TD>");
				out.println("<FORM ACTION=\"/FabFlix/addStar\" METHOD=\"POST\">");
				out.println("<TR><TD>Name: </TD><TD><input type=\"text\" name=\"name\"></TD></TR>");
				out.println("<tr><td>Date of Birth (yyyy-mm-dd):</td><td><input type=\"text\" name=\"date_of_birth\"></td></tr>");
				out.println("<tr><td>Photo URL:</td><td><input type=\"text\" name=\"photo_url\"></td></tr>");
				out.println("<tr><td></td><td><input type=\"submit\" value=\"Submit\"</td></tr></TABLE>");            
				out.println("</FORM>");
				out.println("</div>");
            
				out.println("<script>");
				out.println("function showAddStarDiv() {");
				out.println("document.getElementById('addStarDiv').style.display = \"block\";");
				out.println("document.getElementById('showMetaDataDiv').style.display = \"none\";");
				out.println("document.getElementById('addMovieDiv').style.display = \"none\";");
				out.println("} ");
				out.println("</script>");
		
				// DISPLAY METADATA
				out.println("<div id=\"showMetaDataDiv\"  style=\"display:none;\">");
				out.println("<TABLE><TR><TD>");
				out.println("DATABASE METADATA</TD></TR><TR><TD>");
            
				ResultSet result = null;
				
				DatabaseMetaData dbMetadata = dbcon.getMetaData();
				ResultSet dbResult = dbMetadata.getTables(null, null, "%", null);
    			
				String tableName = null;
				Statement select = dbcon.createStatement();
				ResultSetMetaData tableMetadata = null;
    			
				while(dbResult.next())
				{
					// For every dbtable, create an htmltable for display
					out.println("<TABLE><TR><TD>");
   				
					tableName = dbResult.getString(3);
   				
					select = dbcon.createStatement();
					result = select.executeQuery("Select * from " + tableName);
    			
					tableMetadata = result.getMetaData();
					out.println("Table: " + tableName + "</TD></TR>");
   				  				
					for(int i = 1; i <= tableMetadata.getColumnCount(); ++i)
						out.println("<TR><TD> " + tableMetadata.getColumnName(i) + "(" + tableMetadata.getColumnTypeName(i) + ")</TD></TR>");
   					
					out.println("</TD></TR></TABLE><br>");
				}			

				out.println("</TD></TR></TABLE>");
				out.println("</div>");
            
				out.println("<script>");
				out.println("function showMetadataDiv() {");
				out.println("document.getElementById('addStarDiv').style.display = \"none\";");
				out.println("document.getElementById('showMetaDataDiv').style.display = \"block\";");
				out.println("document.getElementById('addMovieDiv').style.display = \"none\";");            out.println("} ");
				out.println("</script>");
                       
				// Insert new movie
				out.println("<div id=\"addMovieDiv\"  style=\"display:none;\">");
				out.println("<TABLE><TR><TD>ADD MOVIE INFORMATION</TD></TR>");
				out.println("<FORM ACTION=\"/FabFlix/addMovie\" METHOD=\"POST\">");
				out.println("<TR><TD>Title: </TD><TD><input type=\"text\" name=\"movieTitle\"></TD></TR>");
				out.println("<TR><TD>Year: </TD><TD><input type=\"text\" name=\"movieYear\"></TD></TR>");
				out.println("<TR><TD>Director: </TD><TD><input type=\"text\" name=\"movieDirector\"></TD></TR>");
				out.println("<TR><TD>Trailer URL: </TD><TD><input type=\"text\" name=\"movieTrailer\"></TD></TR>");
				out.println("<TR><TD>Banner URL: </TD><TD><input type=\"text\" name=\"movieBanner\"></TD></TR>");
				out.println("<TR><TD>Genre: </TD><TD><input type=\"text\" name=\"movieGenre\"></TD></TR>");
				out.println("<TR><TD>Star Name: </TD><TD><input type=\"text\" name=\"starName\"></TD></TR>");
				out.println("<TR><TD>Star Date of Birth (yyyy-mm-dd): </TD><TD><input type=\"text\" name=\"starDOB\"></TD></TR>");
				out.println("<TR><TD>Star Photo URL: </TD><TD><input type=\"text\" name=\"starPURL\"></TD></TR>");
				
				out.println("<tr><td></td><td><input type=\"submit\" value=\"Submit\"</td></tr></TABLE>");
				out.println("</FORM>");
				out.println("</div>");
            
				out.println("<script>");
				out.println("function showAddMovieDiv() {");
				out.println("document.getElementById('addStarDiv').style.display = \"none\";");
				out.println("document.getElementById('showMetaDataDiv').style.display = \"none\";");
				out.println("document.getElementById('addMovieDiv').style.display = \"block\";");
				out.println("} ");
				out.println("</script>");
            
				out.println("</BODY></HTML>");
			}
			catch(Exception e)
			{
				out.println(e);
			}
		}
		else
		{
			// Display login and direct to AdminLogin servlet
			out.println("<!doctype html>");
			out.println("<html>");
			out.println("<head>");
			out.println("<meta charset=\"utf-8\">");
			out.println("<title>FabFlix Admin Login Page</title>");
			out.println("<link type=\"text/css\" rel=\"stylesheet\" href=\"style.css\"/>");
			out.println("<script src='https://www.google.com/recaptcha/api.js'></script>");
			out.println("</head>");
			out.println("<body>");
			out.println("<div align=\"center\">");
			out.println("<form action=\"/FabFlix/AdminLogin\" method=\"post\">");
			out.println("<p style=\"font-size:60px;font-family:Gotham, 'Helvetica Neue', Helvetica, Arial, 'sans-serif'\"> FabFlix Admin Login</p>");
			out.println("Username: <input type=\"text\" name=\"username\" id=\"username\" required=\"required\"><br><br>");
			out.println("Password: <input type=\"password\" name=\"password\" id=\"password\" required=\"required\"><br><br>");
			out.println("<div class=\"g-recaptcha\" data-sitekey=\"6Le7uSAUAAAAAB-FUfaH5OK0XRICh6N7P2ZrjqQA\"></div><br>");
			out.println("<input type=\"reset\" value=\"Reset\">");
			out.println("<input type=\"submit\" value=\"Submit\">");
			out.println("</form>");
			out.println("<br>");
			out.println("</div>");
			out.println("</body>");
			out.println("</html>");
		}
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
