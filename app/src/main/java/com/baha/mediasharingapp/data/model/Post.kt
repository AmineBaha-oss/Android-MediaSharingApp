package com.baha.mediasharingapp.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "posts")
data class Post(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val caption: String,
    val lat: Double,
    val lng: Double,
    val imagePath: String
)