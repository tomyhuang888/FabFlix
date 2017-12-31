
/* A servlet to display the contents of the MySQL movieDB database */

import java.io.*;
import java.net.*;
import java.sql.*;
import java.text.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;


// @WebServlet("/Login")
public class Login extends HttpServlet
{
    // public String getServletInfo()
    // {
    //    return "Servlet connects to MySQL database and displays result of a SELECT";
    // }
    boolean authentication = true;
    // Use http GET

    public void doGet(HttpServletRequest request, HttpServletResponse response)
        throws IOException, ServletException
    {
        String loginUser = "mytestuser";
        String loginPasswd = "mypassword";
        String loginUrl = "jdbc:mysql://localhost:3306/moviedb";
    //     // String loginUrl = "jdbc:mysql:///moviedb?autoReconnect=true&useSSL=false";
    //     // connection = DriverManager.getConnection("jdbc:mysql:///moviedb?autoReconnect=true&useSSL=false", userName, password);
        int id = -1;
        String username = null;
        String cookieValue = null;

        response.setContentType("text/html");    // Response mime type

        Cookie[] cookies = request.getCookies();
        if(cookies != null){
            for (int i = 0; i < cookies.length; i++) {
                if(cookies[i].getName().equals("fabflix_curruser")){
                    cookieValue = cookies[i].getValue();
                }
            }
        }
        try{
            Class.forName("com.mysql.jdbc.Driver").newInstance();

            // Connection dbcon = DriverManager.getConnection(loginUrl, loginUser, loginPasswd);
            Connection dbcon = DriverManager.getConnection(loginUrl, loginUser, loginPasswd);
            // Declare our statement
            Statement statement = dbcon.createStatement();
            if(cookieValue != null){
                String[] split = cookieValue.split("/");
                id = Integer.parseInt(split[0]);
                username = split[1];
                String session_query = "SELECT * FROM sessions WHERE customer_id = " + id + " AND email = \'" + username + "\'";
                ResultSet session_result = statement.executeQuery(session_query);
                if(session_result.next()){
                    response.sendRedirect("/FabFlix");
                }
            }
        }catch(Exception e){
            System.out.println(e.getMessage());
        }
    //     // Output stream to STDOUT
        PrintWriter out = response.getWriter();
        out.println("<HTML>");
        out.println("<HEAD><TITLE>FabFlix Login</TITLE>");
        out.println("<script src='https://www.google.com/recaptcha/api.js'></script>");
        out.println("</HEAD>");
        out.println("<BODY BGCOLOR=\"#FDF5E6\">");
        out.println("<H1 ALIGN=\"CENTER\">FabFlix Login</H1>");
        out.println("<FORM METHOD=\"POST\" ACTION=\"/FabFlix/Login\">");
        out.println("<span>Username: </span>  <INPUT id=\"username\" TYPE=\"TEXT\" NAME=\"username\"><BR>");
        out.println("<span>Password: </span> <INPUT id=\"password\" TYPE=\"PASSWORD\" NAME=\"password\"><BR>");
        out.println("<div class=\"g-recaptcha\" data-sitekey=\"6Le7uSAUAAAAAB-FUfaH5OK0XRICh6N7P2ZrjqQA\"></div><br>");
        out.println("<CENTER><INPUT id=\"login_button\" TYPE=\"SUBMIT\" VALUE=\"Login\"></CENTER>");
        if(request.getSession().getAttribute("login_auth")!= null){
          if(request.getSession().getAttribute("login_auth").equals("fail")){
              out.println("<p id=\"error\" style=\"color:red\"> Error: Incorrect Username/Password </p>");
              request.getSession().setAttribute("login_auth", "none");
          }else if(request.getSession().getAttribute("login_auth").equals("recaptcha")){
              out.println("<p id=\"error\" style=\"color:red\"> Error: ReCaptcha Failed </p>");
              request.getSession().setAttribute("login_auth", "none");
          }
        }
        out.println("</FORM></BODY></HTML>");
        out.close();
    }

      public void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws IOException, ServletException
      {
          boolean reCaptchaValid = VerifyUtils.verify(request.getParameter("g-recaptcha-response"));
          if(!reCaptchaValid)
          {
            request.getSession().setAttribute("login_auth", "recaptcha");
            response.sendRedirect("/FabFlix/Login");
          }
          String loginUser = "mytestuser";
          String loginPasswd = "mypassword";
          String loginUrl = "jdbc:mysql://localhost:3306/moviedb";

          authentication = false;

          PrintWriter out = response.getWriter();
          try{
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            Connection dbcon = DriverManager.getConnection(loginUrl, loginUser, loginPasswd);
            Statement statement = dbcon.createStatement();

            String username = request.getParameter("username");
            String password = request.getParameter("password");
            out.println("<HTML><HEAD><TITLE>Authentication</TITLE></HEAD>");

            String query = "SELECT * FROM customers WHERE email = \'" + username + "\' AND password = \'" + password + "\'";


            ResultSet rs = statement.executeQuery(query);
            
            if(rs.next()){
                String first_name = rs.getString("first_name");
                String last_name = rs.getString("last_name");

              // out.println("<p>success</p>");
              // out.println("<p>Welcome back " + first_name + " " + last_name + "!</p>");
              
              Cookie cookie = new Cookie("fabflix_curruser", Integer.toString(rs.getInt("id"))+"/"+ username);
              cookie.setMaxAge(Integer.MAX_VALUE);
              cookie.setPath("/");
              cookie.setComment("Fabflix Login Cookie");
              response.addCookie(cookie);
              int update = statement.executeUpdate("INSERT INTO sessions(customer_id, email) VALUES(" + rs.getString("id") + ",\'" + username + "\')");
              request.getSession().setAttribute("login_auth", "success");
              request.getSession().setAttribute("first_name", first_name);
              request.getSession().setAttribute("last_name", last_name);
              authentication = true;
              response.sendRedirect("/FabFlix");
            }else{
              // out.println("<p>failed</p>");
              request.getSession().setAttribute("login_auth", "fail");
              response.sendRedirect("/FabFlix/Login");
            }
          }catch (SQLException ex) {
                  while (ex != null) {
                        out.println ("<p>SQL Exception:  " + ex.getMessage () + "</p>");
                        ex = ex.getNextException ();
                    }  // end while
                }  // end catch SQLException

            catch(java.lang.Exception ex)
                {
                    out.println("<HTML>" +
                                "<HEAD><TITLE>" +
                                "MovieDB: Error" +
                                "</TITLE></HEAD>\n<BODY>" +
                                "<P>SQL error in doGet: " +
                                ex.getMessage() + "</P></BODY></HTML>");
                    return;
                }
          out.close();
      }
}
