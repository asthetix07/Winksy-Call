package com.example.winksycall

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.example.winksycall.ui.components.AppNavHost
import com.example.winksycall.ui.theme.WinksyCallTheme
import dagger.hilt.android.AndroidEntryPoint


class MainActivity : ComponentActivity() {
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WinksyCallTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) {
                    AppNavHost()
                }
            }
        }
    }
}


