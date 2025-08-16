package com.example.winksycall.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class CallViewModelFactory(
    private val context: Context
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CallViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CallViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
