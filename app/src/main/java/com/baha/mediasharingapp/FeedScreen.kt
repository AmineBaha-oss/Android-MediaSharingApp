package com.baha.mediasharingapp

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedScreen(navController: NavController, posts: List<Post>) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Feed") },
                actions = {
                    IconButton(onClick = { navController.navigate(Screen.Upload.route) }) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = "Upload")
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(8.dp)
        ) {
            items(posts) { post ->
                PostCard(post)
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun PostCard(post: Post) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = post.username, style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Text("Image: ${post.imageUrl}")
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = post.caption)
        }
    }
}
