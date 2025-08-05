package com.example.winksycall.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.example.winksycall.ui.LoadingOverlay
import com.example.winksycall.utility.getSavedCredentials

@Composable
fun LoginScreen(
    onLoginClicked: (email: String, password: String, onResult: (Boolean, String?) -> Unit) -> Unit,
    onSignupNavigate: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }
    val context = LocalContext.current
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    val (savedEmail, savedPassword) = getSavedCredentials(context)

    val isEmailValid = email.length >= 10
    val isPasswordMatch =  password.isNotEmpty()
    val isFormValid = isEmailValid && isPasswordMatch

    // Optionally prefill if not null
    LaunchedEffect(Unit) {
        if (!savedEmail.isNullOrEmpty()) {
            email = savedEmail
        }
        if (!savedPassword.isNullOrEmpty()) {
            password = savedPassword
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize().navigationBarsPadding().statusBarsPadding()
            .background(colorScheme.background)
            .then(if (isLoading) Modifier.blur(8.dp) else Modifier)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Login",
            style = MaterialTheme.typography.headlineSmall.copy(color = colorScheme.onBackground)
        )

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            isError = !isEmailValid && email.isNotEmpty(),
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                cursorColor = colorScheme.primary,
                focusedBorderColor = colorScheme.primary,
                unfocusedBorderColor = colorScheme.secondary,
                focusedLabelColor = colorScheme.primary
            ),
            textStyle = TextStyle(color = colorScheme.onSurface),
            placeholder = { Text("example@gmail.com",color = Color.LightGray) }
        )
            if (!isEmailValid && email.isNotEmpty()) {
            Text(
                text = "Email must be at least 10 characters",
                color = Color.Red,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.align(Alignment.Start)
            )
            }

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                singleLine = true,
                visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                trailingIcon = {
                    IconButton(onClick = { showPassword = !showPassword }) {
                        Icon(
                            imageVector = if (showPassword) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = if (showPassword) "Hide password" else "Show password"
                        )
                    }
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = colorScheme.primary,
                    unfocusedBorderColor = colorScheme.secondary,
                    cursorColor = colorScheme.primary,
                    focusedLabelColor = colorScheme.primary
                ),
                textStyle = TextStyle(color = colorScheme.onSurface),
                placeholder = { Text("abc,123,#@", color = Color.LightGray) }
            )



            Button(
            onClick = {
                isLoading = true
                onLoginClicked(email, password) { success, error ->
                    isLoading = false
                    if (!success) {
                        errorMessage = error
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = colorScheme.primary,
                contentColor = colorScheme.onPrimary
            ),
            shape = RoundedCornerShape(8.dp),
            enabled = isFormValid && !isLoading
        ) {
            Text("Login")
        }

        errorMessage?.let {
            Text(
                text = it,
                color = Color.Red,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        TextButton(
            onClick = onSignupNavigate
        ) {
            Text("Don't have an account? Sign Up")
        }
    }

    }
    LoadingOverlay(isLoading = isLoading)
}
