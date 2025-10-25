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

    if($role == "vip"){
      $result["error"] = "User is already VIP.";
      echo json_encode($result);
      exit;
    }

    // Change "role" to "vip"
    $updateRole = $conn->prepare("UPDATE users SET role = 'vip' WHERE id = ?");
    $updateRole->bind_param("i", $userId);
    $updateRole->execute();

    if ($updateRole->affected_rows === 0) {
    echo json_encode(["error" => "Failed to upgrade user to VIP."]);
    exit;
}

    // Simulate order data (replace with real data from DB or session)
    $orderId = rand(1000, 9999); // or fetch from orders table
    $products = [
      ["name" => "BodyBliss+ Membership", "quantity" => 1, "price" => 29.99, "subtotal" => 29.99]
    ];
    $total = 29.99;
    
}

// Build Email
$toEmail = $email;
$subject = "You are now a Bodybliss+ Member - BodyBliss";
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
    <h2>Hello, ' . htmlspecialchars($name) . '!</h2>
    <p>Thank you for purchasing the <strong>BodyBliss+</strong> membership!</p>
    <p>Your order (ID <strong>#' . $orderId . '</strong>) has been successfully processed.</p>

    <p>As a <strong>BodyBliss+ VIP member</strong>, you now have access to:</p>
    <ul>
      <li>Unlimited free shipping on all orders</li>
      <li>Exclusive discounts (between 10% and 20%) on selected shop items</li>
      <li>Monthly Mystery Bliss Box delivered to your door</li>
      <li>24/7 AI-powered Wellness Support</li>
    </ul>

    <p>Official Invoice in Attached file.</p>

    <div class="footer">
      If you have any questions, feel free to contact us at any time.<br>
      Thank you for choosing <strong>BodyBliss</strong>!
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
    .vip-section {
      margin-top: 30px;
      padding: 15px;
      border: 1px solid #d32f2f;
      border-radius: 8px;
      background-color: #fff3f3;
    }
    .vip-section h3 {
      color: #d32f2f;
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
    <h2>Invoice - BodyBliss</h2>
    <p>Massages & Candles</p>
  </div>

  <p><strong>Customer:</strong> ' . htmlspecialchars($name) . '<br>
     <strong>Email:</strong> ' . htmlspecialchars($email) . '<br>
     <strong>Order ID:</strong> #' . $orderId . '</p>

  <table>
    <thead>
      <tr><th>Product</th><th>Quantity</th><th>Price</th><th>Subtotal</th></tr>
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

  <div class="vip-section">
    <h3>Welcome to BodyBliss+</h3>
    <p>As a VIP member, you now enjoy:</p>
    <ul>
      <li>✔️ Unlimited free shipping on all orders</li>
      <li>✔️ Exclusive discounts (10%-20%) on selected products</li>
      <li>✔️ Monthly Mystery Bliss Box</li>
      <li>✔️ 24/7 AI-powered Wellness Support</li>
    </ul>
    <p>Thank you for upgrading. We’re thrilled to have you on board!</p>
  </div>

  <div class="footer">
    Thank you for shopping with BodyBliss. This invoice is valid without signature.
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
    echo json_encode(["success" => true, "message" => "Compra efetuada e e-mail enviado.", "role" => "vip"]);
} else {
    http_response_code(500);
    echo json_encode(["error" => $smtpSent]); // Mostra erro real no alert()
}

//echo json_encode($result);