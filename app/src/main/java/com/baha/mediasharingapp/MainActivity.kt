package com.baha.mediasharingapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.rememberNavController
import com.baha.mediasharingapp.ui.theme.MediaSharingAppTheme
import com.baha.mediasharingapp.viewmodel.UserViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MediaSharingAppTheme {
                val navController = rememberNavController()
                val userViewModel = UserViewModel()
                val postViewModel = userViewModel.getPostViewModel()

                AppNavigation(
                    navController = navController,
                    userViewModel = userViewModel,
                    postViewModel = postViewModel
                )
            }
        }
    }
}