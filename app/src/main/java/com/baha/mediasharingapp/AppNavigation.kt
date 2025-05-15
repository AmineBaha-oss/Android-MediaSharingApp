package com.baha.mediasharingapp.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.lifecycle.viewmodel.compose.viewModel
import com.baha.mediasharingapp.FeedScreen
import com.baha.mediasharingapp.MapScreen
import com.baha.mediasharingapp.data.PostRepository
import com.baha.mediasharingapp.data.local.AppDatabase
import com.baha.mediasharingapp.viewmodel.PostViewModel
import com.baha.mediasharingapp.viewmodel.PostViewModelFactory

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    // instantiate DB → repo → VM
    val context = LocalContext.current
    val db = AppDatabase.getInstance(context)
    val repo = PostRepository(db.postDao())
    val vm: PostViewModel = viewModel(
        factory = PostViewModelFactory(repo)
    )

    var selectedScreen by remember { mutableStateOf(BottomScreen.Feed) }

    Scaffold(
        bottomBar = {
            NavigationBar {
                BottomScreen.values().forEach { screen ->
                    NavigationBarItem(
                        icon = { Icon(screen.icon, contentDescription = screen.label) },
                        selected = screen == selectedScreen,
                        onClick = {
                            selectedScreen = screen
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.startDestinationId) { inclusive = false }
                                launchSingleTop = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = BottomScreen.Feed.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(BottomScreen.Feed.route) {
                FeedScreen(vm, navController)
            }
            composable(BottomScreen.Map.route) {
                MapScreen(vm)
            }
            composable(BottomScreen.Post.route) {
                PostScreen(vm)
            }
            composable(BottomScreen.Profile.route) {
                ProfileScreen()
            }
        }
    }
}

enum class BottomScreen(val route: String, val icon: androidx.compose.ui.graphics.vector.ImageVector, val label: String) {
    Feed("feed", Icons.Default.Home, "Feed"),
    Map("map", Map, "Map"),
    Post("post", Icons.Default.Add, "Post"),
    Profile("profile", Icons.Default.Person, "Profile")
}
