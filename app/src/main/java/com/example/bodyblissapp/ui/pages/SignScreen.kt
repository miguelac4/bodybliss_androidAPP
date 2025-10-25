package com.example.bodyblissapp.ui.pages

import SignViewModel
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.bodyblissapp.ui.Page
import com.example.bodyblissapp.ui.util.CreditCardVisualTransformation
import com.example.bodyblissapp.ui.util.ExpiryDateVisualTransformation

@Composable
fun SignScreen(
    userId: Int,
    currentPageSetter: (Page) -> Unit,
    viewModel: SignViewModel = viewModel()
) {
    val scrollState = rememberScrollState()

    val isLoading = viewModel.isLoading
    val successMessage = viewModel.successMessage
    val errorMessage = viewModel.errorMessage

    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(24.dp)
    ) {
        Text("Join BodyBliss+", fontSize = 28.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(12.dp))
        Text("Unlock VIP benefits and elevate your wellness journey.", fontSize = 16.sp)

        Spacer(modifier = Modifier.height(24.dp))

        Text("🌟 Your BodyBliss+ Perks:", fontWeight = FontWeight.SemiBold)
        Spacer(modifier = Modifier.height(8.dp))
        val benefits = listOf(
            "Unlimited free shipping on all orders",
            "Exclusive discounts (10%-20%) on selected products",
            "Monthly Mystery Bliss Box",
            "24/7 AI-powered Wellness Support"
        )
        benefits.forEach {
            Text("• $it", fontSize = 14.sp)
        }

        Spacer(modifier = Modifier.height(32.dp))
        Divider()
        Spacer(modifier = Modifier.height(24.dp))

        // Form
        Card(elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("💳 Credit Card", style = MaterialTheme.typography.titleMedium)
                Text("Secure and encrypted", style = MaterialTheme.typography.bodySmall)
                Spacer(modifier = Modifier.height(12.dp))

                // Cardholder Name
                OutlinedTextField(
                    value = viewModel.nameOnCard,
                    onValueChange = { viewModel.nameOnCard = it },
                    label = { Text("Name on Card") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(12.dp))

                // Card Number
                OutlinedTextField(
                    value = viewModel.cardNumber,
                    onValueChange = { viewModel.cardNumber = it.filter { it.isDigit() }.take(16) },
                    label = { Text("Card Number") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    visualTransformation = CreditCardVisualTransformation()
                )
                Spacer(modifier = Modifier.height(12.dp))

                // Expiry Date
                OutlinedTextField(
                    value = viewModel.expiryDate,
                    onValueChange = { viewModel.expiryDate = it.filter { it.isDigit() }.take(4) },
                    label = { Text("Expiry Date (MM/YY)") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    visualTransformation = ExpiryDateVisualTransformation()
                )
                Spacer(modifier = Modifier.height(12.dp))

                // CVC
                OutlinedTextField(
                    value = viewModel.cvcCvv,
                    onValueChange = { viewModel.cvcCvv = it.filter { it.isDigit() }.take(3) },
                    label = { Text("CVC / CVV") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = { viewModel.signUpVip(userId, context) },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading
                ) {
                    Text(if (isLoading) "Processing..." else "Subscribe to BodyBliss+")
                }

                Spacer(modifier = Modifier.height(16.dp))

                viewModel.successMessage?.let {
                    Text(text = it, color = Color(0xFF2E7D32), fontWeight = FontWeight.SemiBold)
                }

                viewModel.errorMessage?.let {
                    Text(text = it, color = Color.Red, fontWeight = FontWeight.SemiBold)
                }
            }
        }


        Spacer(modifier = Modifier.height(16.dp))

        successMessage?.let {
            LaunchedEffect(successMessage) {
                if (successMessage != null) {
                    currentPageSetter(Page.VIP)
                }
            }
            Text(text = it, color = Color(0xFF2E7D32), fontWeight = FontWeight.SemiBold)
        }

        errorMessage?.let {
            Text(text = it, color = Color.Red, fontWeight = FontWeight.SemiBold)
        }
    }
}

