<?php
include('db.php');
header("Content-Type: application/json");

$data = json_decode(file_get_contents("php://input"), true);

$userId = $data['user_id'] ?? null;
$productId = $data['product_id'] ?? null;
$quantity = $data['quantity'] ?? 1;

if (!$userId || !$productId) {
    http_response_code(400);
    echo json_encode(["error" => "Missing data"]);
    exit;
}

// Optional: check if item already in cart (update instead of insert)
$stmt = $conn->prepare("SELECT id FROM cart WHERE user_id = ? AND product_id = ?");
$stmt->bind_param("ii", $userId, $productId);
$stmt->execute();
$result = $stmt->get_result();

if ($result->num_rows > 0) {
    // Already in cart - update quantity
    $stmt = $conn->prepare("UPDATE cart SET quantity = quantity + ? WHERE user_id = ? AND product_id = ?");
    $stmt->bind_param("iii", $quantity, $userId, $productId);
} else {
    // Not in cart - insert new
    $stmt = $conn->prepare("INSERT INTO cart (user_id, product_id, quantity) VALUES (?, ?, ?)");
    $stmt->bind_param("iii", $userId, $productId, $quantity);
}

if ($stmt->execute()) {
    echo json_encode(["success" => true]);
} else {
    http_response_code(500);
    echo json_encode(["error" => "DB error"]);
}
?>
