package com.example.bodyblissapp.ui.pages

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.bodyblissapp.viewmodel.SearchViewModel
import com.example.bodyblissapp.data.model.Product
import coil.compose.AsyncImage
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.stringResource
import com.example.bodyblissapp.R
import java.util.Locale


@Composable
fun SearchScreen(viewModel: SearchViewModel = viewModel()) {
    var localQuery by remember { mutableStateOf("") }
    var localMinPrice by remember { mutableStateOf("") }
    var localMaxPrice by remember { mutableStateOf("") }
    val language = remember { Locale.getDefault().language }

    val filteredItems by viewModel.filteredProducts.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.fetchProducts(language)
    }

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {

        OutlinedTextField(
            value = localQuery,
            onValueChange = { localQuery = it },
            label = { Text(stringResource(R.string.search)) },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value = localMinPrice,
                onValueChange = { localMinPrice = it },
                label = { Text(stringResource(R.string.min_price)) },
                modifier = Modifier.weight(1f)
            )
            OutlinedTextField(
                value = localMaxPrice,
                onValueChange = { localMaxPrice = it },
                label = { Text(stringResource(R.string.max_price)) },
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(onClick = {
            viewModel.updateQuery(localQuery)
            viewModel.updateMinPrice(localMinPrice.toFloatOrNull())
            viewModel.updateMaxPrice(localMaxPrice.toFloatOrNull())
        }) {
            Text(stringResource(R.string.search_button))
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(filteredItems) { product ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Row(modifier = Modifier.padding(12.dp)) {
                        AsyncImage(
                            model = product.productImageUrl,
                            contentDescription = product.name,
                            modifier = Modifier.size(80.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(product.name, style = MaterialTheme.typography.titleMedium)
                            Text(product.description, style = MaterialTheme.typography.bodySmall)
                            Text(
                                "€${"%.2f".format(product.price)}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
        }
    }
}
