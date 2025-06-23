<?php
include("param2.inc.php");

$mysqli = new mysqli(HOST, USER, PASSWORD, DATABASE, PORT);
if ($mysqli->connect_errno) {
    echo json_encode(["status" => "error", "message" => "Erreur de connexion à la base de données"]);
    exit();
}

$mysqli->set_charset("utf8");

header('Content-Type: application/json; charset=utf-8');

// Récupérer les données envoyées par l'application via GET
$pseudo = isset($_GET['pseudo']) ? $mysqli->real_escape_string($_GET['pseudo']) : '';
$mot_de_passe = isset($_GET['mot_de_passe']) ? $mysqli->real_escape_string($_GET['mot_de_passe']) : '';

if (!empty($pseudo) && !empty($mot_de_passe)) {
    // Requête SQL pour vérifier les informations de connexion
    $stmt = $mysqli->prepare("SELECT * FROM utilisateurs WHERE pseudo = ? AND mot_de_passe = ?");
    if ($stmt === false) {
        echo json_encode(["status" => "error", "message" => "Erreur de préparation de la requête: " . $mysqli->error]);
        exit();
    }

    $stmt->bind_param("ss", $pseudo, $mot_de_passe);
    $stmt->execute();
    $result = $stmt->get_result();

    if ($result->num_rows > 0) {
        // Connexion réussie
        echo json_encode(["status" => "success", "message" => "Connexion réussie"]);
    } else {
        // Informations incorrectes
        echo json_encode(["status" => "failed", "message" => "Pseudo ou mot de passe incorrect."]);
    }

    $stmt->close();
} else {
    // Paramètres manquants
    echo json_encode(["status" => "failed", "message" => "Pseudo et mot de passe sont requis."]);
}

$mysqli->close();
?>

