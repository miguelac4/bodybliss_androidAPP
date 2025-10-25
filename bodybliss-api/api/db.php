<?php
$host = 'db';
$user = 'root';
$pass = 'root';
$dbname = 'bodybliss';

$conn = new mysqli($host, $user, $pass, $dbname);
$conn->set_charset("utf8");

if ($conn->connect_error) {
    die("Erro na ligacao a base de dados: " . $conn->connect_error);
}
?>
