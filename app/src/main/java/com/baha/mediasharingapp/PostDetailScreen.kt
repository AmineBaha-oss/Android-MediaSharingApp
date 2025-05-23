package com.baha.mediasharingapp

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.baha.mediasharingapp.viewmodel.PostViewModel
import com.baha.mediasharingapp.viewmodel.UserViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostDetailScreen(
    postId: Long,
    viewModel: PostViewModel,
    navController: NavController,
    userViewModel: UserViewModel
) {
    val context = LocalContext.current
    val allPosts = userViewModel.getAllPosts()
    val post = remember(postId, allPosts) { allPosts.find { it.id == postId } }
    val currentUser = userViewModel.currentUser.collectAsState().value

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Post Detail", color = MaterialTheme.colorScheme.onPrimary) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                actions = {
                    if (post != null && currentUser != null && post.userId == currentUser.id) {
                        IconButton(onClick = {
                            navController.navigate("${Screen.EditPost.route}/${post.id}")
                        }) {
                            Icon(
                                Icons.Default.Edit,
                                contentDescription = "Edit Post",
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    }
                }
            )
        }
    ) { padding ->
        if (post == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text("Post not found")
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp)
            ) {
                // User info
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 8.dp)
                ) {
                    val username = viewModel.getUsernameForPost(post.userId)
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = username.take(1).uppercase(),
                            color = MaterialTheme.colorScheme.onPrimary,
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = username,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Post image
                Image(
                    painter = rememberAsyncImagePainter(post.imagePath),
                    contentDescription = "Post image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Caption
                Text(
                    text = post.caption,
                    style = MaterialTheme.typography.bodyLarge
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Location
                if (post.locationName.isNotEmpty()) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.LocationOn,
                            contentDescription = "Location",
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = post.locationName,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Map button if location is available
                    if (post.lat != 0.0 && post.lng != 0.0) {
                        Button(
                            onClick = {
                                // Navigate to map with this specific post ID for focus
                                navController.navigate("map/${post.id}")
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("View on Map")
                        }
                    }
                }
            }
        }
    }
}