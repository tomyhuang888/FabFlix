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

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class StarParserExample {

	List myStars;
	Document dom;
	int count;
	private Map<String, int> idNameStarID; 

	public StarParserExample(){
		//create a list to hold the employee objects
		myStars = new ArrayList();
		idNameStarID = new Map<String,int>();
		count = 0;
	}

	public void runExample() {
		
		//parse the xml file and get the dom object
		parseXmlFile();
		
		//get each employee element and create a Employee object
		parseDocument();
		
		//Iterate through the list and print the data
		// printData();
		addDataToDatabase();
	}

	private void parseXmlFile(){
		//get the factory
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		
		try {
			
			//Using factory get an instance of document builder
			DocumentBuilder db = dbf.newDocumentBuilder();
			
			//parse using builder to get DOM representation of the XML file
			dom = db.parse("actors63.xml");
			

		}catch(ParserConfigurationException pce) {
			pce.printStackTrace();
		}catch(SAXException se) {
			se.printStackTrace();
		}catch(IOException ioe) {
			ioe.printStackTrace();
		}
	}

	private void parseDocument(){
		//get the root elememt
		Element docEle = dom.getDocumentElement();
		
		//get a nodelist of <actor> elements
		NodeList nl = docEle.getElementsByTagName("actor");
		if(nl != null && nl.getLength() > 0) {
			for(int i = 0 ; i < nl.getLength();i++) {
				
				//get the employee element
				Element el = (Element)nl.item(i);
				
				//get the Employee object
				Star e = getStar(el);
				
				//add it to list
				myStars.add(e);
			}
		}
	}


	/**
	 * I take an employee element and read the values in, create
	 * an Employee object and return it
	 * @param empEl
	 * @return
	 */
	private Star getStar(Element empEl) {
		count++;
		System.out.println("Count: " + count);
		//for each <employee> element get text or int values of 
		//name ,id, age and name
		String stagename = getTextValue(empEl,"stagename");
		String first_name = "";
		String last_name = "";
		if(stagename == null){
			first_name = "";
			last_name = "";
		}else{
			String[] names = stagename.split(" ");
			if(names.length == 1){
				first_name = "";
				last_name = names[0];
			}else if(names.length == 2){
				first_name = names[0];
				last_name = names[1];
			}
		}
		// System.out.println("Stagename: " + stagename);
		// String last_name = getTextValue(empEl,"year");
		// System.out.println("Last Name: " + last_name);
		String dob = String.valueOf(getIntValue(empEl,"dob"));
		// System.out.println("DOB: " + dob);
		if(first_name == null){
			first_name = "";
		}
		if(last_name == null){
			last_name = "";
		}
		// if (dob == null){
		// 	dob = "";
		// }
		// String type = empEl.getAttribute("type");
		
		//Create a new Employee with the value read from the xml nodes
		Star s = new Star(first_name, last_name, dob);
		
		return s;
	}


	/**
	 * I take a xml element and the tag name, look for the tag and get
	 * the text content 
	 * i.e for <employee><name>John</name></employee> xml snippet if
	 * the Element points to employee node and tagName is name I will return John  
	 * @param ele
	 * @param tagName
	 * @return
	 */
	private String getTextValue(Element ele, String tagName) {
		String textVal = null;
		NodeList nl = ele.getElementsByTagName(tagName);
		if(nl != null && nl.getLength() > 0) {
			Element el = (Element)nl.item(0);
			if(el.getFirstChild() != null){
				textVal = el.getFirstChild().getNodeValue();
			}
		}
		return textVal;
	}

	
	/**
	 * Calls getTextValue and returns a int value
	 * @param ele
	 * @param tagName
	 * @return
	 */
	private int getIntValue(Element ele, String tagName) {
		//in production application you would catch the exception
		try{
			return Integer.parseInt(getTextValue(ele,tagName));
		}catch (Exception e){
			System.out.println(e.toString());
			System.out.println("Error int value for tag, <" + tagName + ">, and value " + ele.toString());
			return -1;
		}
		// return -1;
	}
	
	/**
	 * Iterate through the list and print the 
	 * content to console
	 */
	private void printData(){
		
		System.out.println("No of Stars '" + myStars.size() + "'.");
		
		Iterator it = myStars.iterator();
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
			Iterator it = myStars.iterator();
			PreparedStatement stmtCheck = dbcon.prepareStatement("SELECT id FROM stars WHERE first_name = ? AND last_name = ? AND dob = ?");
			PreparedStatement stmtInsert = dbcon.prepareStatement("INSERT INTO stars (first_name, last_name, dob) VALUES (?, ?, ?);");
			// System.out.println("before");
			while(it.hasNext()) {
				// System.out.println("Count = ";
				int star_id = -1;
				Star s = (Star) it.next();
				String first_name = s.getFirstName();
				String last_name = s.getLastName();
				String dob = s.getDOB();
				if(dob != null){
					if(dob.equals("-1")){
						dob = null;
					}else{
						dob = dob + "/01/01";
					}
				}
				// System.out.println("first_name: " + first_name);
				// System.out.println("last_name: " + last_name);
				// System.out.println("dob: " + dob);
				stmtCheck.setString(1, first_name);
				stmtCheck.setString(2, last_name);
				stmtCheck.setString(3, dob);
				
				ResultSet session_result = stmtCheck.executeQuery();
				if(session_result.next()){
					star_id = session_result.getInt("id");
				}else{
					stmtInsert.setString(1, first_name);
					stmtInsert.setString(2, last_name);
					stmtInsert.setString(3, dob);
	            	int star_result = stmtInsert.executeUpdate();
	            	if(star_result == 0){
						System.out.println("Error inserting star_in_movies");
					}
					stmtCheck.setString(1, first_name);
					stmtCheck.setString(2, last_name);
					stmtCheck.setString(3, dob);
					
					session_result = stmtCheck.executeQuery();
					if(session_result.next()){
						star_id = session_result.getInt("id");
					}
				}
				idNameStarID.put(first_name + " " + last_name, star_id);
			}
        }catch(Exception e){
            System.out.println(e.getMessage());
        }
	}

	public static void main(String[] args){
		//create an instance
		StarParserExample spe = new StarParserExample();
		
		//call run example
		spe.runExample();
	}

}