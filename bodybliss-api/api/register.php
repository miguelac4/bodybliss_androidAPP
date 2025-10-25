<?php
header("Content-Type: application/json");
require_once("db.php");

$response = [];

if ($_SERVER["REQUEST_METHOD"] === "POST") {
    $name = $_POST["name"] ?? null;
    $email = $_POST["email"] ?? null;
    $password = $_POST["password"] ?? null;
    $gender = $_POST["gender"] ?? null;

    $name = trim($_POST["name"] ?? "");
    $email = trim($_POST["email"] ?? "");
    $password = trim($_POST["password"] ?? "");
    $gender = trim($_POST["gender"] ?? "");


    // Validate string lengths
    if (strlen($name) < 2 || strlen($name) > 50) {
        $response["success"] = false;
        $response["error"] = "O nome deve ter entre 2 e 50 caracteres.";
        echo json_encode($response);
        exit;
    }

    if (strlen($email) > 100) {
        $response["success"] = false;
        $response["error"] = "O email é demasiado longo.";
        echo json_encode($response);
        exit;
    }

    if (strlen($password) < 6) {
        $response["success"] = false;
        $response["error"] = "A palavra-passe deve ter pelo menos 6 caracteres.";
        echo json_encode($response);
        exit;
    }


    // Validar campos obrigatórios
    if (!$name || !$email || !$password || !$gender) {
        http_response_code(400);
        $response["success"] = false;
        $response["error"] = "Todos os campos são obrigatórios.";
        echo json_encode($response);
        exit;
    }

    if (!filter_var($email, FILTER_VALIDATE_EMAIL)) {
        $response["success"] = false;
        $response["error"] = "Formato de email inválido.";
        echo json_encode($response);
        exit;
    }

    // Verificar se o email já está registado
    $check = $conn->prepare("SELECT id FROM users WHERE email = ?");
    $check->bind_param("s", $email);
    $check->execute();
    $check->store_result();

    if ($check->num_rows > 0) {
        $response["success"] = false;
        $response["error"] = "O email já está registado.";
        echo json_encode($response);
        exit;
    }

    // Hash da password
    $hashedPassword = password_hash($password, PASSWORD_DEFAULT);

    // Inserir novo utilizador
    $insert = $conn->prepare("INSERT INTO users (name, email, password, gender, profile_pic) VALUES (?, ?, ?, ?, ?)");
    $defaultProfile = "nullprofile.jpg";
    $insert->bind_param("sssss", $name, $email, $hashedPassword, $gender, $defaultProfile);

    if ($insert->execute()) {
        $response["success"] = true;
        $response["message"] = "Registo efetuado com sucesso.";
        $response["user_id"] = $insert->insert_id;
    } else {
        $response["success"] = false;
        $response["error"] = "Erro ao registar utilizador.";
    }
} else {
    http_response_code(405);
    $response["success"] = false;
    $response["error"] = "Método inválido.";
}

echo json_encode($response);
?>
