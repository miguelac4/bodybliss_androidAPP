package com.example.bodyblissapp.ui.pages

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.bodyblissapp.data.model.Product
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.bodyblissapp.viewmodel.AddCartViewModel
import com.example.bodyblissapp.viewmodel.CategoryDetailViewModel
import java.util.Locale
import androidx.compose.ui.res.stringResource
import com.example.bodyblissapp.R


@Composable
fun CategoryDetailScreen(categoria: String) {
    val viewModel: CategoryDetailViewModel = viewModel()
    val products by viewModel.products.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    val language = remember { Locale.getDefault().language }

    LaunchedEffect(categoria) {
        viewModel.loadProductsForCategory(categoria, language)
    }


    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(
            text = stringResource(R.string.products_of) + categoria,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        when {
            isLoading -> CircularProgressIndicator()
            errorMessage != null -> Text("Erro ao carregar produtos: $errorMessage", color = Color.Red)
            products.isEmpty() -> Text("Nenhum produto encontrado.")
            else -> {
                LazyColumn {
                    items(products) { product ->
                        ProductCard(product)
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }
            }
        }
    }
}


@Composable
fun ProductCard(product: Product) {
    val context = LocalContext.current
    val addCartViewModel: AddCartViewModel = viewModel()

    val addError = addCartViewModel.addErrorMap[product.id]

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column {
            // Product image
            Image(
                painter = rememberAsyncImagePainter(model = product.productImageUrl),
                contentDescription = product.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp),
                contentScale = ContentScale.Crop
            )

            // Product info + button
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    product.name,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(4.dp))

                Text(product.description, style = MaterialTheme.typography.bodyMedium)
                Spacer(modifier = Modifier.height(4.dp))

                Text("Preço: €${product.price}", style = MaterialTheme.typography.bodyMedium)
                Spacer(modifier = Modifier.height(8.dp))

                // Add to Cart button
                Button(
                    onClick = {
                        addCartViewModel.addProductToCart(context, product.id)
                    }) {
                    Text(stringResource(R.string.add_to_cart))
                }

                // Optional error message
                if (addError != null) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = addError ?: "",
                        color = Color.Red,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}
