<?php
include("param2.inc.php");

$mysqli = new mysqli(HOST, USER, PASSWORD, DATABASE, PORT);
if ($mysqli->connect_errno) {
    echo json_encode(["status" => "error", "message" => "Erreur de connexion à la base de données"]);
    exit();
}

$mysqli->set_charset("utf8");
header('Content-Type: application/json; charset=utf-8');

$pseudo = $_POST['pseudo'] ?? '';
$password = $_POST['password'] ?? '';

if (!empty($pseudo) && !empty($password)) {
    // Vérifier si pseudo déjà existant
    $stmt = $mysqli->prepare("SELECT id FROM utilisateurs WHERE pseudo = ?");
    $stmt->bind_param("s", $pseudo);
    $stmt->execute();
    $stmt->store_result();

    if ($stmt->num_rows > 0) {
        echo json_encode(["status" => "failed", "message" => "Pseudo déjà utilisé."]);
    } else {
        $stmt->close();
        $stmt = $mysqli->prepare("INSERT INTO utilisateurs (pseudo, mot_de_passe) VALUES (?, ?)");
        $stmt->bind_param("ss", $pseudo, $password);

        if ($stmt->execute()) {
            echo json_encode(["status" => "success", "message" => "Compte créé."]);
        } else {
            echo json_encode(["status" => "error", "message" => "Erreur lors de l'insertion."]);
        }
    }
    $stmt->close();
} else {
    echo json_encode(["status" => "failed", "message" => "Pseudo et mot de passe sont requis."]);
}

$mysqli->close();
?>
