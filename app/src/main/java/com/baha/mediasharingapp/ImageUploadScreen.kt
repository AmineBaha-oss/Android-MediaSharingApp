package com.baha.mediasharingapp

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun ImageUploadScreen(
    navController: NavController,
    onUpload: (String, String) -> Unit
) {
    var selectedImage by remember { mutableStateOf<String?>(null) }
    var caption by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Image Upload", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        if (selectedImage != null) {
            Text("Selected Image: $selectedImage")
        } else {
            Text("No image selected")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = {
            selectedImage = "https://via.placeholder.com/300"
        }) {
            Text("Select Image")
        }
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = caption,
            onValueChange = { caption = it },
            label = { Text("Caption") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                if (selectedImage != null && caption.isNotBlank()) {
                    onUpload(selectedImage!!, caption)
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Upload Image")
        }
    }
}
