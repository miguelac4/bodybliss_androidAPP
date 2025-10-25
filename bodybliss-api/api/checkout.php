<?php
header("Content-Type: application/json");
require_once("db.php");
require_once(__DIR__ . '/libs/lib-mail-v2.php');
require_once(__DIR__ . '/libs/dompdf/autoload.inc.php');


use Dompdf\Dompdf;
use Dompdf\Options;

$result = [];

if ($_SERVER["REQUEST_METHOD"] === "POST") {
    $userId = $_POST["user_id"];
    $lang = $_POST["lang"] ?? "en";
    $cardNumber = $_POST["card_number"];
    $expirateDate = $_POST["expirate_date"];
    $cvcCvv = $_POST["cvc_cvv"];
    $nameOnCard = $_POST["name_on_card"];


    // Verify Card
    if (
        $cardNumber !== '4242424242424242' ||
        $expirateDate !== '01/12' ||
        $cvcCvv !== '424'
    ) {
        $result["error"] = "Invalid Card";
        echo json_encode($result);
        exit;
    }

    // Catch User
    $getUser = $conn->prepare("SELECT email, name, role FROM users WHERE id = ?");
    $getUser->bind_param("i", $userId);
    $getUser->execute();
    $userResult = $getUser->get_result();

    if ($userResult->num_rows === 0) {
        $result["error"] = "Utilizador não encontrado.";
        echo json_encode($result);
        exit;
    }

    $userData = $userResult->fetch_assoc();
    $email = $userData['email'];
    $name = $userData['name'];
    $role = $userData['role'];

    $sql = "SELECT pt.name, p.price, c.quantity
            FROM cart c
            JOIN products p ON p.id = c.product_id
            JOIN product_translations pt ON pt.product_id = p.id AND pt.language = ?
            WHERE c.user_id = ?";

    $stmt = $conn->prepare($sql);
    $stmt->bind_param("si", $lang, $userId);
    $stmt->execute();

    $resultSet = $stmt->get_result();

    $products = [];
    $total = 0;

    while ($row = $resultSet->fetch_assoc()) {
        $row['subtotal'] = $row['price'] * $row['quantity'];
        $total += $row['subtotal'];
        $products[] = $row;
    }

    if (empty($products)) {
        $result["error"] = "Carrinho está vazio.";
        exit;
    }

    // Criar registo da encomenda
    $insertOrder = $conn->prepare("INSERT INTO orders (user_id, total) VALUES (?, ?)");
    $insertOrder->bind_param("id", $userId, $total);
    if (!$insertOrder->execute()) {
        http_response_code(500);
        $result["error"] = "Erro ao registar encomenda.";
        exit;
    }
    $orderId = $insertOrder->insert_id; // ID da nova encomenda

    // Inserir produtos em order_items
    $insertItem = $conn->prepare("INSERT INTO order_items (order_id, product_name, quantity, price, subtotal) VALUES (?, ?, ?, ?, ?)");
    foreach ($products as $item) {
        $insertItem->bind_param(
            "isidd",
            $orderId,
            $item['name'],
            $item['quantity'],
            $item['price'],
            $item['subtotal']
        );
        $insertItem->execute();
    }   

    // Clear User Cart
    $deleteCart = $conn->prepare("DELETE FROM cart WHERE user_id = ?");
    $deleteCart->bind_param("i", $userId);
    $deleteCart->execute();

    if ($insertItem->affected_rows > 0) {
        $result["success"] = true;
        $result["message"] = "Método válido.";
        } else {
        $result["success"] = false;
        $result["message"] = "Método vinálido.";
    }
}

// Build Email
$toEmail = $email;
$subject = "Your checkout has been completed successfully - BodyBliss";
$emailBody = '
<html>
<head>
  <style>
    body {
      font-family: "Segoe UI", Tahoma, sans-serif;
      color: #333;
    }
    .email-container {
      max-width: 600px;
      margin: auto;
      padding: 20px;
      border-radius: 8px;
      background-color: #f9f9f9;
      box-shadow: 0 2px 6px rgba(0,0,0,0.05);
    }
    h2 {
      color: #d32f2f;
    }
    ul {
      padding-left: 20px;
    }
    li {
      margin-bottom: 6px;
    }
    .footer {
      margin-top: 20px;
      font-size: 0.9em;
      color: #777;
    }
  </style>
</head>
<body>
  <div class="email-container">
    <h2>Olá, ' . htmlspecialchars($name) . '!</h2>
    <p>Obrigado por comprar na <strong>BodyBliss</strong>.</p>
    <p>A sua encomenda foi registada com sucesso com o <strong>ID #' . $orderId . '</strong>.</p>
    <p>Resumo da compra:</p>
    <ul>';


foreach ($products as $item) {
    $emailBody .= '<li>' . htmlspecialchars($item['name']) . ' x' . $item['quantity'] . ' = ' . number_format($item['subtotal'], 2) . '€</li>';
}

$emailBody .= '</ul>
    <p><strong>Total:</strong> ' . number_format($total, 2) . '€</p>
    <div class="footer">
      Encontrará a fatura em anexo.<br>
      Se tiver alguma questão, não hesite em contactar-nos.
    </div>
  </div>
</body>
</html>';

// Convert to base64
$logoPath = realpath(__DIR__ . '/uploads/imgs/logo.jpg');

if (!$logoPath || !file_exists($logoPath)) {
    http_response_code(500);
    echo json_encode([
        "error" => "Logo file still not found.",
        "checked_path" => __DIR__ . '/../uploads/imgs/logo.jpg'
    ]);
    exit;
}


$logoData = base64_encode(file_get_contents($logoPath));
$logoSrc = 'data:image/jpeg;base64,' . $logoData;

// Buscar configurações SMTP da base de dados
$sql = "SELECT * FROM email_accounts WHERE accountName = 'Bodybliss'";
$result = $conn->query($sql);

if (!$result || $result->num_rows === 0) {
    http_response_code(500);
    echo json_encode(["error" => "Falha ao obter configurações de e-mail."]);
    exit;
}

$smtpSettings = $result->fetch_assoc();

// HTML Invoice Generator
$invoiceHtml = '
<html>
<head>
  <style>
    body {
      font-family: DejaVu Sans, sans-serif;
      font-size: 12px;
      color: #333;
    }
    .header {
      text-align: center;
      margin-bottom: 20px;
    }
    .logo {
      height: 60px;
      margin-bottom: 10px;
    }
    table {
      width: 100%;
      border-collapse: collapse;
      margin-top: 15px;
    }
    th, td {
      border: 1px solid #ccc;
      padding: 6px;
      text-align: left;
    }
    th {
      background-color: #f4f4f4;
    }
    .total {
      text-align: right;
      font-weight: bold;
      margin-top: 10px;
    }
    .footer {
      margin-top: 30px;
      font-size: 10px;
      text-align: center;
      color: #999;
    }
  </style>
</head>
<body>

  <div class="header">
    <img src="' . $logoSrc . '" class="logo">
    <h2>Fatura - BodyBliss</h2>
    <p>Massagens & Velas</p>
  </div>

  <p><strong>Cliente:</strong> ' . htmlspecialchars($name) . '<br>
     <strong>Email:</strong> ' . htmlspecialchars($email) . '<br>
     <strong>ID Encomenda:</strong> #' . $orderId . '</p>

  <table>
    <thead>
      <tr><th>Produto</th><th>Quantidade</th><th>Preço</th><th>Subtotal</th></tr>
    </thead>
    <tbody>';


foreach ($products as $item) {
    $invoiceHtml .= '
      <tr>
        <td>' . htmlspecialchars($item['name']) . '</td>
        <td>' . $item['quantity'] . '</td>
        <td>' . number_format($item['price'], 2) . '€</td>
        <td>' . number_format($item['subtotal'], 2) . '€</td>
      </tr>';
}

$invoiceHtml .= '
    </tbody>
  </table>

  <p class="total">Total: ' . number_format($total, 2) . '€</p>

  <div class="footer">
    Obrigado por comprar na BodyBliss. Esta fatura é válida sem assinatura.
  </div>
</body>
</html>';

// Build and save PDF
$options = new Options();
$options->set('isHtml5ParserEnabled', true);
$dompdf = new Dompdf($options);
$dompdf->loadHtml($invoiceHtml);
$dompdf->setPaper('A4', 'portrait');
$dompdf->render();

// Guardar como ficheiro temporário
$pdfPath = __DIR__ . "/uploads_pdf/invoice_order_" . $orderId . ".pdf";
file_put_contents($pdfPath, $dompdf->output());


// Parâmetros SMTP
$smtpSent = sendAuthEmail(
    $smtpSettings['smtpServer'],
    $smtpSettings['useSSL'] == 1,
    intval($smtpSettings['port']),
    intval($smtpSettings['timeout']),
    $smtpSettings['loginName'],
    $smtpSettings['password'],
    $smtpSettings['email'],
    $smtpSettings['displayName'],
    $toEmail,
    $subject ,
    $emailBody,
    null,           // cc
    null,           // bcc
    false,          // debug
    $pdfPath
);

// Delete PDF
if (file_exists($pdfPath)) {
    unlink($pdfPath);
}


if ($smtpSent === true) {
    echo json_encode(["success" => true, "message" => "Compra efetuada e e-mail enviado."]);
} else {
    http_response_code(500);
    echo json_encode(["error" => $smtpSent]); // Mostra erro real no alert()
}

//echo json_encode($result);