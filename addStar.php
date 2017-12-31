<?php
	$link = mysql_connect('jdbc:mysql:///moviedb?autoReconnect=true&useSSL=false', 'mytestuser', 'mypassword');
	if (!$link) 
	{
    		die('Could not connect: ' . mysql_error());
	}

	/* Run selection query to determine if we should add */
	$firstName = $_GET["first_name"];
	$lastName = $_GET["last_name"];
	$dob = $_GET["date_of_birth"];
	$photoURL = $_GET["photo_url"];

	echo $firstName;
	echo $lastName;
	echo $dob;
	echo $photoURL;

	/* Build query */	
	$query = sprintf("SELECT * FROM STARS WHERE FIRST_NAME='%s' AND LAST_NAME='%s'",
		mysql_real_escape_string($firstName),
		mysql_real_escape_string($lastName);

	/* Run query */
	$result = mysql_query($query);

	if(!$result)
	{
		/* Add successful */
		$query = sprintf("INSERT INTO STARS (first_name, last_name, dob, photo_url)
			VALUES($firstName, $lastName, $dob, $photoURL)");
		
		mysql_query($query);

		/* Confirmation message */
		$message = 'Adding star successful!';
		die($message);	
	}
	else
	{
		/* Add failure */
		$message = 'Adding star failed, already exists!';
		die($message);			
	}

	
?>