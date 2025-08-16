package com.example.winksycall.utility


import android.content.Context
import android.content.SharedPreferences

private const val PREF_NAME = "user_credentials"
private const val KEY_EMAIL = "email"
private const val KEY_PASSWORD = "password"

fun saveCredentials(context: Context, email: String, password: String) {
    val prefs: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    prefs.edit().putString(KEY_EMAIL, email).putString(KEY_PASSWORD, password).apply()
}

fun getSavedCredentials(context: Context): Pair<String?, String?> {
    val prefs: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    return prefs.getString(KEY_EMAIL, null) to prefs.getString(KEY_PASSWORD, null)
}
