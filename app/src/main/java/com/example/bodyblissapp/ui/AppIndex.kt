package com.example.bodyblissapp.ui

import androidx.compose.runtime.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.example.bodyblissapp.ui.components.Header
import com.example.bodyblissapp.ui.pages.*
import com.example.bodyblissapp.util.SessionManager
import android.util.Log
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.bodyblissapp.viewmodel.CartViewModel


@Composable
fun AppIndex() {
    var currentPage by remember { mutableStateOf(Page.HOME) }
    var selectedCategory by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }
    val cartViewModel: CartViewModel = viewModel()
    val cartItems by cartViewModel.cartItems.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        Header(
            isLoggedIn = sessionManager.isLoggedIn(),
            onPageSelected = {
            currentPage = it
            selectedCategory = null
        })

        //Log.d("LOGIN", "user: ${sessionManager.getUserName()}")


        when (currentPage) {
            Page.HOME -> HomeScreen()

            Page.ABOUT -> AboutScreen(onPageSelected = { currentPage = it })

            Page.CATALOG -> CatalogScreen(
                onCategoryClick = { categoria ->
                    selectedCategory = categoria
                    currentPage = Page.CATEGORY_DETAIL
                }
            )

            Page.SEARCH -> SearchScreen()

            Page.LOGIN -> LoginScreen(
                onLoginSuccess = { name, email ->
                    currentPage = Page.ACCOUNT
                },
                onLoginFailed = { /* Snackbar already shown in LoginScreen */ },
                onNavigateToRegister = {
                    currentPage = Page.REGISTER
                }
            )

            Page.REGISTER -> RegisterScreen(
                onRegisterSuccess = { name, email ->
                    currentPage = Page.ACCOUNT
                },
                onRegisterFailed = { _, _ -> /* handle error */ }
            )


            Page.ACCOUNT -> AccountScreen(
                userName = sessionManager.getUserName(),
                userEmail = sessionManager.getUserEmail(),
                onLogout = {
                    sessionManager.clearSession()
                    currentPage = Page.HOME
                }
            )


            Page.CATEGORY_DETAIL -> {
                selectedCategory?.let { CategoryDetailScreen(it) }
            }

            Page.CONTACT -> ContactScreen()

            Page.CART -> CartScreen(onPageSelected = { currentPage = it })

            Page.CHECKOUT -> CheckoutScreen(
                cartItems = cartItems,
                userId = sessionManager.getUserId()
            )

            Page.SIGN -> SignScreen(
                userId = sessionManager.getUserId(),
                currentPageSetter = { currentPage = it }
            )

            Page.VIP -> VIPScreen()

            else -> Text("Coming soon...")
        }
    }
}

