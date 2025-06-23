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

	$query = "SELECT nom, url_image, quantite_alcool, region FROM Extravagants";
	if($resultat = $mysqli->query($query))
	{
		// chaine de caractère contenant la liste des extravagants
		$liste_extravagants = "";
		// pour chaque enregistrement
		while($ligne = $resultat->fetch_assoc()) 
		{
			$nom = $ligne['nom'];
			$url_image = $ligne['url_image'];
			$quantite_alcool = $ligne['quantite_alcool'];
			$region = $ligne['region'];
			if($liste_extravagants != "")
			// insérer un point-virgule entre deux extravagants
			$liste_extravagants = $liste_extravagants."|";
			$liste_extravagants = $liste_extravagants.$nom.";".$url_image.";".$quantite_alcool;
		}
		$resultat->free();
		echo $liste_extravagants;
	}
	else
		echo "Erreur de requête : ".$mysqli->error;

	$mysqli->close();
?>
