package com.baha.mediasharingapp.data

import com.baha.mediasharingapp.data.model.Post
import kotlinx.coroutines.flow.Flow

interface PostRepository {
    fun getPosts(): Flow<List<Post>>
    suspend fun addPost(post: Post)
    suspend fun updatePost(post: Post)
    suspend fun deletePost(post: Post)
}

class RoomPostRepository(private val postDao: PostDao) : PostRepository {
    override fun getPosts(): Flow<List<Post>> {
        return postDao.getAllPosts()
    }

    override suspend fun addPost(post: Post) {
        postDao.insertPost(post)
    }

    override suspend fun updatePost(post: Post) {
        postDao.updatePost(post)
    }

    override suspend fun deletePost(post: Post) {
        postDao.deletePost(post)
    }
}