package com.baha.mediasharingapp

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.baha.mediasharingapp.data.model.Post
import com.baha.mediasharingapp.viewmodel.PostViewModel
import com.baha.mediasharingapp.viewmodel.UserViewModel
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import android.app.Activity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditPostScreen(
    postId: Long,
    viewModel: PostViewModel,
    userViewModel: UserViewModel,
    navController: NavController
) {
    val context = LocalContext.current
    val post = remember { viewModel.getPostById(postId) }

    if (post == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Post not found")
        }
        return
    }

    var caption by remember { mutableStateOf(post.caption) }
    var locationName by remember { mutableStateOf(post.locationName) }
    var lat by remember { mutableStateOf(post.lat) }
    var lng by remember { mutableStateOf(post.lng) }
    val imagePath = post.imagePath

    // Places API initialization
    LaunchedEffect(Unit) {
        try {
            if (!com.google.android.libraries.places.api.Places.isInitialized()) {
                com.google.android.libraries.places.api.Places.initialize(
                    context,
                    context.getString(R.string.google_maps_key)
                )
            }
        } catch (e: Exception) {
            Toast.makeText(context, "Error initializing Places API: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    val autocompleteLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.let { intent ->
                try {
                    val place = Autocomplete.getPlaceFromIntent(intent)
                    place.latLng?.let { latLng ->
                        lat = latLng.latitude
                        lng = latLng.longitude
                        locationName = place.name ?: place.address ?: "Selected Location"

                        // Store the location name
                        userViewModel.addLocationName(lat, lng, locationName)
                    }
                } catch (e: Exception) {
                    Toast.makeText(context, "Error processing location: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Post", color = MaterialTheme.colorScheme.onPrimary) },
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
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Show post image
            Image(
                painter = rememberAsyncImagePainter(imagePath),
                contentDescription = "Post image",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Caption field
            OutlinedTextField(
                value = caption,
                onValueChange = { caption = it },
                label = { Text("Caption") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Location button
            Button(
                onClick = {
                    try {
                        if (!com.google.android.libraries.places.api.Places.isInitialized()) {
                            Toast.makeText(context, "Places API not initialized. Using current location.", Toast.LENGTH_SHORT).show()
                            return@Button
                        }

                        val fields = listOf(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS)
                        val intent = Autocomplete.IntentBuilder(
                            AutocompleteActivityMode.OVERLAY,
                            fields
                        ).build(context)
                        autocompleteLauncher.launch(intent)
                    } catch (e: Exception) {
                        Toast.makeText(context, "Error launching location picker: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = "Update Location"
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(if (locationName.isNotEmpty()) locationName else "Add Location")
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    if (caption.isBlank()) {
                        Toast.makeText(context, "Caption cannot be empty", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    // Create updated post
                    val updatedPost = post.copy(
                        caption = caption,
                        locationName = locationName,
                        lat = lat,
                        lng = lng
                    )

                    // Update the post
                    viewModel.addPost(updatedPost)

                    // Refresh posts
                    viewModel.refreshPosts()
                    userViewModel.updateUserPostsAfterChange()

                    // Show notification
                    NotificationHelper.notify(
                        context,
                        "Post Updated",
                        "Your post has been updated successfully."
                    )

                    // Navigate back
                    navController.popBackStack()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Save Changes")
            }
        }
    }
}