package com.baha.mediasharingapp

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.baha.mediasharingapp.ui.Screen

@Composable
fun LoginScreen(nav: NavController) {
    var email by remember { mutableStateOf("") }
    var pw by remember { mutableStateOf("") }
    var err by remember { mutableStateOf<String?>(null) }

    Column(
        Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Login", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(16.dp))
        OutlinedTextField(email, { email = it; err = null }, label = { Text("Email") })
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(pw, { pw = it; err = null },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation()
        )
        Spacer(Modifier.height(16.dp))
        Button({
            if (email.isNotBlank() && pw.isNotBlank())
                nav.navigate(Screen.Feed.route) { popUpTo(Screen.Login.route){ inclusive=true } }
            else err = "Enter both fields"
        }, Modifier.fillMaxWidth()) {
            Text("Login")
        }
        err?.let { Text(it, color = MaterialTheme.colorScheme.error) }
        TextButton({ nav.navigate(Screen.Signup.route) }) {
            Text("Don't have an account? Sign up")
        }
    }
}
