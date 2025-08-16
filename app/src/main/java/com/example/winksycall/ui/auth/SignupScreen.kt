package com.example.winksycall.ui.auth


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
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.example.winksycall.R
import com.example.winksycall.ui.components.LoadingOverlay

@Composable
fun SignupScreen(
    onSignupClicked: (String, String, String, Boolean) -> Unit,
    onLoginNavigate: () -> Unit
) {
//    val context = LocalContext.current
//    val authViewModel: AuthViewModel = viewModel(
//        factory = AuthViewModelFactory(context.applicationContext as Application)
//    )

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var rememberMe by remember { mutableStateOf(false) }
    var showPassword by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    val colorScheme = MaterialTheme.colorScheme

    val errorMessage by remember { mutableStateOf<String?>(null) }
    val isEmailValid = email.length >= 10
    val isFormValid = email.length >= 10 && password == confirmPassword && password.isNotEmpty()
    val isPasswordMatch = password == confirmPassword && password.isNotEmpty()


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
            Text("Sign Up", style = MaterialTheme.typography.headlineSmall.copy(color = colorScheme.onBackground))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                isError = !isEmailValid && email.isNotEmpty(),
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("example@gmail.com") }
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
                visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { showPassword = !showPassword }) {
                        Icon(
                            painter = if (showPassword) painterResource(id = R.drawable.password_2_off_24px) else painterResource(id = R.drawable.password_2_24px) ,
                            contentDescription = null
                        )
                    }
                },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("abc,123,#@")}
            )

            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = { Text("Confirm Password") },
                visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("abc,123,#@")}
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
                Checkbox(checked = rememberMe, onCheckedChange = { rememberMe = it })
                Text("Remember Me")
            }

            Button(
                onClick = {
                    isLoading = true
                    onSignupClicked(email, password, confirmPassword, rememberMe)
                },
                enabled = isFormValid && !isLoading,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Sign Up")
            }

            TextButton(onClick = onLoginNavigate) {
                Text("Already have an account? Login")
            }

            errorMessage?.let {
                Text(it,  modifier = Modifier.padding(top = 8.dp), color = MaterialTheme.colorScheme.error)
            }
        }
    }
    LoadingOverlay(isLoading = isLoading)
}


