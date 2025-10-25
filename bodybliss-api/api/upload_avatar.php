<?php
ini_set('display_errors', 0);
error_reporting(0);
ob_clean();
ob_start();
header("Content-Type: application/json");

require_once("db.php");

$response = [];

if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    if (!isset($_POST['user_id']) || !isset($_FILES['profile_pic'])) {
        http_response_code(400);
        $response["success"] = false;
        $response["error"] = "user_id e ficheiro são obrigatórios.";
        echo json_encode($response);
        exit;
    }

    $user_id = intval($_POST['user_id']);
    $file = $_FILES['profile_pic'];

    $extension = strtolower(pathinfo($file['name'], PATHINFO_EXTENSION));
    $allowed = ['jpg', 'jpeg', 'png', 'gif'];

    if (!in_array($extension, $allowed)) {
        $response["success"] = false;
        $response["error"] = "Formato inválido. Apenas JPG, PNG ou GIF.";
        echo json_encode($response);
        exit;
    }

    $newFileName = "p{$user_id}." . $extension;
    $uploadPath = __DIR__ . "/uploads/avatars/" . $newFileName;

    // Log input
    error_log("TMP FILE: " . $file['tmp_name']);
    error_log("UPLOAD PATH: " . $uploadPath);
    error_log("Uploaded file tmp_name: " . $file['tmp_name']);
    error_log("File exists: " . (file_exists($file['tmp_name']) ? "yes" : "no"));
    error_log("is_uploaded_file: " . (is_uploaded_file($file['tmp_name']) ? "yes" : "no"));
    file_put_contents("php://stderr", print_r($_FILES, true));
    file_put_contents("php://stderr", print_r($_POST, true));
    error_log("Final upload path: " . $uploadPath);

    if (!is_uploaded_file($file['tmp_name'])) {
        error_log("NOT A VALID UPLOADED FILE");
    }

    if (move_uploaded_file($file['tmp_name'], $uploadPath)) {
        $stmt = $conn->prepare("UPDATE users SET profile_pic = ? WHERE id = ?");
        $stmt->bind_param("si", $newFileName, $user_id);
        $stmt->execute();

        $response["success"] = true;
        $response["image_url"] = "http://10.0.2.2:8080/uploads/avatars/" . $newFileName;
    } else {
        $response["success"] = false;
        $response["error"] = "Erro ao fazer upload.";
    }

} else {
    http_response_code(405);
    $response["success"] = false;
    $response["error"] = "Método inválido.";
}

ob_clean();
echo json_encode($response);
exit;
?>
