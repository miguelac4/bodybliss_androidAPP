package com.example.bodyblissapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatDelegate
import com.example.bodyblissapp.ui.AppIndex
import com.example.bodyblissapp.ui.theme.BodyblissAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO) // Force NightMode off

        setContent {
            BodyblissAppTheme {
                AppIndex()
            }
        }
    }
}
