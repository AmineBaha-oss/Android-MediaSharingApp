package com.baha.mediasharingapp

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.baha.mediasharingapp.data.model.Post
import com.baha.mediasharingapp.viewmodel.PostViewModel
import com.baha.mediasharingapp.viewmodel.UserViewModel


@Composable
fun FeedScreen(
    navController: NavController,
    viewModel: PostViewModel,
    userViewModel: UserViewModel
) {
    val posts by viewModel.posts.collectAsState(initial = emptyList())

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Button(
            onClick = { navController.navigate(Screen.Post.route) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Create New Post")
        }

        Spacer(modifier = Modifier.height(16.dp))

        posts.forEach { post ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                onClick = {
                    navController.navigate("${Screen.PostDetail.route}/${post.id}")
                }
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(post.caption, style = MaterialTheme.typography.titleMedium)
                    if (post.locationName.isNotEmpty()) {
                        Text(post.locationName, style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
    }
}