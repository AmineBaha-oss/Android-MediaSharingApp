package com.baha.mediasharingapp

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.baha.mediasharingapp.viewmodel.UserViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    userViewModel: UserViewModel,
    navController: NavController
) {
    val context = LocalContext.current
    val currentUser = userViewModel.currentUser.collectAsState().value

    // Set up state for form fields
    var username by remember { mutableStateOf(currentUser?.username ?: "") }
    var email by remember { mutableStateOf(currentUser?.email ?: "") }
    var bio by remember { mutableStateOf(currentUser?.bio ?: "") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Profile", color = MaterialTheme.colorScheme.onPrimary) },
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
                .padding(16.dp)
        ) {
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

            // Bio field
            OutlinedTextField(
                value = bio,
                onValueChange = { bio = it },
                label = { Text("Bio") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                minLines = 3
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Save button
            Button(
                onClick = {
                    if (username.isNotEmpty() && email.isNotEmpty()) {
                        val success = userViewModel.updateUserProfile(username, email, bio)
                        if (success) {
                            NotificationHelper.notify(
                                context,
                                "Profile Updated",
                                "Your profile has been updated successfully."
                            )
                            navController.popBackStack()
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Save Changes")
            }
        }
    }
}