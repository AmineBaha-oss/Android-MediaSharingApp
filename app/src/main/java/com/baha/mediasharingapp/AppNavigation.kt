package com.baha.mediasharingapp

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.baha.mediasharingapp.viewmodel.PostViewModel
import com.baha.mediasharingapp.viewmodel.UserViewModel

@Composable
fun AppNavigation(
    navController: NavHostController,
    userViewModel: UserViewModel,
    postViewModel: PostViewModel
) {
    val isLoggedIn by userViewModel.isLoggedIn.collectAsState()

    NavHost(
        navController = navController,
        startDestination = if (isLoggedIn) Screen.Feed.route else Screen.Login.route
    ) {
        composable(Screen.Login.route) {
            LoginScreen(navController = navController, userViewModel = userViewModel)
        }

        composable(Screen.Signup.route) {
            SignupScreen(navController = navController, userViewModel = userViewModel)
        }

        composable(Screen.Feed.route) {
            MainScreen(navController = navController, userViewModel = userViewModel, postViewModel = postViewModel)
        }

        // Add other routes that might be directly accessed
        composable(Screen.Post.route) {
            PostScreen(navController = navController, viewModel = postViewModel, userViewModel = userViewModel)
        }

        composable(Screen.Profile.route) {
            val userPosts by userViewModel.userPosts.collectAsState()
            val currentUser = userViewModel.currentUser.collectAsState().value

            ProfileScreen(
                posts = userPosts,
                username = currentUser?.username ?: "",
                email = currentUser?.email ?: "",
                bio = currentUser?.bio ?: "",
                onLogout = {
                    userViewModel.logout()
                    navController.navigate(Screen.Login.route) {
                        popUpTo(navController.graph.id) { inclusive = true }
                    }
                },
                navController = navController
            )
        }

        composable(Screen.Map.route) {
            MapScreen(
                userViewModel = userViewModel,
                posts = postViewModel.posts.collectAsState(initial = emptyList()).value
            )
        }
    }
}