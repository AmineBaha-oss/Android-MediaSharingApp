package com.baha.mediasharingapp

import androidx.compose.runtime.Composable
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
    NavHost(
        navController = navController,
        startDestination = Screen.Login.route
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
    }
}