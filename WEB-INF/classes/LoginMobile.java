import java.io.*;
import java.net.*;
import java.sql.*;
import java.text.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import com.google.gson.Gson;

// @WebServlet("/Login")
public class LoginMobile extends HttpServlet
{
	public void doGet(HttpServletRequest request, HttpServletResponse response)
        throws IOException, ServletException
    {
    	String username = request.getParameter("username");
        String password = request.getParameter("password");
        String loginUser = "mytestuser";
		String loginPasswd = "mypassword";
		String loginUrl = "jdbc:mysql://localhost:3306/moviedb";

		// response.setContentType("text/html");

		List<String> parameters = new ArrayList<String>();
		try{
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			Connection dbcon = DriverManager.getConnection(loginUrl, loginUser, loginPasswd);
			Statement statement = dbcon.createStatement();

			PreparedStatement query = dbcon.prepareStatement("SELECT * FROM customers WHERE email = ? AND password = ?;");
			// String query = "SELECT * FROM customers WHERE email = \'" + username + "\' AND password = \'" + password + "\'";
			query.setString(1, username);
			query.setString(2, password);

			ResultSet rs = query.executeQuery();
			
	    	
			if(rs.next()){
				int id = rs.getInt("id");
				String first_name = rs.getString("first_name");
				String last_name = rs.getString("last_name");
				parameters.add("true");
				parameters.add(String.valueOf(id));
			}else{
				parameters.add("false");
				parameters.add("");
			}
		}catch (SQLException ex) {
			while (ex != null) {
			    // out.println ("<p>SQL Exception:  " + ex.getMessage () + "</p>");
			    ex = ex.getNextException ();
			}  // end while
		} catch(Exception e){
            System.out.println(e.getMessage());
        }

        String json = new Gson().toJson(parameters);
        response.setContentType("application/json");
	    response.setCharacterEncoding("UTF-8");
	    response.getWriter().write(json);
    }
	public void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws IOException, ServletException
	{
		String username = request.getParameter("username");
        String password = request.getParameter("password");
        String loginUser = "mytestuser";
		String loginPasswd = "mypassword";
		String loginUrl = "jdbc:mysql://localhost:3306/moviedb";

		// response.setContentType("text/html");

		List<String> parameters = new ArrayList<String>();
		try{
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			Connection dbcon = DriverManager.getConnection(loginUrl, loginUser, loginPasswd);
			Statement statement = dbcon.createStatement();

			PreparedStatement query = dbcon.prepareStatement("SELECT * FROM customers WHERE email = ? AND password = ?;");
			// String query = "SELECT * FROM customers WHERE email = \'" + username + "\' AND password = \'" + password + "\'";
			query.setString(1, username);
			query.setString(2, password);

			ResultSet rs = query.executeQuery();
			
	    	
			if(rs.next()){
				int id = rs.getInt("id");
				String first_name = rs.getString("first_name");
				String last_name = rs.getString("last_name");
				parameters.add("true");
				parameters.add(String.valueOf(id));
			}else{
				parameters.add("false");
				parameters.add("");
			}
		}catch (SQLException ex) {
			while (ex != null) {
			    // out.println ("<p>SQL Exception:  " + ex.getMessage () + "</p>");
			    ex = ex.getNextException ();
			}  // end while
		} catch(Exception e){
            System.out.println(e.getMessage());
        }

        String json = new Gson().toJson(parameters);
        response.setContentType("application/json");
	    response.setCharacterEncoding("UTF-8");
	    response.getWriter().write(json);
	 
	}
}