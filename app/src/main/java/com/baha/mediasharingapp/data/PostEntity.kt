package com.baha.mediasharingapp.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "posts")
data class PostEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val username: String,
    val imageUri: String,
    val caption: String,
    val latitude: Double,
    val longitude: Double
) {
}
