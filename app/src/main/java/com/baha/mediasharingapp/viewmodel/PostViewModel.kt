package com.baha.mediasharingapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.baha.mediasharingapp.data.PostRepository
import com.baha.mediasharingapp.data.model.Post
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class PostViewModel(
    private val repository: PostRepository
) : ViewModel() {

    // Expose a cold Flow of posts as a hot StateFlow in the VM scope
    val posts: StateFlow<List<Post>> = repository
        .getPosts()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = emptyList()
        )

    // Helper to insert a new post
    fun addPost(post: Post) {
        viewModelScope.launch {
            repository.addPost(post)
        }
    }
}

// Factory to create PostViewModel with our repository
class PostViewModelFactory(
    private val repository: PostRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PostViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PostViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: $modelClass")
    }
}
