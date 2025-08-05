package com.example.winksycall

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.winksycall.auth.AuthViewModel
import com.example.winksycall.auth.HomeScreen
import com.example.winksycall.auth.LoginScreen
import com.example.winksycall.auth.SignupScreen
import com.example.winksycall.ui.CallScreen
import com.example.winksycall.utility.saveCredentials

@Composable
fun AppNavHost() {
    val navController = rememberNavController()
    val context = LocalContext.current
    val authViewModel = androidx.lifecycle.viewmodel.compose.viewModel<AuthViewModel>()


    NavHost(
        navController = navController,
        startDestination = "login"
    ) {
        composable("signup") {
        SignupScreen(
            onSignupClicked = { email, password, confirmPassword, rememberMe ->
                if (password != confirmPassword) {
                    println("Passwords do not match.")
                    return@SignupScreen
                }

                authViewModel.signUp(email, password) { success, error ->
                    if (success) {
                        if (rememberMe) {
                            saveCredentials(
                                context = context,
                                email = email,
                                password = password
                            )
                        }
                        // Go back to login or navigate elsewhere
                        navController.popBackStack()
                    } else {
                        println("Signup failed: $error")
                    }
                }
            },
            onLoginNavigate = {
                navController.popBackStack()
            }
        )
    }

        composable("login") {
            var errorMessage by remember { mutableStateOf<String?>(null) }

            LoginScreen(
                onLoginClicked = { email, password, onResult ->
                    authViewModel.signIn(email, password ) { success, error ->
                        if (success) {
                            navController.navigate("home") {
                                // Clear the back stack so user cannot go back to login
                                popUpTo("login") { inclusive = true }
                            }
                        } else {
                            errorMessage =  error
                            onResult(false,error)
                        }
                    }
                }
                ,
                onSignupNavigate = { navController.navigate("signup") }
            )

            // Show toast if there's an error
            errorMessage?.let { msg ->
                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                errorMessage = null
            }
        }

        composable("home") {
            HomeScreen()
        }

        composable("call/{roomId}") { backStackEntry ->
            val roomId = backStackEntry.arguments?.getString("roomId") ?: return@composable
            CallScreen(roomId = roomId)
        }

    }
}