package com.baha.mediasharingapp.data.model

data class User(
    val id: Long = 0,
    val username: String,
    val email: String,
    val password: String,
    val bio: String = "",
    val followerCount: Int = 0,
    val followingCount: Int = 0
)