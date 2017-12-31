<?php
	/*jdbc:mysql:///moviedb?autoReconnect=true&useSSL=false*/
	$link = new mysqli('localhost', 'mytestuser', 'mypassword');

	if($link->connect_error){
		die("Connection failed: " . $link->connect_error);
	}
	echo "Connected successfully";

	/* Run selection query to determine if we should add */
	$movieTitle = $_GET["movieTitle"];
	$movieYear = $_GET["movieYear"];
	$movieDirector = $_GET["movieDirector"];
	$movieTrailer = $_GET["movieTrailer"];
	$movieBanner = $_GET["movieBanner"];
	$movieGenre = $_GET["movieGenre"];
	$movieID = $_GET["mid"];
	$movieStar = $_GET["movieStar"];
	$addGenre = $_GET["addGenre"];
	$addStarFirst = $_GET["addStarFirst"];
	$addStarLast = $_GET["addStarLast"];

	/* Build query */	
	$query = sprintf("SELECT * FROM MOVIES WHERE TITLE='%s' AND YEAR='%s' AND DIRECTOR='%s'",
		mysql_real_escape_string($movieTitle),
		mysql_real_escape_string($movieYear),
		mysql_real_escape_string($movieDirector);

	/* Run query */
	$result = mysql_query($query);

	if(!$result)
	{
		/* Run stored procedure */
		//$mysql->query("CALL add_movie()");
		$link->query("CALL add_movie()");		
	}
	else
	{
		/* Add failure */
		$message = 'Adding movie failed, already exists!';
		die($message);			
	}
?>