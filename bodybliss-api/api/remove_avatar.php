<?php
ini_set('display_errors', 0);
error_reporting(0);
header("Content-Type: application/json");

require_once("db.php");

$response = [];

if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    if (!isset($_POST['user_id'])) {
        http_response_code(400);
        $response["success"] = false;
        $response["error"] = "user_id é obrigatório.";
        echo json_encode($response);
        exit;
    }

    $user_id = intval($_POST['user_id']);

    $defaultPic = "nullprofile.jpg";
    $stmt = $conn->prepare("UPDATE users SET profile_pic = ? WHERE id = ?");
    $stmt->bind_param("si", $defaultPic, $user_id);

    if ($stmt->execute()) {
        $response["success"] = true;
        $response["image_url"] = "http://10.0.2.2:8080/uploads/avatars/" . $defaultPic;
    } else {
        $response["success"] = false;
        $response["error"] = "Erro ao atualizar avatar.";
    }
} else {
    http_response_code(405);
    $response["success"] = false;
    $response["error"] = "Método inválido.";
}

echo json_encode($response);
exit;
