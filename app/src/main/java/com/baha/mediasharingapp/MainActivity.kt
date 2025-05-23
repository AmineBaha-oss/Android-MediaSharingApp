package com.baha.mediasharingapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import com.baha.mediasharingapp.ui.theme.MediaSharingAppTheme
import com.baha.mediasharingapp.viewmodel.UserViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MediaSharingAppTheme {
                val navController = rememberNavController() // This returns NavHostController
                val userViewModel = UserViewModel()
                val postViewModel = userViewModel.getPostViewModel()

                MainScreen(
                    navController = navController,
                    postViewModel = postViewModel,
                    userViewModel = userViewModel
                )
            }
        }
    }
}