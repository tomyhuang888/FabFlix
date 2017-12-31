

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
// import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

// @WebServlet("/Browse")
public class Browse extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	HashMap<String, String> urlQuery = new HashMap<String, String>();
	
	void parseQuery(String query)
	{
        String[] colValArgPairs = query.split("=|&");
        
        String key;
        String value;
        
        for(int i = 0; i < colValArgPairs.length - 1; i += 2)
        	if(colValArgPairs[i+1] != null && colValArgPairs[i+1].length() != 0)
        	{
        		key = colValArgPairs[i];
        		value = colValArgPairs[i+1];
        		
        		try{
        			key = URLDecoder.decode(key, "UTF-8");
        			value = URLDecoder.decode(value, "UTF-8");
        			urlQuery.put(key, value);
        		}
        		catch(Exception e)
        		{
        			urlQuery.put(colValArgPairs[i], colValArgPairs[i+1]);
        		}
        	}
	}
	
	String updateQuery(String updateStr)
	{
		// First, split all args into pairs via delimiting by &
		String[] pairList = updateStr.split("&");
		String[] updateStrValArgPair;
		String newQuery = "";
		
		// For every pair of attributes and values
		for(int i = 0; i < pairList.length; ++i)
		{
			updateStrValArgPair = pairList[i].split("=");
			
			if(updateStrValArgPair.length == 2)
				urlQuery.put(updateStrValArgPair[0], updateStrValArgPair[1]);
			// else ignore singles
		}
 
		// Build new query line based on key value pairs in HashMap urlQuery
		ArrayList<String> colNames = new ArrayList<String>(urlQuery.keySet());

		for(int i = 0; i < colNames.size(); ++i)
		{
			newQuery = (newQuery + colNames.get(i) + "=" + urlQuery.get(colNames.get(i)));
			
			if(i+1 != colNames.size())
				newQuery = (newQuery + "&");
		}
		
		return newQuery;
	}
	
	int getPrevPage(int currPageNum)
	{
		if(currPageNum == 0)
			return currPageNum;
		else
			return currPageNum-1;
	}
	
	// TODO -- Need to be able to find the end of the results and disable if necessary
	int getNextPage(int currPageNum) 
	{
		if(currPageNum >= 0)
			return currPageNum+1;
		else
			return -1; // return error
	}
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String userName = "mytestuser"; // TODO
        String userPassword = "mypassword"; // TODO
        String loginUrl = "jdbc:mysql:///moviedb?autoReconnect=true&useSSL=false";
        
        response.setContentType("text/html"); // Response mime type

        int resultsPerPage;
        int currPageNum; 
        int titleSort; // 0 Ascending, 1 Descending
        int yearSort; // 0 Ascending, 1 Descending
        
        // Output stream to STDOUT
        PrintWriter out = response.getWriter();
        
        out.println("<HTML><HEAD><TITLE>Browsing Movies</TITLE></HEAD>");
		out.println("<P ALIGN=\"CENTER\"> <H1>FabFlix</H1> </P>");
		out.println("<TABLE width=\"90%\"><TR><TD width=\"80%\"></TD><TD>");
		out.println("<a href=\"/FabFlix/servlet/ShoppingCart\">CHECKOUT</a>");
		out.println("<a href=\"/FabFlix/\">LOGOUT</a>");
		out.println("</TD></TR></TABLE>");
		//out.println("<DIV id=\"temp\"></DIV>"); // For debugging javascript
        
        try {            
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            
            Connection dbcon = DriverManager.getConnection(loginUrl, userName, userPassword);
            // Declare our statement
            Statement statement = dbcon.createStatement();
            statement.setQueryTimeout(15);
            
            urlQuery.clear();
            
            try{
            	parseQuery(request.getQueryString());
            }
            catch(Exception e) // If no query in URL, display list of genres and letters/numbers for titles
            {
            	out.println("<BODY><TABLE ALIGN=\"CENTER\" style=\"width:90%\"><tr><td style=\"width:55%\" align=\"left\">");
            	out.println("<H1>Browse by Genre:</H1></td><td style=\"width:35%\" align=\"left\"><H1>Browse by Title:</H1></td></tr><tr><td>");
            	
                Class.forName("com.mysql.jdbc.Driver").newInstance();

                // Retrieve list of genres, sort alphabetically
                String query = "SELECT * FROM genres ORDER BY name";
                ResultSet rs = statement.executeQuery(query);

                out.println("<TABLE><tr><td>");
                
                // Iterate through each row of rs
                int i = 0;
                int rowThreshold = 8;
                while (rs.next()) {
                	String g_name = rs.getString("name");
                  
                	// Put in new div
                	if(i >= rowThreshold)
                	{
                		out.println("</td><td align=\"center\">");
                		i = 0;
                	}
                	if(i < rowThreshold)
                	{
                		// TODO, each genre listed should be a link that generates 
                		// a search based on the genre selected (will need ID)
                		out.println("<a href=\"Browse?genre=" + g_name + "\">" + g_name + "</a><br>");	
                	}
                	
                	i++;
                }

                rs.close();
                statement.close();
                dbcon.close();
                
                out.println("</td></tr></TABLE>");
            	
            	out.println("</td><td style=\"width:35%\" align=\"center\">");
            	
                response.setContentType("text/html"); // Response mime type
                
                out.println("<TABLE style=\"width:40%\"><tr><td align=\"center\">");

                // Display movies by title
                char[] titleChars;
                titleChars = new char[]{
                		'A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S','T','U',
                		'V','W','X','Y','Z','0','1','2','3','4','5','6','7','8','9'};
                
                for(i = 0; i < titleChars.length; ++i)
                	out.println("<a href=\"Browse?titlechar=" + titleChars[i] + "\">" + titleChars[i] + "</a> " + " ");         
                
                out.println("</td></tr></TABLE></td></tr></TABLE></BODY></HTML>");
                
                out.close();
            	return;
            }
            
            out.println("<BODY>");
            
            // Search Box
            out.println("<TABLE>");
            out.println("<FORM ACTION=\"/FabFlix/Browse\" METHOD=\"get\">");
            out.println("<tr><td>Search:</td><td><input type=\"text\" onkeyup=\"updateSuggestions(this.value)\" name=\"title\"></td></tr>");
            //onmousemove=\"hoverMovie()\" 
            out.println("<tr><td id=\"suggestions\"></td></tr><br>");
            out.println("</FORM>");
            out.println("</TABLE>");
            
            // Adapted from https://www.w3schools.com/php/php_ajax_livesearch.asp
            out.println("<script language=\"javascript\" type=\"text/javascript\">");
            out.println("function updateSuggestions(str)");
            out.println("{");
            out.println("	if(str.length==0){");
            out.println("		document.getElementById(\"suggestions\").innerHTML=\"\";");
            out.println("		document.getElementById(\"suggestions\").style.border=\"0px\";");
            out.println("		return;");
            out.println("	}");
            // IE7+, Firefox, Chrome, Opera, Safari
            out.println("	if(window.XMLHttpRequest){");
            out.println("		xmlhttp=new XMLHttpRequest();");
            // IE5-6
            out.println("	}else{");
            out.println("		xmlhttp=new ActiveXObject(\"Microsoft.XMLHTTP\");");
            out.println("	}");
            out.println("	xmlhttp.onreadystatechange=function(){");
            out.println("		if(this.readyState==4){");
            out.println("			document.getElementById(\"suggestions\").innerHTML=this.responseText;");
            out.println("			document.getElementById(\"suggestions\").style.border=\"1px solid #A5ACB2\";");
            out.println("		}");
            out.println("	}");
            out.println("	xmlhttp.open(\"GET\", \"SuggestMovies?q=\"+str);");
            out.println("	xmlhttp.send()");
            out.println("}");
            out.println("</script>");
            
            // Adapted from https://www.w3schools.com/php/php_ajax_livesearch.asp
            out.println("<script language=\"javascript\" type=\"text/javascript\">");
            out.println("function hoverInfo(str)");
            out.println("{");
            out.println("   var str2 = \"hover_info\"");
            out.println("   var fullstr = str2.concat(str)");
            // IE7+, Firefox, Chrome, Opera, Safari
            out.println("	if(window.XMLHttpRequest){");
            out.println("		xmlhttp=new XMLHttpRequest();");
            // IE5-6
            out.println("	}else{");
            out.println("		xmlhttp=new ActiveXObject(\"Microsoft.XMLHTTP\");");
            out.println("	}");
            out.println("	xmlhttp.onreadystatechange=function(){");
            out.println("		if(this.readyState==4){");
            out.println("			document.getElementById(fullstr).innerHTML=this.responseText;");
            out.println("			document.getElementById(fullstr).style.border=\"1px solid #A5ACB2\";");
            out.println("		}");
            out.println("	}");
            out.println("	xmlhttp.open(\"GET\", \"HoverMovie?q=\"+str);");
            out.println("	xmlhttp.send()");
            out.println("}");
            out.println("</script>");
            
            // Adapted from https://www.w3schools.com/php/php_ajax_livesearch.asp
            out.println("<script language=\"javascript\" type=\"text/javascript\">");
            out.println("function closeInfo(str)");
            out.println("{");
            out.println("   var str2 = \"hover_info\"");
            out.println("   var fullstr = str2.concat(str)");
            // IE7+, Firefox, Chrome, Opera, Safari
            out.println("	if(window.XMLHttpRequest){");
            out.println("		xmlhttp=new XMLHttpRequest();");
            // IE5-6
            out.println("	}else{");
            out.println("		xmlhttp=new ActiveXObject(\"Microsoft.XMLHTTP\");");
            out.println("	}");
            out.println("	xmlhttp.onreadystatechange=function(){");
            out.println("		if(this.readyState==4){");
            out.println("			document.getElementById(fullstr).innerHTML=\"\";");
            out.println("			document.getElementById(fullstr).style.border=\"0px\";");
            out.println("		}");
            out.println("	}");
            out.println("	xmlhttp.open(\"GET\", \"HoverMovie?q=\"+str);");
            out.println("	xmlhttp.send()");
            out.println("}");
            out.println("</script>");
            
            out.println("<H2>Search Results: </H1><DIV>");
            
            // Get limit, if none default to 5           
            try {resultsPerPage = Integer.parseInt(request.getParameter("rc"));}
            catch(NumberFormatException e){resultsPerPage = 5;}
            
            // Get offset, if none default to 0
            try {currPageNum = Integer.parseInt(request.getParameter("page"));}
            catch(NumberFormatException e){currPageNum = 0;}
            
            // Get title sort, if none default to -1 (none)
            try {titleSort = Integer.parseInt(urlQuery.get("ts"));}
            catch(NumberFormatException e){titleSort = -1;}
            
            // Get year sort, if none default to -1 (none)
            try {yearSort = Integer.parseInt(urlQuery.get("ys"));}
            catch(NumberFormatException e){yearSort = -1;}
            
            String query1;
            
           	if(urlQuery.containsKey("firstName") || urlQuery.containsKey("lastName"))
           	{
           		// Select all movies for which a star was in
           		query1 = 
           				"SELECT  `movies`.`id`, `movies`.`title`, `movies`.`director`, `movies`.`year`, `movies`.`banner_url`, `movies`.`trailer_url` " +
           				"FROM `movies` JOIN `stars_in_movies` JOIN `stars` ON `movies`.`id` = `stars_in_movies`.`movie_id` AND `stars_in_movies`.`star_id` = `stars`.`id` " +
           				"WHERE `stars`.`id`=`stars_in_movies`.`star_id` ";
           		
           		if(urlQuery.containsKey("firstName"))
           		{
           			if(urlQuery.containsKey("substring"))
           				query1 += "AND `stars`.`first_name` LIKE '%" + urlQuery.get("firstName") + "%' ";
           			else
           				query1 += "AND `stars`.`first_name`='" + urlQuery.get("firstName") + "' ";
           		}
           		if(urlQuery.containsKey("lastName"))
           		{
           			if(urlQuery.containsKey("substring"))
           				query1 += "AND `stars`.`last_name` LIKE '%" + urlQuery.get("lastName") + "%' ";
           			else
           				query1 += "AND `stars`.`last_name`='" + urlQuery.get("lastName") + "' ";
           		}
           	}
           	else if(urlQuery.containsKey("titleChar"))
           	{
           		query1 = 
               		"SELECT DISTINCT `movies`.`id`, `movies`.`title`, `movies`.`year`, `movies`.`director`, `movies`.`banner_url`, `movies`.`trailer_url` " +
                    "FROM `movies` JOIN `genres_in_movies` JOIN `genres` " +
                    "WHERE `movies`.`id` = `genres_in_movies`.`movie_id` AND `genres_in_movies`.`genre_id` = `genres`.`id` AND `movies`.`title` LIKE '" + urlQuery.get("titleChar") + "%' ";
           	}
           	else // Build basic movie query
           	{
           		// Select all movies
           		query1 =
           			"SELECT DISTINCT `movies`.`id`, `movies`.`title`, `movies`.`year`, `movies`.`director`, `movies`.`banner_url`, `movies`.`trailer_url` " +
                	"FROM `movies` JOIN `genres_in_movies` JOIN `genres` " +
                	"WHERE `movies`.`id` = `genres_in_movies`.`movie_id` AND `genres_in_movies`.`genre_id` = `genres`.`id` ";
           	}
           	// and start concatenating WHERE clause with conditions based on user's input
            if(urlQuery.containsKey("genre"))
            {
            	if(urlQuery.containsKey("substring"))
            		query1 += " AND `genres`.`name` LIKE '%" + urlQuery.get("genre") + "%' ";
            	else
            		query1 += " AND `genres`.`name` = '" + urlQuery.get("genre") + "' "; 	
            }
            if(urlQuery.containsKey("title"))
            {
            	if(urlQuery.containsKey("substring"))
            		query1 += " AND `movies`.`title` LIKE '%" + urlQuery.get("title") + "%' ";
            	else
            		query1 += " AND `movies`.`title` = '" + urlQuery.get("title") + "' ";
            }
            if(urlQuery.containsKey("year"))
            {
            	if(urlQuery.containsKey("substring"))
            		query1 += " AND `movies`.`year` LIKE '%" + urlQuery.get("year") + "%' ";
            	else
            		query1 += " AND `movies`.`year` = '" + urlQuery.get("year") + "' ";    
        	}
           	if(urlQuery.containsKey("director"))
           	{
            	if(urlQuery.containsKey("substring"))
            		query1 += " AND `movies`.`director` LIKE '%" + urlQuery.get("director") + "%' ";
            	else
            		query1 += " AND `movies`.`director` = '" + urlQuery.get("director") + "' "; 
           	}

            if(titleSort != -1)
            {
                if(titleSort == 1) 
                	query1 += "ORDER BY `movies`.`title` DESC";
                else
                	query1 += "ORDER BY `movies`.`title` ASC";
            }
            if(yearSort != -1)
            {
            	if(yearSort == 1)
            		query1 += "ORDER BY `movies`.`year` DESC";
            	else
            		query1 += "ORDER BY `movies`.`year` ASC";
            }
            
            // Pagination
            query1 += " LIMIT " + resultsPerPage + " OFFSET " + currPageNum*resultsPerPage;
            
            ResultSet rs1 = statement.executeQuery(query1);
           
        	out.println("<TABLE align=\"center\" style=\"width:95%\"><tr>");

        	ResultSet rs2 = null;
        	ResultSet rs3 = null;
        	
        	if(!rs1.isBeforeFirst())
        	{
        		out.println("There are no results for the search:<br>");
        		
        		Set set = urlQuery.entrySet();
        		Iterator it = set.iterator();
        		while(it.hasNext())
        		{
        			Map.Entry entry = (Map.Entry)it.next();
        			out.println(entry.getKey() + ": " + entry.getValue());
        			out.println("<br>");
        		}
        		
        		out.println("<br><a href=\"/FabFlix/Search\">Try Another Search</a>");
        	}
        	else
        	{
                out.println("<td width=\"20%\"></td><td> Sort by: </td>");
                out.println("<td> <a href=\"" + "/FabFlix/Browse?" + updateQuery("ts=0&ys=-1") + "\"> Title A->Z </a></td>");
                out.println("<td> <a href=\"" + "/FabFlix/Browse?" + updateQuery("ts=1&ys=-1") + "\"> Title Z->A </a></td>");
                out.println("<td> <a href=\"" + "/FabFlix/Browse?" + updateQuery("ts=-1&ys=0") + "\"> Year 0->9  </a></td>"); 
                out.println("<td> <a href=\"" + "/FabFlix/Browse?" + updateQuery("ts=-1&ys=1") + "\"> Year 9->0  </a></td></tr>");
            
	            while(rs1.next())
	            {
	            	// Selects all stars in a movie
	            	String query2 = 	
	            			"SELECT `s`.`first_name`, `s`.`last_name`, `s`.`id` " + 
	            			"FROM `movies` as m, `stars` as s, `stars_in_movies` as sim " +
	            			"WHERE `m`.`ID` = `sim`.`movie_id` AND `s`.`id` = `sim`.`star_id` AND `m`.`ID` = " + rs1.getString("id");
	            	
	            	// Selects genre of a movie given movie ID
	            	String query3 = 
	            			"SELECT `g`.`name` " + 
	            			"FROM `movies` as m, `genres` as g, `genres_in_movies` as gim " + 
	            			"WHERE `m`.`ID` = `gim`.`movie_id` AND `g`.`id` = `gim`.`genre_id` AND `m`.`ID` = " + rs1.getString("id");
	            	
	            	Statement statement2 = dbcon.createStatement();
	            	
	            	rs2 = statement2.executeQuery(query2);
	            	Statement statement3 = dbcon.createStatement();
	            	
	            	rs3 = statement3.executeQuery(query3);
	            	
	        		out.println("<TABLE align =\"center\" style=\"width:100%\"><tr>");
	            	out.println("<td align=\"center\" rowspan=\"7\" width=\"15%\">");
	            	String url = "/FabFlix/Movie?id=" + rs1.getString("id");
	            	out.println("<a href=\"" + url + "\"><img src=\"" + rs1.getString("banner_url") + "\" style=\"width:80px;height:120px;\"></a><br><br>");
	            	out.println("<a href=\"/FabFlix/AddToCart?movie_id=" + rs1.getString("id") +"\" target=\"_blank\">Add to Cart</a>");out.println("<td align=\"left\" width=\"15%\">");
	            	out.println("Title: </td><td width=\"30%\">");
	            	out.println("<a onmouseenter=\"hoverInfo(" + rs1.getString("id") + ")\" href=\"" + url + "\"</a>" + rs1.getString("title") + "</td>");
	            	out.println("<td onmouseleave=\"closeInfo(" + rs1.getString("id") + ")\" id=\"hover_info" + rs1.getString("id") + "\" style=\"text-align:left\" rowspan=\"7\" width=\"40%\"></td></tr>");
	            	out.println("</td><td>");
	            	out.println("Year: </td><td>");
	            	out.println(rs1.getString("year") + "</td></tr>");
	            	out.println("</td><td>");
	            	out.println("Director: </td><td>");
	            	out.println(rs1.getString("director") + "</td></tr>");
	            	out.println("</td><td>");
	            	out.println("MovieID: </td><td>");
	            	out.println(rs1.getString("id") + "</td></tr>");
	            	out.println("</td><td>");
	            	out.println("Starring: </td><td>");
	            	while(rs2.next()) // Iterate through stars and hyperlink to personal pages
	            		out.println("<a href=\"/FabFlix/Star?starid=" + rs2.getString("ID") + "\">" + rs2.getString("first_name") + " " + rs2.getString("last_name") + "</a>" + "   ");
	            	out.println("</td></tr></td><td>");            
	            	out.println("Genre: </td><td>");
	            	while(rs3.next()) // Iterate through genres and hyperlink to search by genre
	            		out.println("<a href=\"/FabFlix/Browse?genre=" + rs3.getString("name") + "\">" + rs3.getString("name") + "</a>" + "   ");
	            
	            	out.println("</td></tr></td><td>");
	            	out.println("</td></tr></TABLE><br><br>");
	            	
	                rs2.close();
	                rs3.close();
	            }
	            
	            out.println("<TABLE align=\"center\" style=\"width:80%\">");
	            out.println("<tr><td width=\"20%\"></td><td>");
	            out.println("<a href=\"/FabFlix/Browse?" + updateQuery("page=" + getPrevPage(currPageNum)) + "\">PREV</a>");
	            out.println("  ");
	            out.println("<a href=\"/FabFlix/Browse?" + updateQuery("page=" + getNextPage(currPageNum)) + "\">NEXT</a>"); 
	            out.println("</td><td>RESULTS PER PAGE: ");
	            out.println("  ");
	            out.println("<a href=\"/FabFlix/Browse?" + updateQuery("rc=1&page=0") + "\">1</a>");
	            out.println("  ");
	            out.println("<a href=\"/FabFlix/Browse?" + updateQuery("rc=5&page=0") + "\">5</a>");
	            out.println("  ");
	            out.println("<a href=\"/FabFlix/Browse?" + updateQuery("rc=10&page=0") + "\">10</a>"); 
	            out.println("  ");
	            out.println("<a href=\"/FabFlix/Browse?" + updateQuery("rc=25&page=0") + "\">25</a>"); 
	            out.println("  ");
	            out.println("<a href=\"/FabFlix/Browse?" + updateQuery("rc=50&page=0") + "\">50</a>"); 
	            out.println("</td><td width=\"20%\"></td>");
	            out.println("</TABLE>");
	            out.println("</TABLE>");
        	}

            rs1.close();
            statement.close();
            dbcon.close();
        } catch (SQLException ex) {
            while (ex != null) {
                System.out.println("SQL Exception:  " + ex.getMessage());
                ex = ex.getNextException();
            } // end while
        } // end catch SQLException

        catch (java.lang.Exception ex) {
            out.println("<HTML>" + "<HEAD><TITLE>" + "FabFlix Error: Unable to Retrieve Movies, Please Contact Support (123)456-7890" + "</TITLE></HEAD>\n<BODY>"
                    + "<P>SQL error in doGet: " + ex.getMessage() + "</P></BODY></HTML>");
            return;
        }
        
        out.println("</DIV></BODY></HTML>");
        
        out.close();
        
	}
}
