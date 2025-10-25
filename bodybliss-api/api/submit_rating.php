<?php
require_once("db.php");

$userId = $_POST['user_id'];
$rating = $_POST['rating'];
$timestamp = time();

// Garante que só existe 1 rating por user_id
$sql = "INSERT INTO ratings (user_id, rating, timestamp)
        VALUES (?, ?, ?)
        ON DUPLICATE KEY UPDATE
            rating = VALUES(rating),
            timestamp = VALUES(timestamp)";

$stmt = $conn->prepare($sql);
$stmt->bind_param("sdi", $userId, $rating, $timestamp);

if ($stmt->execute()) {
    echo json_encode(["success" => true]);
} else {
    echo json_encode(["success" => false, "error" => $stmt->error]);
}
?>
