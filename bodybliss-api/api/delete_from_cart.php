<?php
header("Content-Type: application/json");
require_once("db.php");

$result = [];

if ($_SERVER["REQUEST_METHOD"] === "POST") {
    $userId = $_POST["user_id"];
    $productId = $_POST["product_id"];

    $sql = "DELETE FROM cart WHERE user_id = ? AND product_id = ?";

    $stmt = $conn->prepare($sql);
    $stmt->bind_param("si", $userId, $productId);
    $stmt->execute();


    if ($stmt->affected_rows > 0) {
    $result["success"] = true;
    $result["message"] = "Método válido.";
    } else {
    $result["success"] = false;
    $result["message"] = "Método vinálido.";
    }
}

echo json_encode($result);
