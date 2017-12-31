import java.io.*;
import java.net.*;
import java.sql.*;
import java.text.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import org.xml.sax.helpers.DefaultHandler;

public class CastSaxParserExample extends DefaultHandler{

	List myCasts;
	
	private String tempVal;
	private String tempDirector;
	
	//to maintain context
	private Cast tempCast;
	
	
	public CastSaxParserExample(){
		myCasts = new ArrayList();
	}
	
	public void runExample() {
		parseDocument();
		// printData();
		addDataToDatabase();
	}

	private void parseDocument() {
		
		//get a factory
		SAXParserFactory spf = SAXParserFactory.newInstance();
		try {
		
			//get a new instance of parser
			SAXParser sp = spf.newSAXParser();
			
			//parse the file and also register this class for call backs
			sp.parse("casts124.xml", this);
			
		}catch(SAXException se) {
			se.printStackTrace();
		}catch(ParserConfigurationException pce) {
			pce.printStackTrace();
		}catch (IOException ie) {
			ie.printStackTrace();
		}
	}

	/**
	 * Iterate through the list and print
	 * the contents
	 */
	private void printData(){
		
		System.out.println("No of Casts '" + myCasts.size() + "'.");
		
		Iterator it = myCasts.iterator();
		while(it.hasNext()) {
			System.out.println(it.next().toString());
		}
	}

	private void addDataToDatabase(){
		String loginUser = "mytestuser";
        String loginPasswd = "mypassword";
        String loginUrl = "jdbc:mysql://localhost:3306/moviedb";
        // System.out.println("addtodatabase");
        try{
            Class.forName("com.mysql.jdbc.Driver").newInstance();
			// System.out.println("check");
            // Connection dbcon = DriverManager.getConnection(loginUrl, loginUser, loginPasswd);
            Connection dbcon = DriverManager.getConnection(loginUrl, loginUser, loginPasswd);
            // Declare our statement
            // Statement statement = dbcon.createStatement();
            // System.out.println("No of Casts '" + myCasts.size() + "'.");
			Iterator it = myCasts.iterator();
			PreparedStatement stmtStarID = dbcon.prepareStatement("SELECT id FROM stars WHERE first_name = ? AND last_name = ?;");
			PreparedStatement stmtMovieID = dbcon.prepareStatement("SELECT id FROM movies WHERE title = ? AND director = ?;");
			PreparedStatement stmtInsertSIM = dbcon.prepareStatement("INSERT INTO stars_in_movies (star_id, movie_id) VALUES (?, ?);");	
			PreparedStatement stmtAddStar = dbcon.prepareStatement("INSERT INTO stars (first_name, last_name) VALUES (?, ?);");
			PreparedStatement stmtAddMovie = dbcon.prepareStatement("INSERT INTO movies (title, year, director) VALUES (?, ?, ?);");		
			// PreparedStatement stmtCheck = dbcon.prepareStatement("SELECT * FROM stars WHERE first_name = ? AND last_name = ? AND dob = ?");
			// PreparedStatement stmtInsert = dbcon.prepareStatement("INSERT INTO stars (first_name, last_name, dob) VALUES (?, ?, ?);");
			// System.out.println("before");
			String prev_title = "";
			String prev_director = "";
			int star_id = -1;
			int movie_id = -1;
			while(it.hasNext()) {
				// System.out.println("Count = ");
				Cast c = (Cast) it.next();
				String title = c.getTitle();
				String actor = c.getActor();
				String director = c.getDirector();
				// System.out.println("first_name: " + first_name);
				// System.out.println("last_name: " + last_name);
				// System.out.println("dob: " + dob);
				String first_name = "";
				String last_name = "";
				if(actor == null){
					first_name = "";
					last_name = "";
				}else{
					String[] names = actor.split(" ");
					if(names.length == 1){
						first_name = "";
						last_name = names[0];
					}else if(names.length == 2){
						first_name = names[0];
						last_name = names[1];
					}
				}
				stmtStarID.setString(1, first_name);
				stmtStarID.setString(2, last_name);
				ResultSet starid_result = stmtStarID.executeQuery();
				if(starid_result.next()){
					star_id = starid_result.getInt("id");
				}else{
					stmtAddStar.setString(1, first_name);
					stmtAddStar.setString(2, last_name);
					int addStar_result = stmtAddStar.executeUpdate();
					if(addStar_result == 1){
						starid_result = stmtStarID.executeQuery();
						if(starid_result.next()){
							star_id = starid_result.getInt("id");
						}
					}
				}
				//check if same movie
				if(!(prev_title.equals(title) && prev_director.equals(director))){
					//if not same movie, check for 
					stmtMovieID.setString(1, title);
					stmtMovieID.setString(2, director);
					ResultSet movieid_result = stmtMovieID.executeQuery();
					if(movieid_result.next()){
						movie_id = movieid_result.getInt("id");
					}else{
						stmtAddMovie.setString(1, title);
						stmtAddMovie.setInt(2, -1);
						stmtAddMovie.setString(3, director);
						int addMovie_result = stmtAddMovie.executeUpdate();
						if(addMovie_result == 1){
							movieid_result = stmtMovieID.executeQuery();
							if(movieid_result.next()){
								movie_id = movieid_result.getInt("id");
							}
						}
					}
					prev_title = title;
					prev_director = prev_director;
				}

				// if(star_id != -1 && movie_id != -1){
				stmtInsertSIM.setInt(1, star_id);
				stmtInsertSIM.setInt(2, movie_id);
				int sim_result = stmtInsertSIM.executeUpdate();
				if(sim_result == 0){
					System.out.println("Error inserting star_in_movies");
				}
				// }else{
				// 	if(star_id == -1){
				// 		System.out.println("Star (\"" + actor + "\") does not exists.");
				// 	}
				// 	if(movie_id == -1){
				// 		System.out.println("Movie (title: \"" + title + "\", director: \"" + director + "\") does not exists.");
				// 	}
				// }

				
				// ResultSet session_result = stmtCheck.executeQuery();
				// if(session_result.next() == false){
				// 	stmtInsert.setString(1, first_name);
				// 	stmtInsert.setString(2, last_name);
				// 	stmtInsert.setString(3, dob);
	   //          	int star_result = stmtInsert.executeUpdate();
	   //          	// System.out.println("result: " + String.valueOf(star_result));
				// }
				// System.out.println(it.next().toString());
			}
            
        }catch(Exception e){
        	// System.out.println("error");
            System.out.println(e.getMessage());
        }
	}
	

	//Event Handlers
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		//reset
		tempVal = "";
		if(qName.equalsIgnoreCase("dirfilms")) {
			//create a new instance of employee
			tempCast = new Cast();
			// tempCast.setType(attributes.getValue("type"));
		}
	}
	

	public void characters(char[] ch, int start, int length) throws SAXException {
		tempVal = new String(ch,start,length);
	}
	
	public void endElement(String uri, String localName, String qName) throws SAXException {

		if(qName.equalsIgnoreCase("m")) {
			// System.out.println(tempVal);
			//add it to the list
			tempCast.setDirector(tempDirector);
			myCasts.add(tempCast);
			tempCast = new Cast();
		}else if (qName.equalsIgnoreCase("t")) {
			// System.out.println(tempVal);
			tempCast.setTitle(tempVal);
		}else if (qName.equalsIgnoreCase("a")) {
			// System.out.println(tempVal);
			tempCast.setActor(tempVal);
		}else if (qName.equalsIgnoreCase("is")) {
			// System.out.println(tempVal);
			tempDirector = tempVal;
		}

		
	}
	
	public static void main(String[] args){
		CastSaxParserExample cpe = new CastSaxParserExample();
		cpe.runExample();
	}
	
}




