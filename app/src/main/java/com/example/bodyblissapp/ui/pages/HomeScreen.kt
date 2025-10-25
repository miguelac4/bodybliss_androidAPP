package com.example.bodyblissapp.ui.pages

import androidx.compose.foundation.Image
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.example.bodyblissapp.R
import com.example.bodyblissapp.data.model.Section
import com.google.accompanist.pager.*
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.bodyblissapp.viewmodel.HomeViewModel
import androidx.compose.runtime.getValue

@Composable
fun HomeScreen() {
    val viewModel: HomeViewModel = viewModel()
    val averageRating by viewModel.averageRating.collectAsState()

    val sections = listOf(
        Section(R.drawable.carrosel1, R.string.t_carrosel1, R.string.d_carrosel1),
        Section(R.drawable.carrosel2, R.string.t_carrosel2, R.string.d_carrosel2),
        Section(R.drawable.carrosel3, R.string.t_carrosel3, R.string.d_carrosel3)
    )

    val pagerState = rememberPagerState()

    Column(modifier = Modifier.fillMaxSize()) {
        HorizontalPager(
            count = sections.size,
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
        ) { page ->
            val section = sections[page]
            Box {
                Image(
                    painter = painterResource(id = section.imageRes),
                    contentDescription = stringResource(id = section.titleRes),
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(12.dp))
                )
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(16.dp)
                ) {
                    Text(
                        text = stringResource(id = section.titleRes),
                        color = Color.White,
                        style = MaterialTheme.typography.headlineSmall
                    )
                    Text(
                        text = stringResource(id = section.subtitleRes),
                        color = Color.White,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        HorizontalPagerIndicator(
            pagerState = pagerState,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(8.dp),
            activeColor = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(16.dp))

        averageRating?.let { rating ->
            val title = stringResource(R.string.average_evaluation_title, stringResource(R.string.evaluation))
            val stars = stringResource(R.string.star_rating, rating)

            Text(
                text = "$title $stars",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

        }
    }
}