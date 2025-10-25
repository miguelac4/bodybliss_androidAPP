<?php
header("Content-Type: application/json");
require_once("db.php");

$response = [];

if ($_SERVER['REQUEST_METHOD'] === 'GET' && isset($_GET['user_id'])) {
    $user_id = intval($_GET['user_id']);

    $stmt = $conn->prepare("SELECT profile_pic FROM users WHERE id = ?");
    $stmt->bind_param("i", $user_id);
    $stmt->execute();
    $stmt->bind_result($profile_pic);
    $stmt->fetch();

    if ($profile_pic) {
        $response["success"] = true;
        $response["image_url"] = "http://10.0.2.2:8080/uploads/avatars/" . $profile_pic;
    } else {
        $response["success"] = false;
        $response["error"] = "Imagem não encontrada.";
    }
} else {
    http_response_code(400);
    $response["success"] = false;
    $response["error"] = "user_id inválido.";
}

echo json_encode($response);
?>
