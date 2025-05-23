package com.baha.mediasharingapp

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.baha.mediasharingapp.viewmodel.UserViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    navController: NavController,
    userViewModel: UserViewModel
) {
    val context = LocalContext.current
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var bio by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Register", color = MaterialTheme.colorScheme.onPrimary) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Title
            Text(
                text = "Create Account",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            // Username field
            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Username") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            )

            // Email field
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            )

            // Password field
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            )

            // Confirm Password field
            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = { Text("Confirm Password") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            )

            // Bio field
            OutlinedTextField(
                value = bio,
                onValueChange = { bio = it },
                label = { Text("Bio (optional)") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                minLines = 3
            )

            // Error message
            if (errorMessage.isNotEmpty()) {
                Text(
                    text = errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Register button
            Button(
                onClick = {
                    // Validation
                    when {
                        username.isEmpty() -> errorMessage = "Username cannot be empty"
                        email.isEmpty() -> errorMessage = "Email cannot be empty"
                        password.isEmpty() -> errorMessage = "Password cannot be empty"
                        password != confirmPassword -> errorMessage = "Passwords do not match"
                        else -> {
                            errorMessage = ""
                            // Call signup from UserViewModel
                            val success = userViewModel.signup(username, email, password)
                            if (success) {
                                // Update profile with bio if provided
                                if (bio.isNotEmpty()) {
                                    userViewModel.updateUserProfile(username, email, bio)
                                }
                                NotificationHelper.notify(
                                    context,
                                    "Registration Successful",
                                    "Welcome to the app, $username!"
                                )
                                // Navigate to main screen
                                navController.navigate(Screen.Feed.route) {
                                    popUpTo(Screen.Login.route) { inclusive = true }
                                }
                            } else {
                                errorMessage = "Registration failed. Please try again."
                            }
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
            ) {
                Text("Register")
            }

            // Login link
            TextButton(
                onClick = { navController.popBackStack() }
            ) {
                Text("Already have an account? Login")
            }
        }
    }
}