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

	$query = "SELECT nom, url_image, quantite_alcool, bouteille, style, pays FROM Bieres";
	if($resultat = $mysqli->query($query))
	{
		// chaine de caractère contenant la liste des bières
		$liste_bieres = "";
		// pour chaque enregistrement
		while($ligne = $resultat->fetch_assoc()) 
		{
			$nom = $ligne['nom'];
			$url_image = $ligne['url_image'];
			$quantite_alcool = $ligne['quantite_alcool'];
			$bouteille = $ligne['bouteille'];
			$style = $ligne['style'];
			$pays = $ligne['pays'];
			if($liste_bieres != "")
			// insérer un point-virgule entre deux bières
			$liste_bieres = $liste_bieres."|";
			$liste_bieres = $liste_bieres.$nom.";".$url_image.";".$quantite_alcool.";".$bouteille.";".$style.";".$pays;
		}
		$resultat->free();
		echo $liste_bieres;
	}
	else
		echo "Erreur de requête : ".$mysqli->error;

	$mysqli->close();
?>
