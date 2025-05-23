package com.baha.mediasharingapp

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.baha.mediasharingapp.data.model.Post
import com.baha.mediasharingapp.viewmodel.UserViewModel
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

@Composable
fun MapScreen(
    userViewModel: UserViewModel,
    posts: List<Post>,
    focusPostId: Long? = null
) {
    // Find the post to focus on, if any
    val focusPost = focusPostId?.let { id -> posts.find { it.id == id } }

    // Determine starting point (focused post or center of all posts)
    val startLatLng = if (focusPost != null) {
        LatLng(focusPost.lat, focusPost.lng)
    } else {
        // Default to center of posts or a fallback location
        val validPosts = posts.filter { it.lat != 0.0 && it.lng != 0.0 }
        if (validPosts.isNotEmpty()) {
            val avgLat = validPosts.map { it.lat }.average()
            val avgLng = validPosts.map { it.lng }.average()
            LatLng(avgLat, avgLng)
        } else {
            LatLng(0.0, 0.0) // Default fallback
        }
    }

    // Configure initial camera position
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(startLatLng, if (focusPost != null) 15f else 2f)
    }

    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState
    ) {
        // Add markers for all posts with valid location data
        posts.filter { it.lat != 0.0 && it.lng != 0.0 }.forEach { post ->
            val postLatLng = LatLng(post.lat, post.lng)
            val isFocused = post.id == focusPostId

            Marker(
                state = MarkerState(position = postLatLng),
                title = post.caption.take(30) + if (post.caption.length > 30) "..." else "",
                snippet = post.locationName,
                icon = if (isFocused) {
                    BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)
                } else {
                    BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)
                }
            )
        }
    }
}