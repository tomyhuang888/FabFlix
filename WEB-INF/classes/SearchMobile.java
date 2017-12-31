import java.io.*;
import java.net.*;
import java.sql.*;
import java.text.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import com.google.gson.Gson;

// @WebServlet("/Login")
public class SearchMobile extends HttpServlet
{
	public void doGet(HttpServletRequest request, HttpServletResponse response)
        throws IOException, ServletException
    {
    	String loginUser = "mytestuser";
		String loginPasswd = "mypassword";
		String loginUrl = "jdbc:mysql://localhost:3306/moviedb";
		
		// response.setContentType("text/html");
		String search_text = request.getParameter("search_text");
		List<String> parameters = new ArrayList<String>();
		try{
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			Connection dbcon = DriverManager.getConnection(loginUrl, loginUser, loginPasswd);
			Statement statement = dbcon.createStatement();
			String query_str = "SELECT title FROM movies WHERE";
			String[] str_parse = search_text.split(" ");

			for(int i = 0; i < str_parse.length; i++){
				if(i == 0){
					query_str = query_str + " MATCH (title) AGAINST (? IN BOOLEAN MODE)";
				}else{
					query_str = query_str + " AND MATCH (title) AGAINST (? IN BOOLEAN MODE)";
				}
			}
			query_str = query_str + ";";
			PreparedStatement query = dbcon.prepareStatement(query_str);
			for(int i = 0; i < str_parse.length; i++){
				String input_str;
				if(i == (str_parse.length -1) ){
					input_str = str_parse[i] + "*";
				}else{
					input_str = str_parse[i];
				}
				query.setString(i+1, input_str);
			}
		
			ResultSet rs = query.executeQuery();
			while(rs.next()){
				String title = rs.getString("title");
				parameters.add(title);
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
		String loginUser = "mytestuser";
		String loginPasswd = "mypassword";
		String loginUrl = "jdbc:mysql://localhost:3306/moviedb";
		
		// response.setContentType("text/html");
		String search_text = request.getParameter("search_text");
		List<String> parameters = new ArrayList<String>();
		try{
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			Connection dbcon = DriverManager.getConnection(loginUrl, loginUser, loginPasswd);
			Statement statement = dbcon.createStatement();
			String query_str = "SELECT title FROM movies WHERE";
			String[] str_parse = search_text.split(" ");

			for(int i = 0; i < str_parse.length; i++){
				if(i == 0){
					query_str = query_str + " MATCH (title) AGAINST (? IN BOOLEAN MODE)";
				}else{
					query_str = query_str + " AND MATCH (title) AGAINST (? IN BOOLEAN MODE)";
				}
			}
			query_str = query_str + ";";
			PreparedStatement query = dbcon.prepareStatement(query_str);
			for(int i = 0; i < str_parse.length; i++){
				String input_str;
				if(i == (str_parse.length -1) ){
					input_str = str_parse[i] + "*";
				}else{
					input_str = str_parse[i];
				}
				query.setString(i+1, input_str);
			}
		
			ResultSet rs = query.executeQuery();
			while(rs.next()){
				String title = rs.getString("title");
				parameters.add(title);
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