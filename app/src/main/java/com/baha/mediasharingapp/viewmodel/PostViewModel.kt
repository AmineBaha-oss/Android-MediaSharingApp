package com.baha.mediasharingapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.baha.mediasharingapp.data.PostRepository
import com.baha.mediasharingapp.data.model.Post
import com.baha.mediasharingapp.data.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PostViewModel(private val repository: PostRepository) : ViewModel() {
    val posts = repository.getPosts()

    private val _userPosts = MutableStateFlow<List<Post>>(emptyList())
    val userPosts: StateFlow<List<Post>> = _userPosts.asStateFlow()

    private val _currentUser = MutableStateFlow<String?>(null)
    val currentUser: StateFlow<String?> = _currentUser.asStateFlow()

    // Map to store user IDs to usernames for display
    private val userMap = mutableMapOf<Long, String>()

    init {
        // Dummy data for users
        userMap[1L] = "john_doe"
        userMap[2L] = "jane_smith"
        userMap[3L] = "mike_wilson"
    }

    fun getUsernameForPost(userId: Long): String {
        return userMap[userId] ?: "Unknown User"
    }

    fun setCurrentUser(username: String) {
        _currentUser.value = username
        loadUserPosts(username)
    }

    fun addPost(post: Post) {
        viewModelScope.launch {
            repository.addPost(post)
        }
    }

    fun deletePost(post: Post) {
        viewModelScope.launch {
            repository.deletePost(post)
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
}