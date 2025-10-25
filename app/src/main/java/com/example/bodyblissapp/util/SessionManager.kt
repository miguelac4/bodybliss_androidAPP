package com.example.bodyblissapp.util

import android.content.Context
import android.content.SharedPreferences
import android.util.Log

class SessionManager(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences("user_session", Context.MODE_PRIVATE)

    fun saveUserSession(userId: Int, name: String, email: String, role: String) {
        prefs.edit()

            .putInt("user_id", userId)
            .putString("name", name)
            .putString("email", email)
            .putBoolean("is_logged_in", true)
            .putString("role", role)
            .apply()
    }

    fun getUserId(): Int {
        return prefs.getInt("user_id", -1)
    }

    fun getUserName(): String {
        return prefs.getString("name", "") ?: ""
    }

    fun getUserEmail(): String {
        return prefs.getString("email", "") ?: ""
    }

    fun getUserRole(): String {
        return prefs.getString("role", "") ?:""
    }

    fun isLoggedIn(): Boolean {
        return prefs.getBoolean("is_logged_in", false)
    }

    fun clearSession() {
        prefs.edit().clear().apply()
    }
}
