package com.example.winksycall.viewmodels

import android.app.Application
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class VideoCallViewModelFactory(
    private val roomId: String,
    private val isInitiator: Boolean,
    private val context: Context
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(VideoCallViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return VideoCallViewModel(
                app = context.applicationContext as Application,
                roomId = roomId,
                isInitiator = isInitiator
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}