package com.baha.mediasharingapp.data

import com.baha.mediasharingapp.data.local.PostDao
import com.baha.mediasharingapp.data.model.Post
import kotlinx.coroutines.flow.Flow

class PostRepository(
    private val dao: PostDao
) {
    fun getPosts(): Flow<List<Post>> = dao.getAllPosts()
    suspend fun addPost(post: Post) = dao.insertPost(post)
}