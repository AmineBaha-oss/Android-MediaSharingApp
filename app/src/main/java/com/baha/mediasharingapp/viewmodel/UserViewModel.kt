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
    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    // User's posts
    private val _userPosts = MutableStateFlow<List<Post>>(emptyList())
    val userPosts: StateFlow<List<Post>> = _userPosts.asStateFlow()

    // All posts for feed
    private val _allPosts = MutableStateFlow<List<Post>>(emptyList())
    val allPosts: StateFlow<List<Post>> = _allPosts.asStateFlow()

    // Follower and following counts
    private val _followerCount = MutableStateFlow(0)
    val followerCount: StateFlow<Int> = _followerCount.asStateFlow()

    private val _followingCount = MutableStateFlow(0)
    val followingCount: StateFlow<Int> = _followingCount.asStateFlow()

    // Posts storage
    private val postsList = mutableListOf<Post>()

    // Map of location names
    private val locationNames = mutableMapOf<Pair<Double, Double>, String>()

    // Store registered users
    private val registeredUsers = mutableMapOf<Long, User>()

    init {
        // Add pre-defined users to registered users map
        val predefinedUsers = listOf(
            User(id = 1, username = "john_doe", email = "john@example.com", password = "password", bio = "Photography enthusiast exploring the world one photo at a time."),
            User(id = 2, username = "jane_smith", email = "jane@example.com", password = "password", bio = "Travel blogger and coffee lover. Always looking for the next adventure."),
            User(id = 3, username = "mike_wilson", email = "mike@example.com", password = "password", bio = "Tech geek and outdoor enthusiast. Coding by day, hiking by weekend.")
        )

        predefinedUsers.forEach { user ->
            registeredUsers[user.id] = user
        }

        // Add some sample posts
        val samplePosts = listOf(
            Post(
                id = 1,
                caption = "Beautiful sunset at the beach",
                imagePath = "https://picsum.photos/id/1/800/600",
                lat = 37.7749,
                lng = -122.4194,
                userId = 1,
                locationName = "San Francisco"
            ),
            Post(
                id = 2,
                caption = "Hiking in the mountains",
                imagePath = "https://picsum.photos/id/10/800/600",
                lat = 40.7128,
                lng = -74.0060,
                userId = 2,
                locationName = "New York"
            ),
            Post(
                id = 3,
                caption = "City skyline",
                imagePath = "https://picsum.photos/id/20/800/600",
                lat = 34.0522,
                lng = -118.2437,
                userId = 3,
                locationName = "Los Angeles"
            )
        )

        postsList.addAll(samplePosts)
        _allPosts.value = postsList.toList()

        // Set example follower/following counts
        _followerCount.value = 125
        _followingCount.value = 247
    }

    fun login(usernameOrEmail: String, password: String): Boolean {
        // Check both username and email for all users
        val user = registeredUsers.values.find { user ->
            (user.username == usernameOrEmail || user.email == usernameOrEmail) &&
                    user.password == password
        }

        return if (user != null) {
            _currentUser.value = user
            _isLoggedIn.value = true
            refreshUserPosts()
            true
        } else {
            false
        }
    }

    fun logout() {
        _currentUser.value = null
        _isLoggedIn.value = false
        _userPosts.value = emptyList()
    }

    // New method for user signup
    fun signup(username: String, email: String, password: String): Boolean {
        // Simple signup logic
        if (username.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()) {
            val newUserId = generateNextUserId()
            val newUser = User(
                id = newUserId,
                username = username,
                email = email,
                password = password
            )

            // Store the new user
            registeredUsers[newUserId] = newUser

            _currentUser.value = newUser
            _isLoggedIn.value = true
            refreshUserPosts()
            return true
        }
        return false
    }

    // New method for updating user profile
    fun updateUserProfile(username: String, email: String, bio: String): Boolean {
        val currentUser = _currentUser.value ?: return false
        val updatedUser = currentUser.copy(
            username = username,
            email = email,
            bio = bio
        )

        // Update in registered users map
        registeredUsers[currentUser.id] = updatedUser
        _currentUser.value = updatedUser
        return true
    }

    private fun generateNextUserId(): Long {
        return (registeredUsers.keys.maxOrNull() ?: 0) + 1
    }

    fun getUserById(userId: Long): User? {
        return registeredUsers[userId]
    }

    // Get username by user ID (used for posts)
    fun getUsernameById(userId: Long): String {
        return getUserById(userId)?.username ?: "Unknown User"
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