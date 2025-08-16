package com.example.winksycall.ui.components

import android.app.Application
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.winksycall.ui.CallScreen
import com.example.winksycall.ui.HomeScreen
import com.example.winksycall.ui.IncomingCallScreen
import com.example.winksycall.ui.SplashScreen
import com.example.winksycall.ui.auth.LoginScreen
import com.example.winksycall.ui.auth.SignupScreen
import com.example.winksycall.viewmodels.AuthViewModel
import com.example.winksycall.viewmodels.AuthViewModelFactory
import com.example.winksycall.viewmodels.CallViewModel
import com.example.winksycall.viewmodels.CallViewModelFactory
import com.example.winksycall.viewmodels.VideoCallViewModel
import com.example.winksycall.viewmodels.VideoCallViewModelFactory

@Composable
fun AppNavHost() {
    val navController = rememberNavController()
    val context = LocalContext.current
    val authViewModel: AuthViewModel = viewModel(
        factory = AuthViewModelFactory(context.applicationContext as Application)
    )

    NavHost(navController = navController, startDestination = "splash") {
        composable("login") {
            LoginScreen(
                onLoginClicked = { email, password, onResult ->
                    authViewModel.login(email, password) { success, error ->
                        if (success) {
                            navController.navigate("home") {
                                popUpTo("login") { inclusive = true }
                            }
                        }
                        onResult(success, error)
                    }
                },
                onSignupNavigate = { navController.navigate("signup") }
            )
        }

        composable("signup") {
            SignupScreen(
                onSignupClicked = { email, password, confirm, rememberMe ->
                    if (password != confirm) {
                        Toast.makeText(context, "Passwords do not match", Toast.LENGTH_SHORT).show()
                        return@SignupScreen
                    }

                    authViewModel.signup(email, password, rememberMe) { success, error ->
                        if (success) {
                            navController.popBackStack()
                        } else {
                            Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
                        }
                    }
                },
                onLoginNavigate = { navController.popBackStack() }
            )
        }

        composable("splash") {
            SplashScreen(navController)
        }


        // HOME SCREEN
        composable("home") {
            HomeScreen(
                navController = navController
            )
        }

        composable(
            route = "incoming_call/{roomId}/{callType}/{fromUid}",
            arguments = listOf(
                navArgument("roomId") { type = NavType.StringType },
                navArgument("callType") { type = NavType.StringType },
                navArgument("fromUid") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val roomId = backStackEntry.arguments?.getString("roomId") ?: ""
            val callType = backStackEntry.arguments?.getString("callType") ?: "video"
            val fromUid = backStackEntry.arguments?.getString("fromUid") ?: ""

            // Create the CallViewModel
            val callViewModel: CallViewModel = viewModel(factory = CallViewModelFactory(context.applicationContext))

            IncomingCallScreen(
                callViewModel = callViewModel,
                roomId = roomId,
                callType = callType,
                fromUid = fromUid,
                navController = navController
            )
        }

        // CALL SCREEN (audio/video)
        composable(
            route = "video_call/{roomId}/{callType}/{isInitiator}",
            arguments = listOf(
                navArgument("roomId") { type = NavType.StringType },
                navArgument("callType") { type = NavType.StringType },
                navArgument("isInitiator") { type = NavType.BoolType }
            )
        ) { backStackEntry ->
            val roomId = backStackEntry.arguments?.getString("roomId") ?: return@composable
            val callType = backStackEntry.arguments?.getString("callType") ?: "video"
            val isInitiator = backStackEntry.arguments?.getBoolean("isInitiator") ?: false
            val viewModel: VideoCallViewModel = viewModel(
                factory = VideoCallViewModelFactory(
                    roomId = roomId,
                    isInitiator = isInitiator,
                    context = context.applicationContext as Application
                )
            )
            CallScreen(viewModel = viewModel, callType = callType, navController = navController )
        }
    }
}