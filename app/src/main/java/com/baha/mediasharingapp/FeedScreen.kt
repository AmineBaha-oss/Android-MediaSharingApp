package com.baha.mediasharingapp

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.baha.mediasharingapp.data.model.Post
import com.baha.mediasharingapp.viewmodel.PostViewModel
import com.baha.mediasharingapp.viewmodel.UserViewModel
import androidx.compose.runtime.collectAsState
import androidx.compose.foundation.background

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedScreen(
    navController: NavController,
    viewModel: PostViewModel,
    userViewModel: UserViewModel
) {
    val context = LocalContext.current
    val allPosts = userViewModel.allPosts.collectAsState().value
    val isLoggedIn = userViewModel.isLoggedIn.collectAsState().value
    val currentUser = userViewModel.currentUser.collectAsState().value

    // Make sure to refresh posts whenever screen is shown
    LaunchedEffect(Unit) {
        userViewModel.updateUserPostsAfterChange()
        viewModel.refreshPosts()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Social Feed", color = MaterialTheme.colorScheme.onPrimary) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    ) { padding ->
        if (allPosts.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("No posts to display")
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = {
                        userViewModel.updateUserPostsAfterChange()
                        viewModel.refreshPosts()
                        Toast.makeText(context, "Refreshing posts...", Toast.LENGTH_SHORT).show()
                    }) {
                        Text("Refresh")
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(vertical = 16.dp)
            ) {
                items(allPosts) { post ->
                    PostCard(
                        post = post,
                        username = viewModel.getUsernameForPost(post.userId),
                        onPostClick = {
                            navController.navigate("${Screen.PostDetail.route}/${post.id}")
                        },
                        onLocationClick = {
                            navController.navigate("map/${post.id}")
                        },
                        onMenuClick = { showOptions ->
                            if (isLoggedIn && currentUser?.id == post.userId) {
                                navController.navigate("${Screen.EditPost.route}/${post.id}")
                            } else {
                                Toast.makeText(
                                    context,
                                    "You can only edit your own posts",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun PostCard(
    post: Post,
    username: String,
    onPostClick: () -> Unit,
    onLocationClick: () -> Unit,
    onMenuClick: (Boolean) -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onPostClick),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // User info row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
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
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = username,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Menu icon
                Box {
                    IconButton(onClick = { showMenu = true }) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "More options"
                        )
                    }

                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Edit Post") },
                            onClick = {
                                onMenuClick(true)
                                showMenu = false
                            }
                        )
                    }
                }
            }

            // Post image
            Image(
                painter = rememberAsyncImagePainter(post.imagePath),
                contentDescription = "Post image",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp),
                contentScale = ContentScale.Crop
            )

            // Post content
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = post.caption,
                    style = MaterialTheme.typography.bodyLarge,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(8.dp))

                if (post.locationName.isNotEmpty()) {
                    Row(
                        modifier = Modifier.clickable(onClick = onLocationClick),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = "Location",
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = post.locationName,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}