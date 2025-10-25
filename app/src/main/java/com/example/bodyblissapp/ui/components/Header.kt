package com.example.bodyblissapp.ui.components

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.bodyblissapp.R
import com.example.bodyblissapp.ui.Page
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.example.bodyblissapp.util.SessionManager
import com.example.bodyblissapp.viewmodel.AccountViewModel
import androidx.compose.runtime.getValue



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Header(
    isLoggedIn: Boolean,
    onPageSelected: (Page) -> Unit
) {

    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }
    val viewModel: AccountViewModel = viewModel()

    val avatarUrl by viewModel.avatarUrl.collectAsState()
    val userId = sessionManager.getUserId()

    LaunchedEffect(isLoggedIn) {
        if (isLoggedIn) {
            viewModel.fetchAvatar(userId)
        }
    }

    Column {
        TopAppBar(
            title = {},
            navigationIcon = {
                Image(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = "Logo",
                    modifier = Modifier
                        .size(180.dp)
                        .padding(6.dp)
                )
            },
            actions = {
                IconButton(onClick = { onPageSelected(Page.SEARCH) }) {
                    Icon(Icons.Default.Search, contentDescription = "Search")
                }
                IconButton(onClick = {
                    if (isLoggedIn) {
                        onPageSelected(Page.ACCOUNT)
                    } else {
                        onPageSelected(Page.LOGIN)
                    }
                }) {
                    if (isLoggedIn && avatarUrl != null) {
                        Image(
                            painter = rememberAsyncImagePainter(avatarUrl),
                            contentDescription = "Avatar",
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                        )
                    } else {
                        Icon(Icons.Default.AccountCircle, contentDescription = "Profile")
                    }
                    }

                if (isLoggedIn == true) {

                    if (sessionManager.getUserRole() == "client") {
                        IconButton(onClick = { onPageSelected(Page.SIGN) }) {
                            Icon(Icons.Default.Favorite, contentDescription = "Join BodyBliss+")
                        }
                    }else {
                        IconButton(onClick = { onPageSelected(Page.VIP) }) {
                            Icon(Icons.Default.Favorite, contentDescription = "Enter BodyBliss+")
                        }
                    }


                    IconButton(onClick = { onPageSelected(Page.CART) }) {
                        Icon(Icons.Default.ShoppingCart, contentDescription = "Cart")
                    }
                }
            }
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(onClick = { onPageSelected(Page.HOME) }) {
                Text(stringResource(id = R.string.home)) }
            Button(onClick = { onPageSelected(Page.ABOUT) }) {
                Text(stringResource(id = R.string.about)) }
            Button(onClick = { onPageSelected(Page.CATALOG) }) {
                Text(stringResource(id = R.string.catalog)) }
            Button(onClick = { onPageSelected(Page.CONTACT) }) {
                Text(stringResource(id = R.string.contact)) }
        }
    }
}