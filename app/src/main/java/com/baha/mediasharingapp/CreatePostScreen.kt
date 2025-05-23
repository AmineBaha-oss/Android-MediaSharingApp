package com.baha.mediasharingapp

import android.app.Activity
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddLocation
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Link
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.baha.mediasharingapp.data.model.Post
import com.baha.mediasharingapp.viewmodel.PostViewModel
import com.baha.mediasharingapp.viewmodel.UserViewModel
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import java.io.File
import kotlin.random.Random

const val PLACEHOLDER_IMAGE_URL = "https://via.placeholder.com/400x300/6200EE/FFFFFF?text=Image+Placeholder"
const val DEFAULT_LINKED_IMAGE = "https://picsum.photos/800/600"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatePostScreen(
    navController: NavController,
    viewModel: PostViewModel,
    userViewModel: UserViewModel
) {
    val context = LocalContext.current
    var caption by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var locationName by remember { mutableStateOf("") }
    var lat by remember { mutableStateOf(0.0) }
    var lng by remember { mutableStateOf(0.0) }
    var photoFile by remember { mutableStateOf<File?>(null) }
    var showLinkDialog by remember { mutableStateOf(false) }
    var imageLink by remember { mutableStateOf("") }

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

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            imageUri = it
        }
    }

    val takePictureLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            photoFile?.let {
                imageUri = Uri.fromFile(it)
            }
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

                        userViewModel.addLocationName(lat, lng, locationName)
                    }
                } catch (e: Exception) {
                    Toast.makeText(context, "Error processing location: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    if (showLinkDialog) {
        AlertDialog(
            onDismissRequest = { showLinkDialog = false },
            title = { Text("Enter Image URL") },
            text = {
                OutlinedTextField(
                    value = imageLink,
                    onValueChange = { imageLink = it },
                    label = { Text("Image URL") },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("e.g., https://example.com/image.jpg") }
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (imageLink.isNotBlank()) {
                            imageUri = Uri.parse(imageLink)
                        } else {
                            imageUri = Uri.parse(DEFAULT_LINKED_IMAGE)
                        }
                        showLinkDialog = false
                    }
                ) {
                    Text("Use Image")
                }
            },
            dismissButton = {
                TextButton(onClick = { showLinkDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Create Post", color = MaterialTheme.colorScheme.onPrimary) },
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
            if (imageUri != null) {
                Image(
                    painter = rememberAsyncImagePainter(imageUri),
                    contentDescription = "Selected image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No image selected")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = caption,
                onValueChange = { caption = it },
                label = { Text("Caption") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { imagePickerLauncher.launch("image/*") },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.Image,
                        contentDescription = "Gallery"
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Gallery")
                }

                Button(
                    onClick = {
                        try {
                            val tempFile = File.createTempFile(
                                "JPEG_${System.currentTimeMillis()}_",
                                ".jpg",
                                context.cacheDir
                            )

                            photoFile = tempFile
                            val uri = FileProvider.getUriForFile(
                                context,
                                "${context.packageName}.provider",
                                tempFile
                            )

                            takePictureLauncher.launch(uri)
                        } catch (e: Exception) {
                            Toast.makeText(context, "Camera error: ${e.message}", Toast.LENGTH_SHORT).show()
                            imageUri = Uri.parse(PLACEHOLDER_IMAGE_URL)
                        }
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.CameraAlt,
                        contentDescription = "Camera"
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Cam", style = MaterialTheme.typography.bodySmall)
                }

                Button(
                    onClick = {
                        showLinkDialog = true
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.Link,
                        contentDescription = "Link Image"
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Link")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Location button with safeguards
            Button(
                onClick = {
                    try {
                        if (!com.google.android.libraries.places.api.Places.isInitialized()) {
                            Toast.makeText(context, "Places API not initialized. Using default location.", Toast.LENGTH_SHORT).show()
                            locationName = "Default Location"
                            lat = 37.7749
                            lng = -122.4194
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
                        // Use default location as fallback
                        locationName = "Default Location"
                        lat = 37.7749
                        lng = -122.4194
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.AddLocation,
                    contentDescription = "Add Location"
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(if (locationName.isNotEmpty()) locationName else "Add Location")
            }

            // Display selected location if available
            if (locationName.isNotEmpty()) {
                Text(
                    text = locationName,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    if (caption.isBlank()) {
                        Toast.makeText(context, "Please add a caption", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    if (imageUri == null) {
                        Toast.makeText(context, "Please select an image", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    // For demo purposes, we'll use the URI string directly
                    val imagePath = imageUri.toString()

                    val currentUser = userViewModel.currentUser.value
                    val userId = currentUser?.id ?: 0L

                    // Create a post with location
                    val newPost = Post(
                        id = Random.nextLong(),
                        caption = caption,
                        imagePath = imagePath,
                        lat = lat,
                        lng = lng,
                        userId = userId,
                        locationName = locationName
                    )

                    viewModel.addPost(newPost)
                    viewModel.refreshPosts() // Explicitly refresh posts
                    userViewModel.updateUserPostsAfterChange() // Update user posts

                    NotificationHelper.notify(
                        context,
                        "Post Created",
                        "Your post has been created successfully."
                    )

                    // Navigate back
                    navController.popBackStack()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Create Post")
            }
        }
    }
}