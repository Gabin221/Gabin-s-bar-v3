<?php
	// inclure le fichier contenant la fonction
	include("param.inc.php");
	$mysqli = new mysqli(HOST, USER, PASSWORD, DATABASE, PORT);
	if ($mysqli -> connect_errno)
	{
			exit();
	}
	
	$mysqli->set_charset("utf8");

	header('Content-Type: text/plain; charset=utf-8');

	$query = "SELECT nom, url_image, calories FROM Sirops";
	if($resultat = $mysqli->query($query))
	{
		// chaine de caractère contenant la liste des sirops
		$liste_sirops = "";
		// pour chaque enregistrement
		while($ligne = $resultat->fetch_assoc()) 
		{
			$nom = $ligne['nom'];
			$url_image = $ligne['url_image'];
			$calories = $ligne['calories'];
			if($liste_sirops != "")
			// insérer un point-virgule entre deux sirops
			$liste_sirops = $liste_sirops."|";
			$liste_sirops = $liste_sirops.$nom.";".$url_image.";".$calories;
		}
		$resultat->free();
		echo $liste_sirops;
	}
	else
		echo "Erreur de requête : ".$mysqli->error;

	$mysqli->close();
?>
