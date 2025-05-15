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
    nav: NavController,
    onUpload: (String, String, Double, Double) -> Unit
) {
    var imgUri by remember { mutableStateOf<String?>(null) }
    var caption by remember { mutableStateOf("") }
    var lat    by remember { mutableStateOf(0.0) }
    var lng    by remember { mutableStateOf(0.0) }

    Column(
        Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement   = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Upload", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(16.dp))
        Text(imgUri ?: "No image selected")
        Spacer(Modifier.height(8.dp))
        Button({ /* TODO: launch image picker, set imgUri */ }) {
            Text("Select Image")
        }
        Spacer(Modifier.height(16.dp))
        OutlinedTextField(
            value     = caption,
            onValueChange = { caption = it },
            label     = { Text("Caption") },
            modifier  = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(16.dp))
        Button(
            onClick = {
                imgUri?.let { onUpload(it, caption, lat, lng) }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Upload")
        }
    }
}
