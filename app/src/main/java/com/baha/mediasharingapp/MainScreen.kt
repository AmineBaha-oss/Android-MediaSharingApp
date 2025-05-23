package com.baha.mediasharingapp

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.baha.mediasharingapp.data.model.Post
import com.baha.mediasharingapp.viewmodel.PostViewModel
import com.baha.mediasharingapp.viewmodel.UserViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    navController: NavHostController,
    userViewModel: UserViewModel,
    postViewModel: PostViewModel
) {
    val mainNavController = rememberNavController()
    val currentRoute = mainNavController.currentBackStackEntryAsState().value?.destination?.route

    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = currentRoute == Screen.Feed.route,
                    onClick = { mainNavController.navigate(Screen.Feed.route) },
                    icon = { Icon(Icons.Default.Home, contentDescription = "Feed") },
                    label = { Text("Feed") }
                )
                NavigationBarItem(
                    selected = currentRoute == Screen.Post.route,
                    onClick = { mainNavController.navigate(Screen.Post.route) },
                    icon = { Icon(Icons.Default.Add, contentDescription = "Post") },
                    label = { Text("Post") }
                )
                NavigationBarItem(
                    selected = currentRoute == Screen.Map.route,
                    onClick = { mainNavController.navigate(Screen.Map.route) },
                    icon = { Icon(Icons.Default.Map, contentDescription = "Map") },
                    label = { Text("Map") }
                )
                NavigationBarItem(
                    selected = currentRoute == Screen.Profile.route,
                    onClick = { mainNavController.navigate(Screen.Profile.route) },
                    icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
                    label = { Text("Profile") }
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = mainNavController,
            startDestination = Screen.Feed.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Feed.route) {
                FeedScreen(
                    navController = mainNavController,
                    viewModel = postViewModel,
                    userViewModel = userViewModel
                )
            }
            composable(Screen.Post.route) {
                CreatePostScreen(
                    navController = mainNavController,
                    viewModel = postViewModel,
                    userViewModel = userViewModel
                )
            }
            composable(Screen.Map.route) {
                MapScreen(
                    userViewModel = userViewModel,
                    posts = postViewModel.posts.collectAsState(initial = emptyList()).value
                )
            }
            composable(Screen.Profile.route) {
                val userPosts = userViewModel.userPosts.collectAsState(initial = emptyList()).value
                val currentUser = userViewModel.currentUser.collectAsState().value

                ProfileScreen(
                    posts = userPosts,
                    username = currentUser?.username ?: "",
                    email = currentUser?.email ?: "",
                    onLogout = {
                        userViewModel.logout()
                        navController.navigate(Screen.Login.route) {
                            popUpTo(navController.graph.id) { inclusive = true }
                        }
                    },
                    navController = mainNavController
                )
            }
            composable(Screen.EditProfile.route) {
                EditProfileScreen(userViewModel = userViewModel, navController = mainNavController)
            }
            composable("${Screen.EditPost.route}/{postId}") { backStackEntry ->
                val postId = backStackEntry.arguments?.getString("postId")?.toLongOrNull() ?: 0
                EditPostScreen(postId = postId, viewModel = postViewModel, userViewModel = userViewModel, navController = mainNavController)
            }
            composable("${Screen.PostDetail.route}/{postId}") { backStackEntry ->
                val postId = backStackEntry.arguments?.getString("postId")?.toLongOrNull() ?: 0
                PostDetailScreen(postId = postId, viewModel = postViewModel, navController = mainNavController, userViewModel = userViewModel)
            }
        }
    }
}