package com.example.bodyblissapp.ui.pages

import androidx.compose.material3.SnackbarHost
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.bodyblissapp.data.model.CartItem
import com.example.bodyblissapp.data.model.CheckoutRequest
import com.example.bodyblissapp.viewmodel.CheckoutViewModel
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import androidx.compose.material3.*
import com.example.bodyblissapp.ui.util.CreditCardVisualTransformation
import com.example.bodyblissapp.ui.util.ExpiryDateVisualTransformation


@Composable
fun CheckoutScreen(
    cartItems: List<CartItem>,
    userId: Int,
    viewModel: CheckoutViewModel = viewModel()
) {
    val context = LocalContext.current
    val total = cartItems.sumOf { it.price * it.quantity }

    var cardName by remember { mutableStateOf("") }
    var cardNumber by remember { mutableStateOf("") }
    var expiry by remember { mutableStateOf("") }
    var cvc by remember { mutableStateOf("") }

    val status by viewModel.paymentStatus.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    val scrollState = rememberScrollState()

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Card(elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("🧾 Cart Resume:", style = MaterialTheme.typography.headlineSmall)
                    Spacer(modifier = Modifier.height(8.dp))
                    cartItems.forEach {
                        Text("${it.name} x${it.quantity} = €${"%.2f".format(it.price * it.quantity)}")
                    }
                    Divider(modifier = Modifier.padding(vertical = 8.dp))
                    Text("Total: €${"%.2f".format(total)}", color = Color.Red, fontWeight = FontWeight.Bold)
                }
            }

            Card(elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("💳 Credit Card", style = MaterialTheme.typography.titleMedium)
                    Text("Secure and encrypted", style = MaterialTheme.typography.bodySmall)
                    Spacer(modifier = Modifier.height(12.dp))

                    // Cardholder Name
                    OutlinedTextField(
                        value = cardName,
                        onValueChange = { cardName = it },
                        label = { Text("Name on Card") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    // Card Number
                    OutlinedTextField(
                        value = cardNumber,
                        onValueChange = { cardNumber = it.filter { it.isDigit() }.take(16) },
                        label = { Text("Card Number") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        visualTransformation = CreditCardVisualTransformation()
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    // Expiry Date
                    OutlinedTextField(
                        value = expiry,
                        onValueChange = { expiry = it.filter { it.isDigit() }.take(4) },
                        label = { Text("Expiry Date (MM/YY)") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        visualTransformation = ExpiryDateVisualTransformation()
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    // CVC
                    OutlinedTextField(
                        value = cvc,
                        onValueChange = { cvc = it.filter { it.isDigit() }.take(3) },
                        label = { Text("CVC / CVV") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            val request = CheckoutRequest(userId, cardName, cardNumber, expiry, cvc)

                            viewModel.submitPayment(context, request)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isLoading && cardNumber.length == 16 && expiry.length == 4 && cvc.length == 3 && cardName.isNotBlank(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F))
                    ) {
                        Text(if (isLoading) "Processing..." else "Pay", color = Color.White)
                    }
                }
            }

            // Snackbar trigger on status change
            LaunchedEffect(status) {
                status?.let {
                    val message = if (it == "success") {
                        "Compra efetuada com sucesso!"
                    } else {
                        "Erro no pagamento: $it"
                    }

                    coroutineScope.launch {
                        snackbarHostState.showSnackbar(
                            message = message,
                            duration = SnackbarDuration.Short
                        )
                        viewModel.clearStatus() // Clear to avoid re-showing
                    }
                }
            }
        }
    }
}



