<?php
// Lib: lib-mail-v2.php (released by teacher)
use PHPMailer\PHPMailer\PHPMailer;
use PHPMailer\PHPMailer\Exception;

require 'PHPMailer/Exception.php';
require 'PHPMailer/PHPMailer.php';
require 'PHPMailer/SMTP.php';

/**
 * Send authenticated email using SMTP.
 */
function sendAuthEmail(
    $smtpServer,
    $useSSL,
    $port,
    $timeout,
    $loginName,
    $password,
    $emailFrom,
    $displayNameFrom,
    $emailTo,
    $subject,
    $message,
    $cc = NULL,
    $bcc = NULL,
    $debug = false,
    $attachmentPath = NULL
)
 {
    $mail = new PHPMailer(true);
    try {
        if ($debug) {
            $mail->SMTPDebug = 2;  // Debug output
        } else {
            $mail->SMTPDebug = 0;
        }

        $mail->isSMTP();
        $mail->Host = $smtpServer;
        $mail->SMTPAuth = true;
        $mail->Username = $loginName;
        $mail->Password = $password;

        if ($useSSL) {
            $mail->SMTPSecure = 'ssl';
        } else {
            $mail->SMTPSecure = 'tls';
        }

        $mail->Port = $port;
        $mail->Timeout = $timeout;

        $mail->setFrom($emailFrom, $displayNameFrom);
        $mail->addAddress($emailTo);

        if ($cc != NULL) {
            $mail->addCC($cc);
        }

        if ($bcc != NULL) {
            $mail->addBCC($bcc);
        }

        $mail->Subject = $subject;
        $mail->isHTML(true);
        $mail->Body = $message;
        $mail->AltBody = strip_tags($message); // Plaintext version


        if ($attachmentPath != NULL) {
            $mail->addAttachment($attachmentPath);
        }

        $mail->send();
        return true;
    } catch (Exception $e) {
        // Em vez de fazer echo, retorna o erro como texto
        return "Mailer Error: " . $mail->ErrorInfo;
    }
}
?>
