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

public class MovieSaxParserExample extends DefaultHandler{

	List myMovies;
	
	private String tempVal;
	// private String tempDirector;
	private ArrayList<String> tempCats;
	//to maintain context
	private Movie tempMovie;

	private ArrayList<String> allCats;

	private Map<String, int> idFilmMovie; 
	
	
	public MovieSaxParserExample(){
		myMovies = new ArrayList();
		tempCats = new ArrayList<String>();
		allCats = new ArrayList<String>();
		idFilmMovie = new Map<String, int>();
	}
	
	public void runExample() {
		parseDocument();
		addDataToDatabase();
		printData();
	}

	private void parseDocument() {
		
		//get a factory
		SAXParserFactory spf = SAXParserFactory.newInstance();
		try {
		
			//get a new instance of parser
			SAXParser sp = spf.newSAXParser();
			
			//parse the file and also register this class for call backs
			sp.parse("mains243.xml", this);
			
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
		
		System.out.println("No of Movies '" + myMovies.size() + "'.");
		
		Iterator it = myMovies.iterator();
		while(it.hasNext()) {
			System.out.println(it.next().toString());
		}
		System.out.println("All Categories:");
		for(int i = 0; i < allCats.size(); i++){
			System.out.println("\t" + allCats.get(i));
		}
	}

	private void addDataToDatabase(){
		String loginUser = "mytestuser";
        String loginPasswd = "mypassword";
        String loginUrl = "jdbc:mysql://localhost:3306/moviedb";
        try{
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            Connection dbcon = DriverManager.getConnection(loginUrl, loginUser, loginPasswd);

			Iterator it = myMovies.iterator();
			PreparedStatement stmtMovieID = dbcon.prepareStatement("SELECT id FROM movies WHERE title = ? AND year = ? AND director = ?;");
			PreparedStatement stmtAddMovie = dbcon.prepareStatement("INSERT INTO movies (title, year, director) VALUES (?, ?, ?);");
			PreparedStatement stmtGenreID = dbcon.prepareStatement("SELECT id FROM genres WHERE name = ?;");
			PreparedStatement stmtAddGenre = dbcon.prepareStatement("INSERT INTO genres (name) VALUES (?);");
			PreparedStatement stmtFindGIM = dbcon.prepareStatement("SELECT * FROM genres_in_movies WHERE genre_id = ? AND movie_id = ?;");
			PreparedStatement stmtAddGIM = dbcon.prepareStatement("INSERT INTO genres_in_movies (genre_id, movie_id) VALUES (?, ?);");		
			while(it.hasNext()) {
				// System.out.println("Count = ";
				int movie_id;
				Movie m = (Movie) it.next();
				String title = m.getTitle();
				int year = m.getYear();
				String director = m.getDirector();
				String filmID = m.getFilmID();
				if(title == null){
					title = "";
				}
				if(director == null){
					director = "";
				}
				ArrayList<String> categories = m.getCategories();
				stmtMovieID.setString(1, title);
				stmtMovieID.setInt(2, year);
				stmtMovieID.setString(3, director);
				
				ResultSet movie_id_result = stmtMovieID.executeQuery();
				if(movie_id_result.next()){
					movie_id = movie_id_result.getInt("id");
				}else{
					stmtAddMovie.setString(1, title);
					stmtAddMovie.setInt(2, year);
					stmtAddMovie.setString(3, director);
	            	int addMovie_result = stmtAddMovie.executeUpdate();
	            	if(addMovie_result == 0){
						System.out.println("Error inserting movie (\'" + title + "\', " + String.valueOf(year) + ", \'" + director + "\')");
					}
					movie_id_result = stmtMovieID.executeQuery();
					if(movie_id_result.next()){
						movie_id = movie_id_result.getInt("id");
					}else{
						System.out.println("Error: cannot find movie in database.");
						break;
					}
				}

				idFilmMovie.put(filmID, movie_id);
				
				for (int i = 0; i < categories.size(); i++){
					String category = getCategories(categories.get(i));
					int genre_id;
					stmtGenreID.setString(1, category);
					ResultSet genre_id_result = stmtGenreID.executeQuery();
					if(genre_id_result.next()){
						genre_id = genre_id_result.getInt("id");
					}else{
						stmtAddGenre.setString(1, category);
		            	int addGenre_result = stmtAddGenre.executeUpdate();
		            	if(addGenre_result == 0){
							System.out.println("Error inserting genre (name = \'" + category + "\')");
						}
						genre_id_result = stmtGenreID.executeQuery();
						if(genre_id_result.next()){
							genre_id = genre_id_result.getInt("id");
						}else{
							System.out.println("Error: cannot find genre in database.");
							break;
						}
					}
					stmtFindGIM.setInt(1, genre_id);
					stmtFindGIM.setInt(2, movie_id);
					ResultSet gim_result = stmtFindGIM.executeQuery();
					if(!gim_result.next()){
						stmtAddGIM.setInt(1, genre_id);
						stmtAddGIM.setInt(2, movie_id);
						int addGIM_result = stmtAddGIM.executeUpdate();
		            	if(addGIM_result == 0){
							System.out.println("Error inserting genres_in_movies (genre_id = " + String.valueOf(genre_id) + ", movie_id = " + String.valueOf(movie_id) + ")");
						}
					}
					
				}
				
			}
            
        }catch(Exception e){
            System.out.println(e.getMessage());
        }
	}
	
	public String getCategories(String cat){
		switch(cat.toLowerCase()){
			case "actn":
				return "Action";
			case "advt":
				return "Adventure";
			case "Animation":
				return "Animation";
			case "clas":
				return "Classic";
			case "comd":
				return "Comedy";
			case "coad":
				return "Coming-of-Age-Drama";
			case "crim":
				return "Crime";
			case "docu":
				return "Documentary";
			case "dram":
				return "Drama";
			case "biog":
				return "Biography";
			case "hist":
				return "History";
			case "Faml":
				return "Family";
			case "fant":
				return "Fantasy";
			case "forn":
				return "Foreign";
			case "gang":
				return "Gangster";
			case "Horr":
				return "Horror";
			case "indi":
				return "Indie";
			case "jabo":
				return "James Bond";
			case "music":
				return "Music";
			case "musc":
				return "Musical";
			case "muscpa":
				return "Musical/Performing Arts";
			case "myst":
				return "Mystery";
			case "rom":
				return "Roman";
			case "romt":
				return "Romance";
			case "scfi":
				return "Sci-Fi";
			case "sprt":
				return "Sport";
			case "spy":
				return "Spy";
			case "susp":
				return "Suspense";
			case "thrl":
				return "Thriller";
			case "war":
				return "War";
			case "biop":
				return "Biographical Picture";
			case "noir":
				return "Black";
			case "porn":
				return "Pornography";
			case "cart":
				return "Cartoon";
			case "act":
				return "Act";
			case "romt comd":
				return "Romantic Comedy";
			case "epic":
				return "Epic";
			default:
				return cat.toLowerCase();
		}
	}

	//Event Handlers
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		//reset
		tempVal = "";
		if(qName.equalsIgnoreCase("film")) {
			//create a new instance of employee
			tempMovie = new Movie();
			tempCats = new ArrayList<String>();
		}
	}
	

	public void characters(char[] ch, int start, int length) throws SAXException {
		tempVal = new String(ch,start,length);
	}
	
	public void endElement(String uri, String localName, String qName) throws SAXException {

		if(qName.equalsIgnoreCase("film")) {
			tempMovie.setCategories(tempCats);
			myMovies.add(tempMovie);
			tempMovie = new Movie();
			tempCats = new ArrayList<String>();
		}else if (qName.equalsIgnoreCase("t")) {
			tempMovie.setTitle(tempVal.trim());
		}else if (qName.equalsIgnoreCase("year")) {
			try{
				tempMovie.setYear(Integer.parseInt(tempVal.trim()));
			}catch(NumberFormatException nfe){
				System.out.println("NumberFormatException: For input string: \"" + tempVal.trim() + "\"");
			}
			
		}else if (qName.equalsIgnoreCase("dirn")) {
			tempMovie.setDirector(tempVal.trim());
		}else if (qName.equalsIgnoreCase("cat")) {
			tempCats.add(tempVal);
			allCats.add(tempVal);
		}else if (qName.equalsIgnoreCase("fid")) {
			tempMovie.setFilmID(tempVal.trim());
		}
	}
	
	public static void main(String[] args){
		MovieSaxParserExample mpe = new MovieSaxParserExample();
		mpe.runExample();
	}

	
}




