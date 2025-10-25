<?php
header("Content-Type: application/json");
require_once("db.php");

$response = [];

if ($_SERVER["REQUEST_METHOD"] === "POST") {
    $email = $_POST["email"];
    $password = $_POST["password"];

    $sql = "SELECT id, name, password, role FROM users WHERE email = ?";
    $stmt = $conn->prepare($sql);
    $stmt->bind_param("s", $email);
    $stmt->execute();
    $res = $stmt->get_result();

    if ($row = $res->fetch_assoc()) {
        if (password_verify($password, $row["password"])) {
            $response["success"] = true;
            $response["message"] = "Login válido";
            $response["user_id"] = $row["id"];
            $response["name"] = $row["name"];
            $response["role"] = $row["role"];
            $response["email"] = $email;
        } else {
            $response["success"] = false;
            $response["error"] = "login failed, email or password are incorrect";
        }
    } else {
        $response["success"] = false;
        $response["error"] = "login failed, email or password are incorrect";
    }
} else {
    $response["success"] = false;
    $response["error"] = "Método inválido";
}

echo json_encode($response);
?>
