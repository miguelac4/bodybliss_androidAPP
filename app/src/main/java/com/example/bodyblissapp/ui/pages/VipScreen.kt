package com.example.bodyblissapp.ui.pages

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bodyblissapp.R
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.runtime.*
import kotlinx.coroutines.launch
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.bodyblissapp.viewmodel.AiViewModel

@Composable
fun VIPScreen(aiViewModel: AiViewModel = viewModel()) {
    var userInput by remember { mutableStateOf("") }
    val suggestion = aiViewModel.suggestion.collectAsState()

    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Text("🎉 Welcome to BodyBliss+", fontSize = 26.sp, fontWeight = FontWeight.Bold)
        Text(
            "As a VIP member, you enjoy exclusive features to elevate your wellness experience:",
            fontSize = 16.sp
        )
        listOf(
            "✅ Unlimited free shipping on all orders",
            "✅ Exclusive discounts (10%–20%) on selected products",
            "✅ Monthly Mystery Bliss Box",
            "✅ 24/7 AI-powered Wellness Support"
        ).forEach {
            Text(text = it, fontSize = 15.sp)
        }

        Divider(thickness = 1.dp, color = Color.LightGray)

        Text("💬 Como te sentes hoje?", fontSize = 18.sp, fontWeight = FontWeight.Medium)

        BasicTextField(
            value = userInput,
            onValueChange = { userInput = it },
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .padding(8.dp)
                .background(Color(0xFFF0F0F0))
                .padding(12.dp)
        )

        Button(onClick = {
            coroutineScope.launch {
                aiViewModel.requestSuggestion(userInput)
            }
        }) {
            Text("Obter Sugestão de Bem-Estar")
        }

        if (suggestion.value.isNotBlank()) {
            Text(
                "Sugestão do Assistente:",
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp
            )
            Text(suggestion.value, color = Color.DarkGray)
        }

        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "VIP",
            modifier = Modifier
                .height(80.dp)
                .align(Alignment.CenterHorizontally)
        )
    }
}
