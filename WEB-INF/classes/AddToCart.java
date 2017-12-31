

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
 * Servlet implementation class AddToCart
 */
@WebServlet("/AddToCart")
public class AddToCart extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		PrintWriter out = response.getWriter();
		
        String userName = "mytestuser"; // TODO
        String userPassword = "mypassword"; // TODO
        String loginUrl = "jdbc:mysql:///moviedb?autoReconnect=true&useSSL=false";
        response.setContentType("text/html");
        
        try{
        	Class.forName("com.mysql.jdbc.Driver").newInstance();
        	Connection dbcon = DriverManager.getConnection(loginUrl, userName, userPassword);
        	Statement statement = dbcon.createStatement();
        	
        	int movieID = Integer.parseInt(request.getParameter("movie_id"));
        	int customerID = -1; // TODO
        	String email = "someEmail"; // TODO
        	
        	// Check if movie exists in cart
        	//SELECT movie_id
        	//FROM shoppingcarts
        	//WHERE customer_id = customer_id AND email = email AND movie_id = movie_idif movie does not exist in user’s shopping cart:
        	
        	String checkQuery =
        			"SELECT `movie_id` " +
        			"FROM `shoppingcarts` " +
        			"WHERE customer_id = '" + customerID + "' AND email = '" + email + "' AND movie_id = '" + movieID + "'";
        	
        	ResultSet rs1 = statement.executeQuery(checkQuery);
        	
        	if(!rs1.isBeforeFirst())
        	{
            	// If there isn't a result, add it into the cart
            	//INSERT INTO shoppingcarts(customer_id, email, movie_id, quantity) VALUES(customer_id, email, movie_id, 1)if movie exists in user’s shopping cart:
            	
            	// TODO THIS MIGHT NOT WORK, SYNTAX COULD BE OFF. 
            	String insertQuery = 
            			"INSERT INTO shoppingcarts(customer_id, email, movie_id, quantity) " +
            			"VALUES(" + customerID + ", " + email + ", " + movieID + ", 1)";
      
            	Statement statement2 = dbcon.createStatement();
            	statement2.executeUpdate(insertQuery);
        	}
        	else if(rs1.next())
        	{
            	// If there is a result, add to the quantity
            	//UPDATE shoppingcarts
            	//SET quantity = quantity+1
            	//WHERE customer_id = customer_id AND email = email AND movie_id = movie_id
            	
            	String updateQuery =
            			"UPDATE shoppingcarts " +
            			"SET quantity = quantity+1 " +
            			"WHERE customer_id = '" + customerID + "' AND email = '" + email + "' AND movie_id = '" + movieID + "'";

            	Statement statement3 = dbcon.createStatement();
    			statement3.executeUpdate(updateQuery);
        	}
        	
        }
        catch(Exception e)
        {
        	System.out.println("Something went very bad.");
        }
	}
}
