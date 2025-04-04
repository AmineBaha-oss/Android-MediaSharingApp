package com.baha.mediasharingapp

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Signup : Screen("signup")
    object Feed : Screen("feed")
    object Upload : Screen("upload")
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val posts = remember {
        mutableStateListOf(
            Post(1, "user1", "https://via.placeholder.com/150", "Beautiful scenery!"),
            Post(2, "user2", "https://via.placeholder.com/150", "Loving this view."),
            Post(3, "user3", "https://via.placeholder.com/150", "Had a great day!")
        )
    }

    NavHost(navController = navController, startDestination = Screen.Login.route) {
        composable(Screen.Login.route) {
            LoginScreen(navController)
        }
        composable(Screen.Signup.route) {
            SignupScreen(navController)
        }
        composable(Screen.Feed.route) {
            FeedScreen(navController, posts)
        }
        composable(Screen.Upload.route) {
            ImageUploadScreen(navController, onUpload = { imageUrl, caption ->
                val newId = (posts.maxOfOrNull { it.id } ?: 0) + 1
                posts.add(Post(newId, "CurrentUser", imageUrl, caption))
                navController.navigate(Screen.Feed.route) {
                    popUpTo(Screen.Feed.route) { inclusive = true }
                }
            })
        }
    }
}
