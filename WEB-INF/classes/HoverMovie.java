

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jdk.nashorn.internal.parser.Token;

import javax.servlet.ServletException;
// import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

// @WebServlet("/HoverMovie")
public class HoverMovie extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		PrintWriter out = response.getWriter();
		
        String userName = "mytestuser";
        String userPassword = "mypassword";
        String loginUrl = "jdbc:mysql:///moviedb?autoReconnect=true&useSSL=false";
		
	    out.println("<BODY ALIGN = \"CENTER\">");
	    out.println("<TABLE>");

		try{
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			Connection dbcon = DriverManager.getConnection(loginUrl, userName, userPassword);	
		    
			String query = "SELECT * FROM `movies` WHERE `movies`.`id`='" + request.getParameter("q") + "'";
			Statement statement = dbcon.createStatement();
			ResultSet rs = statement.executeQuery(query);
        	ResultSet rs2 = null;
        	ResultSet rs3 = null;
        	
			while(rs.next())
			{
            	// Selects all stars in a movie
            	/* SELECT s.first_name, s.last_name
    			 * FROM movies as m, stars as s, stars_in_movies
    			 * WHERE m.id = movie_id AND s.id = star_id AND m.id = (movie id)
    			 */
            	String query2 = 
            			"SELECT `s`.`first_name`, `s`.`last_name`, `s`.`id` " + 
            			"FROM `movies` as m, `stars` as s, `stars_in_movies` as sim " +
            			"WHERE `m`.`ID` = `sim`.`movie_id` AND `s`.`id` = `sim`.`star_id` AND `m`.`ID` = " + rs.getString("id");
            	
            	// Selects genre of a movie given movie ID
            	/* SELECT g.name
    			 * FROM movies as m, genre as g, genre_in_movies
    			 * WHERE m.id = movie_id AND g.id = genre_id AND m.id = (movie_id)
    			 */
            	String query3 = 
            			"SELECT `g`.`name` " + 
            			"FROM `movies` as m, `genres` as g, `genres_in_movies` as gim " + 
            			"WHERE `m`.`ID` = `gim`.`movie_id` AND `g`.`id` = `gim`.`genre_id` AND `m`.`ID` = " + rs.getString("id");
            	
            	Statement statement2 = dbcon.createStatement();
            	Statement statement3 = dbcon.createStatement();
            	rs2 = statement2.executeQuery(query2);
            	rs3 = statement3.executeQuery(query3);
            	
        		out.println("<TABLE align =\"center\" style=\"width:100%\"><tr>");
            	out.println("<td align=\"center\" rowspan=\"8\" width=\"30%\">");
            	String url = "/FabFlix/Movie?id=" + rs.getString("id");
            	out.println("<a href=\"" + url + "\"><img src=\"" + rs.getString("banner_url") + "\" style=\"width:80px;height:120px;\"></a><br><br>");
            	out.println("<a href=\"/FabFlix/AddToCart?movie_id=" + rs.getString("id") +"\" target=\"_blank\">Add to Cart</a>");
            	out.println("<td align=\"left\" width=\"15%\">");
            	out.println("Title: </td><td width=\"55%\">");
            	out.println("<a href=\"" + url + "\"</a>" + rs.getString("title") + "</td></tr>");
            	out.println("</td><td>");
            	out.println("Year: </td><td>");
            	out.println(rs.getString("year") + "</td></tr>");
            	out.println("</td><td>");
            	out.println("Director: </td><td>");
            	out.println(rs.getString("director") + "</td></tr>");
            	out.println("</td><td>");
            	out.println("MovieID: </td><td>");
            	out.println(rs.getString("id") + "</td></tr>");
            	out.println("</td><td>");
            	out.println("Starring: </td><td>");
            	while(rs2.next()) // Iterate through stars and hyperlink to personal pages
            		out.println("<a href=\"/FabFlix/Star?starid=" + rs2.getString("ID") + "\">" + rs2.getString("first_name") + " " + rs2.getString("last_name") + "</a>" + "   ");
            	out.println("</td></tr></td><td>");            
            	out.println("Genre: </td><td>");
            	while(rs3.next()) // Iterate through genres and hyperlink to search by genre
            		out.println("<a href=\"/FabFlix/Browse?genre=" + rs3.getString("name") + "\">" + rs3.getString("name") + "</a>" + "   ");
            
            	out.println("</td></tr></td><td>");
            	out.println("Price: </td><td>");
            	out.println("PRICE</td></tr>");
            	out.println("</td><td>");
            	out.println("</td><td>");
            	out.println("<a href=\"" + rs.getString("trailer_url") + "\">Watch Trailer</a></td><td>");
            	out.println("</td><td>");
            	out.println("</td></tr></TABLE><br><br>");
            	
                rs2.close();
                rs3.close();
			}
		}
		catch(Exception e)
		{
			System.out.println(e.getMessage());
		}
		
		out.println("</TABLE>");
		out.println("</BODY></HTML>");		
	}
}
