package com.example.bodyblissapp.ui.pages

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import com.example.bodyblissapp.R
import com.example.bodyblissapp.ui.Page

@Composable
fun AboutScreen(onPageSelected: (Page) -> Unit) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.about_banner),
            contentDescription = "Banner sobre BodyBliss",
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clip(RoundedCornerShape(12.dp)),
            contentScale = ContentScale.Crop
        )

        Text(
            text = stringResource(id = R.string.d_banner_about),
            fontStyle = FontStyle.Italic,
            color = Color.Gray,
            style = MaterialTheme.typography.bodyMedium
        )

        Text(
            text = stringResource(id = R.string.text_about),
            style = MaterialTheme.typography.bodyMedium
        )

        Text("O que oferecemos:", style = MaterialTheme.typography.titleMedium)
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(text = stringResource(id = R.string.service_about1))
            Text(text = stringResource(id = R.string.service_about2))
            Text(text = stringResource(id = R.string.service_about3))
        }

        Text(
            text = stringResource(id = R.string.final_about),
            fontStyle = FontStyle.Italic,
            style = MaterialTheme.typography.bodySmall,
            color = Color.Gray
        )

        Button(
            onClick = { onPageSelected(Page.CATALOG) },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text(text = stringResource(id = R.string.button_about))
        }

        Spacer(modifier = Modifier.height(12.dp))
    }
}
