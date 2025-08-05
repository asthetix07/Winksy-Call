package com.example.winksycall.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.example.winksycall.ui.LoadingOverlay

@Composable
fun SignupScreen(
    onSignupClicked: (email: String, password: String, confirmPassword: String, rememberMe: Boolean) -> Unit,
    onLoginNavigate: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var rememberMe by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    var showPassword by remember { mutableStateOf(false) }
    val isEmailValid = email.length >= 10
    val isPasswordMatch = password == confirmPassword && password.isNotEmpty()
    val isFormValid = isEmailValid && isPasswordMatch && password.isNotEmpty() && confirmPassword.isNotEmpty()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .navigationBarsPadding()
            .statusBarsPadding()
            .background(colorScheme.background)
            .then(if (isLoading) Modifier.blur(8.dp) else Modifier)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Sign Up", style = MaterialTheme.typography.headlineSmall.copy(color = colorScheme.onBackground))

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
                placeholder = { Text("example@gmail.com", color = Color.LightGray) }
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
                trailingIcon = {
                    val icon = if (showPassword) Icons.Default.Favorite else Icons.Default.FavoriteBorder
                    IconButton(onClick = { showPassword = !showPassword }) {
                        Icon(imageVector = icon, contentDescription = "Toggle Password")
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = colorScheme.primary,
                    unfocusedBorderColor = colorScheme.secondary,
                    cursorColor = colorScheme.primary,
                    focusedLabelColor = colorScheme.primary
                ),
                textStyle = TextStyle(color = colorScheme.onSurface),
                placeholder = { Text("abc,123,#@", color = Color.LightGray) }
            )

            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = { Text("Confirm Password") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                isError = !isPasswordMatch && confirmPassword.isNotEmpty(),
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = colorScheme.primary,
                    unfocusedBorderColor = colorScheme.secondary,
                    cursorColor = colorScheme.primary,
                    focusedLabelColor = colorScheme.primary
                ),
                textStyle = TextStyle(color = colorScheme.onSurface),
                placeholder = { Text("abc,123,#@", color = Color.LightGray) }
            )

            if (!isPasswordMatch && confirmPassword.isNotEmpty()) {
                Text(
                    text = "Passwords do not match",
                    color = Color.Red,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.align(Alignment.Start)
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Checkbox(
                    checked = rememberMe,
                    onCheckedChange = { rememberMe = it },
                    colors = CheckboxDefaults.colors(
                        checkedColor = colorScheme.primary,
                        uncheckedColor = colorScheme.secondary
                    )
                )
                Text("Remember Me", color = colorScheme.onSurface)
            }

            Button(
                onClick = {
                    isLoading = true
                    onSignupClicked(email, password, confirmPassword, rememberMe)
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = isFormValid && !isLoading,
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorScheme.primary,
                    contentColor = colorScheme.onPrimary
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Sign Up")
            }

            TextButton(onClick = onLoginNavigate) {
                Text("Already have an account? Login")
            }
        }
    }

    LoadingOverlay(isLoading = isLoading)
}

