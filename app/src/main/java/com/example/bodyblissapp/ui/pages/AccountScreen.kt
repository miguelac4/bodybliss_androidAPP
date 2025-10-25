package com.example.bodyblissapp.ui.pages

import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.RatingBar
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.example.bodyblissapp.viewmodel.AccountViewModel
import com.example.bodyblissapp.util.SessionManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.viewinterop.AndroidView
import com.example.bodyblissapp.data.network.RetrofitInstance
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.example.bodyblissapp.R

@Composable
fun AccountScreen(
    userName: String,
    userEmail: String,
    onLogout: () -> Unit
) {
    val viewModel: AccountViewModel = viewModel()
    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }

    val userId = sessionManager.getUserId()
    val avatarUrl by viewModel.avatarUrl.collectAsState()
    val error by viewModel.error.collectAsState()

    val selectedImageUri = remember { mutableStateOf<Uri?>(null) }
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        selectedImageUri.value = uri
    }

    var expanded by remember { mutableStateOf(false) }
    var showRatingDialog by remember { mutableStateOf(false) }


    // Fetch avatar once when entering the screen
    LaunchedEffect(Unit) {
        viewModel.fetchAvatar(userId)

        // HARDCODED way to ask user ratings always
//        showRatingDialog = true // Isto ativa o diálogo Compose

        // Ask user ratings 25% times
        maybeAskForRating{showRatingDialog = true}
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        // Title at the top center
        Text(stringResource(R.string.my_account), style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(24.dp))

        // Row with image and user info
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar + Buttons Column
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (selectedImageUri.value != null) {
                    Image(
                        painter = rememberAsyncImagePainter(selectedImageUri.value),
                        contentDescription = "Selected Image Preview",
                        modifier = Modifier
                            .size(150.dp)
                            .padding(8.dp),
                        contentScale = ContentScale.Crop
                    )
                } else if (avatarUrl != null) {
                    Image(
                        painter = rememberAsyncImagePainter(avatarUrl),
                        contentDescription = "Avatar",
                        modifier = Modifier
                            .size(150.dp)
                            .padding(8.dp),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    CircularProgressIndicator()
                }
                Box {
                    Button(onClick = { expanded = true }) {
                        Text(stringResource(R.string.change_image))
                    }
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.choose_a_new_image)) },
                            onClick = {
                                expanded = false
                                launcher.launch("image/*")
                            }
                        )
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.remove_image)) },
                            onClick = {
                                expanded = false
                                viewModel.removeAvatar(userId, context)
                            }
                        )
                    }
                }
                Button(
                    onClick = {
                        selectedImageUri.value?.let { uri ->
                            Log.d("UPLOAD_BTN", "Upload triggered with URI: $uri")
                            viewModel.uploadAvatar(uri, userId, context)
                        }
                    },
                    enabled = selectedImageUri.value != null
                ) {
                    Text(stringResource(R.string.upload))
                }
            }

            // Name & Email Column
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.Start
            ) {
                Text("${stringResource(R.string.account_name)} $userName", style = MaterialTheme.typography.bodyLarge)
                Text("Email: $userEmail", style = MaterialTheme.typography.bodyLarge)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Error message
        if (error != null) {
            Text("Erro: $error", color = MaterialTheme.colorScheme.error)
            Spacer(modifier = Modifier.height(8.dp))
        }

        // Logout button
        Button(onClick = onLogout) {
            Text("Logout")
        }
    }

    if (showRatingDialog) {
        RatingDialog(
            onDismiss = { showRatingDialog = false },
            onSubmit = { stars ->
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val response = RetrofitInstance.ratingApi.submitRating(userId.toString(), stars)
                        if (response.isSuccessful && response.body()?.success == true) {
                            Log.d("Rating", "Successfully submitted")
                        } else {
                            Log.e("Rating", "Failed: ${response.body()?.error}")
                        }
                    } catch (e: Exception) {
                        Log.e("Rating", "Exception: ${e.message}")
                    }
                }
            }
        )
    }


}

fun maybeAskForRating(setShowDialog: () -> Unit) {
    if ((0..4).random() == 0) {
        setShowDialog()
    }
}


@Composable
fun RatingDialog(
    onDismiss: () -> Unit,
    onSubmit: (Float) -> Unit
) {
    var rating by remember { mutableStateOf(0f) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.rate_bodybliss)) },
        text = {
            Column {
                Text(stringResource(R.string.rate_experience))
                Spacer(modifier = Modifier.height(8.dp))
                AndroidView(
                    factory = { context ->
                        RatingBar(context).apply {
                            numStars = 5
                            stepSize = 1.0f
                            setOnRatingBarChangeListener { _, value, _ ->
                                rating = value
                            }
                        }
                    }
                )
            }
        },
        confirmButton = {
            TextButton(onClick = {
                onSubmit(rating)
                onDismiss()
            }) {
                Text(stringResource(R.string.submit))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.not_now))
            }
        }
    )
}

