import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Connection;
import java.sql.DriverManager;
import java.io.PrintWriter;
import java.sql.Statement;
import java.sql.SQLException;

import javax.naming.Context;
import javax.servlet.ServletException;
import javax.naming.InitialContext;
import javax.sql.DataSource;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;

@WebServlet("/Star")
public class Star extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
    
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		String userName = "mytestuser"; // TODO
        String userPassword = "mypassword"; // TODO
        String loginUrl = "jdbc:mysql:///moviedb?autoReconnect=true&useSSL=false";
        
        response.setContentType("text/html");
	    PrintWriter out = response.getWriter();
	        
	    out.println("<HTML><HEAD><TITLE>Star Info</TITLE></HEAD>");
	    out.println("<BODY ALIGN = \"CENTER\" BGCOLOR=\"#FDF5E6\">");
		out.println("<P ALIGN=\"CENTER\"> <H1>FabFlix</H1> </P>");
		out.println("<TABLE width=\"90%\"><TR><TD width=\"80%\"></TD><TD>");
		out.println("<a href=\"/FabFlix/servlet/ShoppingCart\">CHECKOUT</a>");
		out.println("<a href=\"/FabFlix/\">LOGOUT</a>");
		out.println("</TD></TR></TABLE>");
	    out.println("<H1>Star Information:</H1>");

	        try
	        {
	        	Class.forName("com.mysql.jdbc.Driver").newInstance();
	        	
	            /*Context initCtx = new InitialContext();
	      		Context envCtx = (Context) initCtx.lookup("java:comp/env");
	      		DataSource ds = (DataSource) envCtx.lookup("jdbc/moviedb");*/
	      		
	      		//Connection dbcon = ds.getConnection();
	      		Connection dbcon = DriverManager.getConnection(loginUrl, userName, userPassword);
	      		Statement statement = dbcon.createStatement();

	            String s_ID = request.getParameter("starid");
	            String select_q = "SELECT * from stars WHERE id = ?";
	            PreparedStatement preparedstmt = dbcon.prepareStatement(select_q);
	            preparedstmt.setString(1, s_ID);
	            ResultSet rs = preparedstmt.executeQuery();

	            out.println("<TABLE ALIGN = \"CENTER\" border>");
	            while (rs.next())
	            {
	            	String star_l_n = rs.getString("last_name");
	                String star_f_n = rs.getString("first_name");
	                String star_dob = rs.getString("dob");
	                String star_p = rs.getString("photo_url");
	                  
	                out.println("<tr align=\"CENTER\"><td colspan=\"2\"><img src= \""+star_p+"\" alt=\""+star_f_n+" "+star_l_n+" Banner\" "+"width =\"170\" height=\"256\"></td></tr> <br><br>" );
	                out.println("<tr>"+"<td><p> <strong> Name: </strong>"+star_f_n +" "+star_l_n+"</td>"+"<td><p> <strong> Date of Birth: </strong>"+star_dob+"</td>"+"</tr>");
	            }
	            out.println("</TABLE>"); 
	            	            
	            String m_query = "SELECT  m.id, title, director, year FROM (movies m JOIN stars_in_movies sm "+"JOIN stars s "+"ON m.id = sm.movie_id AND sm.star_id = s.id) WHERE s.id=? AND sm.star_id = ?"; 
		        PreparedStatement m_p_s = dbcon.prepareStatement(m_query);
		        m_p_s.setString(1, s_ID);
		        m_p_s.setString(2, s_ID);
		        ResultSet m_r_s = m_p_s.executeQuery();

		        out.println("<p align=\"CENTER\"><strong> Starred in: </strong></p>");
		        out.println("<TABLE ALIGN = \"CENTER\" border>");
		        out.println("<tr><td>Movie Title</td><td>Director</td></tr>");
		        while(m_r_s.next())
		        {
		        	String m_i_d = m_r_s.getString("m.id");
		            String m_t = m_r_s.getString("m.title");
		            String m_d = m_r_s.getString("m.director");
		            String m_y = m_r_s.getString("m.year");
		            out.println("<tr ALIGN = \"CENTER\"><td><a href = \"/FabFlix/Movie?id=" + m_i_d + "\">"+m_t+" ("+m_y+") </a></td><td>"+m_d+"</td></tr>");	  
		        }
		        out.println("</TABLE>");
		          
		        m_r_s.close();
	            rs.close();
	            statement.close();
	            dbcon.close();
	        }
	        catch (SQLException ex)
	        {
	              while (ex != null)
	              {
	                    System.out.println ("SQL Exception:  "+ex.getMessage ());
	                    ex = ex.getNextException ();
	              }
	        }
	        catch(java.lang.Exception ex)
	        {
	                out.println("<HTML>"+"<HEAD><TITLE>"+"FabFlix Error: Unable to Retrieve Popular Titles, Please Contact Support (123)456-7890" + "</TITLE></HEAD>\n<BODY>"+
	                            "</TITLE></HEAD>\n<BODY>"+"<P>SQL error in doGet: "+ex.getMessage() + "</P></BODY></HTML>");
	                return;
	        }
	        /*
	        out.println("<p><a href=\"/FabFlix/ShoppingCart\">Shopping Cart</a></p>");
	        out.println("<p><a href=\"javascript:history.back()\">Back</a></p>");
        	out.println("<p><a href=\"/FabFlix/main.html\">Home</a></p>");
        	*/

	        out.close();
	}
}
