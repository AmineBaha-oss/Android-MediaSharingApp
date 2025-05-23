package com.baha.mediasharingapp

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.rememberNavController
import com.baha.mediasharingapp.data.PostRepository
import com.baha.mediasharingapp.data.model.DummyPostRepository
import com.baha.mediasharingapp.ui.theme.MediaSharingAppTheme
import com.baha.mediasharingapp.viewmodel.PostViewModel
import com.baha.mediasharingapp.viewmodel.UserViewModel
import com.google.android.libraries.places.api.Places
import kotlinx.coroutines.flow.MutableStateFlow

class MainActivity : ComponentActivity() {

    // Request notification permission
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            Toast.makeText(this, "Notification permission granted", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Notification permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize Places API
        try {
            if (!Places.isInitialized()) {
                Places.initialize(applicationContext, getString(R.string.google_maps_key))
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Error initializing Places API: ${e.message}", Toast.LENGTH_LONG).show()
        }

        // Request notification permission on Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }

        setContent {
            MediaSharingAppTheme {
                val navController = rememberNavController()
                val context = LocalContext.current

                // Create ViewModels
                val userViewModel = UserViewModel()
                val postRepository = DummyPostRepository(
                    mutableListOf(),
                    MutableStateFlow(emptyList()),
                    userViewModel.currentUser.value?.id
                )
                val postViewModel = PostViewModel(postRepository, userViewModel)

                // Create the notification channel
                LaunchedEffect(Unit) {
                    NotificationHelper.createNotificationChannel(context)
                }

                // Display the app
                AppNavigation(
                    navController = navController,
                    userViewModel = userViewModel,
                    postViewModel = postViewModel
                )
            }
        }
    }
}