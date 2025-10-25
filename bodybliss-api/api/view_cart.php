<?php
header("Content-Type: application/json");
require_once("db.php");

$result = [];

if ($_SERVER["REQUEST_METHOD"] === "POST") {
    $userId = $_POST["user_id"];
    $lang = $_POST["lang"] ?? "en";
        
    $sql = "SELECT user_id, c.product_id, quantity, pt.name, p.price
            FROM cart c
            JOIN products p ON p.id = c.product_id
            JOIN product_translations pt ON pt.product_id = p.id AND pt.language = ?
            WHERE c.user_id = ?";

    $stmt = $conn->prepare($sql);
    $stmt->bind_param("si", $lang, $userId);
    $stmt->execute();
    
    $resultSet = $stmt->get_result();
    $data = [];

    while ($row = $resultSet->fetch_assoc()) {
        $data[] = $row;
    }

    $result["data"] = $data;
    $result["success"] = true;
    $result["error"] = "Método válido.";
} else {
    $result["success"] = false;
    $result["error"] = "Método inválido.";
}

echo json_encode($result);
?>
