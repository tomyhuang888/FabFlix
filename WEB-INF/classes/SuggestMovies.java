


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

// @WebServlet("/SuggestMovies")
public class SuggestMovies extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try
		{
			String userName = "mytestuser"; // TODO
	        String userPassword = "mypassword"; // TODO
	        String loginUrl = "jdbc:mysql:///moviedb?autoReconnect=true&useSSL=false";
	        Class.forName("com.mysql.jdbc.Driver").newInstance();
	        Connection dbcon = DriverManager.getConnection(loginUrl, userName, userPassword);
	        
	        PrintWriter out = response.getWriter();
	        
			// Get string from request and split into different words
			String[] tokens = (request.getParameter("q")).split(" ");
			
			// Cull short words "a, an, to, or, on, ... etc"
			// TODO must cull stop words instead of relying on length
			String[] culledTokens = new String[tokens.length];
			
			for(int i = 0, j = 0; i < tokens.length; ++i)
			{	
				// Do not cull last token, regardless of length
				if(i == tokens.length-1)
				{
					culledTokens[j] = tokens[i];
					++j;
				}
				else if(tokens[i].length() > 2)
				{
					culledTokens[j] = tokens[i];
					++j;
				}
			}
			
			// Form query using fulltext
	        Statement statement = dbcon.createStatement();
	        statement.setQueryTimeout(15);

	        String query = "SELECT * "
	        			 + "FROM `movies` "
	        			 + "WHERE MATCH(title) "
	        			 + "AGAINST('";
	        
	        for(int i = 0; i < culledTokens.length; ++i)
	        {
	        	if(culledTokens[i] != null)
	        	{
	        		// Omit space after last token
	        		if(i == culledTokens.length-1)
	        			query += "+" + culledTokens[i];
	        		else
	        			query += "+" + culledTokens[i] + " ";
	        	}
	        }
	        
	        query += "*' IN BOOLEAN MODE)";
	        
            ResultSet rs = statement.executeQuery(query);
            
            // Form HTML for suggestion box using query results
            out.println("<TABLE>");
            if(rs.isBeforeFirst())
            {
            	int i = 0;
            	while(rs.next()) // TODO each line should have a script associated for onhover to bring up movie info
            	{
            		out.println("<TR><TD><a href=\"" + "/FabFlix/Movie?id=" + 
            		rs.getString("id") + "\"</a>" + rs.getString("title") + "</TD>");
            	}
            }
            else
            {
            	out.println("<TABLE><TR><TD>No results</TD></TR></TABLE>");
            }
            out.println("</TABLE>");
		}
		catch(Exception e)
		{
			System.out.println(e);
		}
	}
}
