<!DOCTYPE HTML>
<HTML><HEAD>
<?php	$s_title = $_GET["title"]; ?>
<?php	$s_year = $_GET["year"]; ?>
<?php	$s_director = $_GET["director"]; ?>
<?php	$s_first = $_GET["firstName"]; ?>
<?php	$s_last = $_GET["lastName"]; ?>
<?php	$s_fuzzy = $_GET["fuzzy"]; ?>
<?php	$s_substr = $_GET["substring"];	?>
</HEAD><BODY>
<?php echo $_GET["title"]; ?>
<?php	echo $s_title; ?>
<?php	echo $s_years; ?>
<?php	echo $s_director; ?>
<?php	echo $s_first; ?>
<?php	echo $s_last; ?>
<?php	echo $s_fuzzy; ?>
<?php	echo $s_substr; ?><br>
</BODY></HTML>