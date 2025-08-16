package com.example.winksycall.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.delay
import com.example.winksycall.R

@Composable
fun SplashScreen(navController: NavController) {
    // Show splash for 2 seconds before navigating
    LaunchedEffect(Unit) {
        delay(1500) // 2s delay
        navController.navigate("login") {
            popUpTo("splash") { inclusive = true }
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize()
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize().background(Color.White)
        ) {
            Image(
                painter = painterResource(id = R.drawable.winksy), // your PNG in res/drawable
                contentDescription = "App Logo",
                modifier = Modifier.size(150.dp)
            )
        }
    }
}
