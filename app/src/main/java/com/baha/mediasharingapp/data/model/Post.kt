package com.baha.mediasharingapp.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "posts")
data class Post(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val caption: String = "",
    val imagePath: String = "",
    val lat: Double = 0.0,
    val lng: Double = 0.0,
    val userId: Long = 0,
    val locationName: String = ""
)