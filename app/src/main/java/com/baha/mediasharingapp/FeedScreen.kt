package com.baha.mediasharingapp

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter
import com.baha.mediasharingapp.data.model.Post

@Composable
fun FeedScreen(
    posts: List<Post>,
    onPostClick: (lat: Double, lng: Double) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier.padding(8.dp)
    ) {
        items(posts) { post ->
            Card(
                modifier = Modifier
                    .padding(4.dp)
                    .clickable { onPostClick(post.lat, post.lng) }
            ) {
                Image(
                    painter = rememberImagePainter(post.imagePath),
                    contentDescription = post.caption,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                )
            }
        }
    }
}