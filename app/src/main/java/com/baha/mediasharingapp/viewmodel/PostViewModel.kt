package com.baha.mediasharingapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.baha.mediasharingapp.data.PostRepository
import com.baha.mediasharingapp.data.model.Post
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PostViewModel(
    private val repository: PostRepository,
    private val userViewModel: UserViewModel // Reference to UserViewModel to get usernames
) : ViewModel() {

    private val _posts = MutableStateFlow<List<Post>>(emptyList())
    val posts: StateFlow<List<Post>> = _posts.asStateFlow()

    init {
        refreshPosts()
    }

    fun refreshPosts() {
        viewModelScope.launch {
            repository.getPosts().collect { newPosts ->
                _posts.value = newPosts
            }
        }
    }

    fun addPost(post: Post) {
        viewModelScope.launch {
            repository.addPost(post)
            refreshPosts()
        }
    }

    fun updatePost(post: Post) {
        viewModelScope.launch {
            repository.updatePost(post)
            refreshPosts()
        }
    }

    fun deletePost(post: Post) {
        viewModelScope.launch {
            repository.deletePost(post)
            refreshPosts()
        }
    }

    // Use UserViewModel to get username for post
    fun getUsernameForPost(userId: Long): String {
        return userViewModel.getUsernameById(userId)
    }
}