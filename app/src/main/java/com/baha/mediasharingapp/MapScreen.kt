package com.baha.mediasharingapp

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.baha.mediasharingapp.data.model.Post
import com.baha.mediasharingapp.viewmodel.UserViewModel
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*

@Composable
fun MapScreen(
    userViewModel: UserViewModel,
    posts: List<Post>
) {
    val firstPostWithLocation = posts.firstOrNull { it.lat != 0.0 && it.lng != 0.0 }
    val initialPosition = if (firstPostWithLocation != null) {
        LatLng(firstPostWithLocation.lat, firstPostWithLocation.lng)
    } else {
        LatLng(40.7128, -74.0060)
    }

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(initialPosition, 10f)
    }

    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState,
        uiSettings = MapUiSettings(zoomControlsEnabled = true)
    ) {
        posts.forEach { post ->
            if (post.lat != 0.0 && post.lng != 0.0) {
                val position = LatLng(post.lat, post.lng)
                Marker(
                    state = MarkerState(position = position),
                    title = post.caption,
                    snippet = post.locationName
                )
            }
        }
    }
}