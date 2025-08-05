package com.example.winksycall.database

data class Contact(
    val name: String = "",
    val email: String = "",
    val id: String = "" // unique identifier (can be Firebase push key or UUID)
)
