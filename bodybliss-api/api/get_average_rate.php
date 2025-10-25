<?php
require_once("db.php");

$sql = "SELECT AVG(rating) AS avg_rating FROM ratings";
$result = $conn->query($sql);
$row = $result->fetch_assoc();

echo json_encode([
    "avg_rating" => round(floatval($row['avg_rating']), 1)
]);
?>