<?php
header("Content-Type: application/json");
header("Access-Control-Allow-Origin: *");

include_once("basic_functions.php");
require_once("db.php");

e_RuntimeReport();

if ($conn->connect_error) {
    http_response_code(500);
    echo json_encode(["error" => "Database connection failed."]);
    exit;
}

// Get language parameter from URL, default to 'en'
$lang = $_GET['lang'] ?? 'en';

// SQL query updated for translations
$sql = "
    SELECT 
        p.id, 
        pt.name AS product_name, 
        pt.description AS product_description, 
        p.price, 
        ct.name AS category_name,
        c.image AS category_image,
        p.image AS product_image
    FROM products p
    LEFT JOIN product_translations pt ON p.id = pt.product_id AND pt.language = ?
    LEFT JOIN category c ON p.category_id = c.id
    LEFT JOIN category_translations ct ON c.id = ct.category_id AND ct.language = ?
";

$stmt = $conn->prepare($sql);
if (!$stmt) {
    http_response_code(500);
    echo json_encode(["error" => "Failed to prepare statement."]);
    exit;
}

$stmt->bind_param("ss", $lang, $lang);
$stmt->execute();
$result = $stmt->get_result();

$products = [];

while ($row = $result->fetch_assoc()) {
    $row["price"] = floatval($row["price"]);
    $row["product_image_url"] = "http://10.0.2.2:8080/uploads/products/" . $row["product_image"];
    $row["category_image_url"] = "http://10.0.2.2:8080/uploads/categories/" . $row["category_image"];
    $products[] = $row;
}

$json = json_encode($products);
if ($json === false) {
    http_response_code(500);
    echo json_encode(["error" => "JSON encoding failed: " . json_last_error_msg()]);
    exit;
}

http_response_code(200);
echo $json;

$conn->close();
?>
