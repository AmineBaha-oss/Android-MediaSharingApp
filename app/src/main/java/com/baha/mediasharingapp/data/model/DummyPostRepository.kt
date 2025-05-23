package com.baha.mediasharingapp.data.model

import com.baha.mediasharingapp.data.PostRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class DummyPostRepository(
    private val allPosts: MutableList<Post>,
    private val userPostsFlow: MutableStateFlow<List<Post>>,
    private val userId: Long?
) : PostRepository {

    override fun getPosts(): Flow<List<Post>> {
        return userPostsFlow
    }

    override suspend fun addPost(post: Post) {
        val newPost = if (post.id == 0L) {
            post.copy(id = (allPosts.maxOfOrNull { it.id } ?: 0) + 1, userId = userId ?: 0)
        } else {
            val index = allPosts.indexOfFirst { it.id == post.id }
            if (index >= 0) {
                allPosts.removeAt(index)
            }
            post
        }

        allPosts.add(newPost)
        userPostsFlow.value = allPosts.filter { it.userId == userId }
    }

    override suspend fun deletePost(post: Post) {
        allPosts.removeIf { it.id == post.id }
        userPostsFlow.value = allPosts.filter { it.userId == userId }
    }
}