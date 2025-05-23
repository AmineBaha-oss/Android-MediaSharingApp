package com.baha.mediasharingapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.baha.mediasharingapp.data.model.Post
import com.baha.mediasharingapp.data.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class UserViewModel : ViewModel() {
    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    private val _userPosts = MutableStateFlow<List<Post>>(emptyList())
    val userPosts: StateFlow<List<Post>> = _userPosts.asStateFlow()

    private val _allPosts = MutableStateFlow<List<Post>>(emptyList())
    val allPosts: StateFlow<List<Post>> = _allPosts.asStateFlow()

    private val _followerCount = MutableStateFlow(0)
    val followerCount: StateFlow<Int> = _followerCount.asStateFlow()

    private val _followingCount = MutableStateFlow(0)
    val followingCount: StateFlow<Int> = _followingCount.asStateFlow()

    private val postsList = mutableListOf<Post>()
    private val locationNames = mutableMapOf<Pair<Double, Double>, String>()

    init {
        // Load dummy data for demo purposes
        loadDummyData()
    }

    private fun loadDummyData() {
        // Create some dummy posts
        val dummyPosts = listOf(
            Post(id = 1, caption = "Beautiful sunset", imagePath = "https://picsum.photos/id/10/800/600", lat = 34.0522, lng = -118.2437, userId = 1, locationName = "Los Angeles"),
            Post(id = 2, caption = "City skyline", imagePath = "https://picsum.photos/id/20/800/600", lat = 40.7128, lng = -74.0060, userId = 2, locationName = "New York"),
            Post(id = 3, caption = "Mountain view", imagePath = "https://picsum.photos/id/30/800/600", lat = 39.7392, lng = -104.9903, userId = 3, locationName = "Denver")
        )

        // Update internal list and state flows
        postsList.addAll(dummyPosts)
        _allPosts.value = postsList.toList()
        refreshUserPosts()

        // Set follower counts
        _followerCount.value = 125
        _followingCount.value = 87
    }

    // User management functions
    fun login(username: String, password: String): Boolean {
        // Simple login logic (would be replaced with actual authentication)
        if (username.isNotEmpty() && password.isNotEmpty()) {
            _currentUser.value = User(
                id = 1,
                username = username,
                email = "$username@example.com",
                password = password
            )
            _isLoggedIn.value = true
            refreshUserPosts()
            return true
        }
        return false
    }

    fun logout() {
        _currentUser.value = null
        _userPosts.value = emptyList()
        _isLoggedIn.value = false
    }

    // New method for user signup
    fun signup(username: String, email: String, password: String): Boolean {
        // Simple signup logic (would be replaced with actual authentication)
        if (username.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()) {
            _currentUser.value = User(
                id = generateNextUserId(),
                username = username,
                email = email,
                password = password
            )
            _isLoggedIn.value = true
            refreshUserPosts()
            return true
        }
        return false
    }

    // New method for updating user profile
    fun updateUserProfile(username: String, email: String, bio: String): Boolean {
        val currentUser = _currentUser.value ?: return false
        _currentUser.value = currentUser.copy(
            username = username,
            email = email,
            bio = bio
        )
        return true
    }

    private fun generateNextUserId(): Long {
        // In a real app, this would be handled by the backend
        // For this example, we'll just use a simple incrementing ID
        return 4L // Assuming we already have users with ID 1, 2, and 3
    }

    fun getUserById(userId: Long): User? {
        // In a real app, this would query a database
        return when (userId) {
            1L -> User(id = 1, username = "john_doe", email = "john@example.com", password = "password")
            2L -> User(id = 2, username = "jane_smith", email = "jane@example.com", password = "password")
            3L -> User(id = 3, username = "mike_wilson", email = "mike@example.com", password = "password")
            else -> null
        }
    }

    // Post management functions
    fun getAllPosts(): List<Post> {
        return postsList.toList()
    }

    fun refreshUserPosts() {
        val userId = _currentUser.value?.id ?: return
        _userPosts.value = postsList.filter { it.userId == userId }
    }

    fun updateUserPostsAfterChange() {
        // Refresh both all posts and user-specific posts
        _allPosts.value = postsList.toList()
        refreshUserPosts()
    }

    fun addPost(post: Post) {
        viewModelScope.launch {
            // Add to internal list
            postsList.add(post)
            updateUserPostsAfterChange()
        }
    }

    fun updatePost(post: Post) {
        viewModelScope.launch {
            val index = postsList.indexOfFirst { it.id == post.id }
            if (index >= 0) {
                postsList[index] = post
                updateUserPostsAfterChange()
            }
        }
    }

    fun deletePost(post: Post) {
        viewModelScope.launch {
            postsList.removeIf { it.id == post.id }
            updateUserPostsAfterChange()
        }
    }

    // Location management
    fun addLocationName(lat: Double, lng: Double, name: String) {
        locationNames[Pair(lat, lng)] = name
    }

    fun getLocationName(lat: Double, lng: Double): String {
        return locationNames[Pair(lat, lng)] ?: "Unknown Location"
    }
}