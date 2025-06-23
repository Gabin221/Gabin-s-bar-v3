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

	$query = "SELECT nom, url_image FROM Cafes";
	if($resultat = $mysqli->query($query))
	{
		// chaine de caractère contenant la liste des sirops
		$liste_cafes = "";
		// pour chaque enregistrement
		while($ligne = $resultat->fetch_assoc()) 
		{
			$nom = $ligne['nom'];
			$url_image = $ligne['url_image'];
			if($liste_cafes != "")
			// insérer un point-virgule entre deux cafés
			$liste_cafes = $liste_cafes."|";
			$liste_cafes = $liste_cafes.$nom.";".$url_image;
		}
		$resultat->free();
		echo $liste_cafes;
	}
	else
		echo "Erreur de requête : ".$mysqli->error;

	$mysqli->close();
?>
