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

	$query = "SELECT nom, url_image, quantite_alcool, style, region FROM Vins";
	if($resultat = $mysqli->query($query))
	{
		// chaine de caractère contenant la liste des vins
		$liste_vins = "";
		// pour chaque enregistrement
		while($ligne = $resultat->fetch_assoc()) 
		{
			$nom = $ligne['nom'];
			$url_image = $ligne['url_image'];
			$quantite_alcool = $ligne['quantite_alcool'];
			$region = $ligne['region'];
			$style = $ligne['style'];
			if($liste_vins != "")
			// insérer un point-virgule entre deux vins
			$liste_vins = $liste_vins."|";
			$liste_vins = $liste_vins.$nom.";".$url_image.";".$quantite_alcool.";".$region.";".$style;
		}
		$resultat->free();
		echo $liste_vins;
	}
	else
		echo "Erreur de requête : ".$mysqli->error;

	$mysqli->close();
?>
