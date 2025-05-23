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

    private val _cachedPosts = MutableStateFlow<List<Post>>(emptyList())
    val cachedPosts: StateFlow<List<Post>> = _cachedPosts.asStateFlow()

    private val _userPosts = MutableStateFlow<List<Post>>(emptyList())
    val userPosts: StateFlow<List<Post>> = _userPosts.asStateFlow()

    private val _currentUser = MutableStateFlow<String?>(null)
    val currentUser: StateFlow<String?> = _currentUser.asStateFlow()

    private val userMap = mutableMapOf<Long, String>()

    init {
        userMap[1L] = "john_doe"
        userMap[2L] = "jane_smith"
        userMap[3L] = "mike_wilson"

        refreshPosts()
    }

    fun getUsernameForPost(userId: Long): String {
        val user = userViewModel?.getUserById(userId)
        if (user != null) {
            return user.username
        }

        return userMap[userId] ?: "Unknown User"
    }

    fun setCurrentUser(username: String) {
        _currentUser.value = username
        loadUserPosts(username)
    }

    fun addPost(post: Post) {
        viewModelScope.launch {
            if (userViewModel != null) {
                userViewModel.addPost(post)
            } else {
                repository.addPost(post)
            }
            refreshPosts()
        }
    }

    fun updatePost(post: Post) {
        viewModelScope.launch {
            if (userViewModel != null) {
                userViewModel.updatePost(post)
            } else {
                repository.updatePost(post)
            }
            refreshPosts()
        }
    }

    fun deletePost(post: Post) {
        viewModelScope.launch {
            if (userViewModel != null) {
                userViewModel.deletePost(post)
            } else {
                repository.deletePost(post)
            }
            refreshPosts()
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
        val post = _cachedPosts.value.find { it.id == postId }
        if (post != null) {
            return post
        }

        val allPosts = userViewModel?.getAllPosts() ?: emptyList()
        return allPosts.find { it.id == postId }
    }

    fun refreshPosts() {
        viewModelScope.launch {
            repository.getPosts().collect {
                _cachedPosts.value = it
            }
        }
    }
}