package com.example.winksycall.utility

import android.content.Context

fun saveCredentials(context: Context, email: String, password: String) {
    val prefs = context.getSharedPreferences("winksy_prefs", Context.MODE_PRIVATE)
    prefs.edit()
        .putString("email", email)
        .putString("password", password)
        .apply()
}

fun getSavedCredentials(context: Context): Pair<String?, String?> {
    val prefs = context.getSharedPreferences("winksy_prefs", Context.MODE_PRIVATE)
    val email = prefs.getString("email", null)
    val password = prefs.getString("password", null)
    return Pair(email, password)
}
