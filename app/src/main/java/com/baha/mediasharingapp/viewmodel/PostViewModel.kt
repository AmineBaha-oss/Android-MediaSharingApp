package com.baha.mediasharingapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.baha.mediasharingapp.data.PostRepository
import com.baha.mediasharingapp.data.model.Post
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PostViewModel(
    private val repository: PostRepository,
    private val userViewModel: UserViewModel? = null
) : ViewModel() {

    val posts = repository.getPosts()

    private val _userPosts = MutableStateFlow<List<Post>>(emptyList())
    val userPosts: StateFlow<List<Post>> = _userPosts.asStateFlow()

    private val _currentUser = MutableStateFlow<String?>(null)
    val currentUser: StateFlow<String?> = _currentUser.asStateFlow()

    // Map to store user IDs to usernames for display
    private val userMap = mutableMapOf<Long, String>()

    init {
        // Add some default mappings
        userMap[1L] = "john_doe"
        userMap[2L] = "jane_smith"
        userMap[3L] = "mike_wilson"
    }

    fun getUsernameForPost(userId: Long): String {
        // First check our userViewModel
        val user = userViewModel?.getUserById(userId)
        if (user != null) {
            return user.username
        }

        // Fall back to our local map
        return userMap[userId] ?: "Unknown User"
    }

    fun setCurrentUser(username: String) {
        _currentUser.value = username
        loadUserPosts(username)
    }

    fun addPost(post: Post) {
        viewModelScope.launch {
            repository.addPost(post)
            // Make sure UserViewModel updates its state
            userViewModel?.updateUserPostsAfterChange()
        }
    }

    fun deletePost(post: Post) {
        viewModelScope.launch {
            repository.deletePost(post)
            // Make sure UserViewModel updates its state
            userViewModel?.updateUserPostsAfterChange()
        }
    }

    fun loadUserPosts(username: String) {
        viewModelScope.launch {
            repository.getPosts().collect { allPosts ->
                _userPosts.value = allPosts
            }
        }
    }

    fun logoutUser() {
        _currentUser.value = null
        _userPosts.value = emptyList()
    }

    fun getPostById(postId: Long): Post? {
        // First try to find in the emitted posts
        val allPosts = userViewModel?.getAllPosts() ?: emptyList()
        return allPosts.find { it.id == postId }
    }
}