DROP PROCEDURE IF EXISTS `add_movie`;

DELIMITER $$

CREATE PROCEDURE `add_movie` (IN `mtitle` VARCHAR(100), IN `myear` INT(11), IN `mdirector` VARCHAR(100),
	IN `mbannerurl` VARCHAR(200), IN `mtrailerurl` VARCHAR(200), IN `mgenre` VARCHAR(32),
    IN `sfirstname` VARCHAR(100), IN `slastname` VARCHAR(100), IN `sdob` DATE,
    IN `sphotourl` VARCHAR(200), OUT `output` VARCHAR(1000))
    
addmovie_proc: 
BEGIN

	# Declare variables
	DECLARE verfMovie int;
	DECLARE verfStar int;
    DECLARE verfGenre int;
    
    DECLARE starid int;
    DECLARE genreid int;
    
    SET output = 'Admin Dashboard ';
    
    # Verify movie format, display errors if not all data given
	IF(`mtitle` IS NULL) THEN
		SET output = CONCAT(output, 'Error: No title given.  Please add movie title and resubmit query');
        SELECT output;
        LEAVE addmovie_proc;
	END IF;
    
    IF(`myear` IS NULL) THEN
		SET output = CONCAT(output, 'Error: No year given.  Please add movie year and resubmit query');
        SELECT output;
        LEAVE addmovie_proc;
	END IF;
    
    IF(`mdirector` IS NULL) THEN
		SET output = CONCAT(output, 'Error: No director given.  Please add movie director and resubmit query');
        SELECT output;
        LEAVE addmovie_proc;
	END IF;

	# Insert star (add to existing movie if all data given)
    IF(!((`sfirstname` IS NULL) AND (`slastname` IS NULL) AND (`sdob` IS NULL) AND (`sphotourl` IS NULL))) THEN
			SELECT stars.id INTO verfStar FROM stars WHERE stars.first_name = `sfirstname` AND 
				stars.last_name = `slastname` AND stars.dob = `sdob` AND stars.photo_url = `sphotourl`;
            IF(verfStar IS NULL) THEN
				INSERT INTO stars(first_name, last_name, dob, photo_url) VALUES(`sfirstname`, `slastname`, `sdob`, `sphotourl`);
                SET output = CONCAT(output, ' Star Insert Success ');
			END IF;
	ELSE
		SET verfStar = NULL;
    END IF;

	# Insert genre (add to existing movie if all data given)
    IF(!(`gname` IS NULL)) THEN
		SELECT genres.id INTO verfGenre FROM genres WHERE genres.name = `gname`;
        IF(verfGenre is NULL) THEN
			INSERT INTO genres(name) VALUES(`gname`);
			SET output = CONCAT(output, 'Genre Insert Success ');
		END IF;
	ELSE
		SET verfGenre = NULL;
	END IF;
    
	SELECT movies.id INTO verfMovie FROM movies WHERE movies.title = `mtitle` AND 
		movies.year = `myear` AND movies.director = `mdirector`;
	
    # If the movie already exists then state that movie already exists
    IF(verfMovie IS NOT NULL) THEN
		SET output = CONCAT(output, 'Movie Already Exists in movie Table ');
        
        # Perform link for star insertion
		SELECT stars.id INTO verfStar FROM stars WHERE stars.first_name = `sfirstname` AND 
			stars.last_name = `slastname` AND stars.dob = `star_dob` AND 
            stars.photo_url = `sphotourl`;
            
		# If there was a result for the star, then update stars_in_movies
		IF(!(verfStar IS NULL)) THEN
			SELECT count(*) INTO starid FROM stars_in_movies, stars WHERE stars.first_name = `sfirstname` AND stars.last_name = `star_lastname` AND stars.id = stars_in_movies.star_id AND stars_in_movies.movie_id = verfMovie;
			IF(starsid = 0) THEN
				INSERT INTO stars_in_movies VALUES(verfStar, verfMovie);
                SET output = CONCAT(output, 'Updated stars_in_movies table ');
			END IF;
		END IF;
        
        # Perform link for genre insertion
		SELECT genres.id INTO verfGenre FROM genres WHERE genres.name = `gname`;
        
        # If there was a result for the genre, then update genres_in_movies
        IF(!(verfGenre IS NULL)) THEN
			SELECT count(*) INTO genreid FROM genres, genres_in_movies WHERE genres.name = `gname` AND 
				genres_in_movies.genre_id = verfGenre AND genres_in_movies.movie_id = verfMovie;
			IF(genreid = 0) THEN
				INSERT INTO genres_in_movies VALUES(verfGenre, verfMovie);
                SET output = CONCAT(output, 'Updated genres_in_movies table ');
			END IF;
		END IF;
	# If the movie does not exist, insert it into the movie table and update corresponding tables
	ELSE
		# Insert into movies
		INSERT INTO movies(title, year, director, banner_url, trailer_url) VALUES(`mtitle`, `myear`,
			`mdirector`, `mbannerurl`, `mtrailerurl`);
		SET output = CONCAT(output, 'Movie Insert Success ');
        
        # Get the movie ID of the movie we just inserted (due to autoincrement of key)
		SELECT movies.id INTO verfMovie FROM movies WHERE movies.title = `mtitle` AND 
			movies.year = `myear` AND movies.director = `mdirector`;
        
        # Get the star ID of the star in the URL
        SELECT stars.id INTO verfStar FROM stars WHERE stars.first_name = `sfirstname` AND 
			stars.last_name = `slastname` AND stars.dob = `sdob` AND stars.photo_url = `sphotourl`;
            
		# If the star exists, then update the stars_in_movies table
        IF (!(verfStar IS NULL)) THEN
			SELECT count(*) INTO starid FROM stars_in_movies, stars WHERE 
				stars.first_name = `sfirstname` AND stars.last_name = `slastname` AND 
                stars.id = stars_in_movies.star_id AND stars_in_movies.movie_id = verfMovie;
			IF(starid = 0) THEN
				INSERT INTO stars_in_movies VALUES(verfStar, verfMovie);
                SET output = CONCAT(output, 'Updated stars_in_movies table ');
			END IF;
		END IF;
		
        # Get the genre ID of the genre in the URL
        SELECT genres.id INTO verfGenre FROM genres WHERE genres.name = `gname`;
        
        # If the genre exists, then update the genres_in_movies table
		IF(!(verfGenre IS NULL)) THEN
			SELECT count(*) INTO genreid FROM genres, genres_in_movies WHERE 
				genres.name = `gname` AND genres_in_movies.genre_id = verfGenre AND
                genres_in_movies.movie_id = verfMovie;
			IF(genreid = 0) THEN
				INSERT INTO genres_in_movies VALUES(verfGenre, verfMovie);
                SET output = CONCAT(output, 'Updated genres_in_movies table ');
			END IF;
		END IF;
	END IF;
    
    # Display log for user
    SELECT output;

END $$

DELIMITER ;