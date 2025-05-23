package com.baha.mediasharingapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.baha.mediasharingapp.data.PostRepository
import com.baha.mediasharingapp.data.model.DummyPostRepository
import com.baha.mediasharingapp.data.model.Post
import com.baha.mediasharingapp.data.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class UserViewModel : ViewModel() {

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser

    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn

    // All users in the system
    private val users = mutableListOf(
        User(1, "john_doe", "john@example.com", "password123", "Photography enthusiast"),
        User(2, "jane_smith", "jane@example.com", "password123", "Travel lover"),
        User(3, "mike_wilson", "mike@example.com", "password123", "Food blogger")
    )

    // Location name cache
    private val locationNames = mutableMapOf<Pair<Double, Double>, String>()

    // Post repository setup
    private val allPosts = mutableListOf<Post>()
    private val _userPosts = MutableStateFlow<List<Post>>(emptyList())
    val userPosts: StateFlow<List<Post>> = _userPosts

    private var _postRepository: PostRepository? = null

    init {
        // Add sample posts
        addSamplePosts()

        // Set initial location names
        locationNames[Pair(40.7128, -74.0060)] = "New York City"
        locationNames[Pair(34.0522, -118.2437)] = "Los Angeles"
        locationNames[Pair(51.5074, -0.1278)] = "London"
        locationNames[Pair(48.8566, 2.3522)] = "Paris"
        locationNames[Pair(35.6762, 139.6503)] = "Tokyo"
    }

    private fun addSamplePosts() {
        // Add sample posts for different users
        allPosts.addAll(listOf(
            Post(1, "Beautiful sunset today!", "https://picsum.photos/id/100/800/600", 40.7128, -74.0060, 1, "New York City"),
            Post(2, "Delicious lunch at this café", "https://picsum.photos/id/102/800/600", 34.0522, -118.2437, 2, "Los Angeles"),
            Post(3, "Amazing architecture", "https://picsum.photos/id/103/800/600", 51.5074, -0.1278, 3, "London"),
            Post(4, "Morning walk in the park", "https://picsum.photos/id/104/800/600", 48.8566, 2.3522, 1, "Paris"),
            Post(5, "City lights at night", "https://picsum.photos/id/106/800/600", 35.6762, 139.6503, 2, "Tokyo")
        ))
    }

    fun getPostViewModel(): PostViewModel {
        val userId = _currentUser.value?.id
        val repository = DummyPostRepository(allPosts, _userPosts, userId)
        _postRepository = repository
        return PostViewModel(repository)
    }

    fun login(email: String, password: String): Boolean {
        val user = users.find { it.email == email && it.password == password }
        if (user != null) {
            _currentUser.value = user
            _isLoggedIn.value = true

            // Update user posts
            updateUserPosts()
            return true
        }
        return false
    }

    fun signup(username: String, email: String, password: String): Boolean {
        // Check if username or email already exists
        if (users.any { it.username == username || it.email == email }) {
            return false
        }

        // Create new user
        val newUser = User(
            id = (users.maxOfOrNull { it.id } ?: 0) + 1,
            username = username,
            email = email,
            password = password
        )

        users.add(newUser)
        _currentUser.value = newUser
        _isLoggedIn.value = true

        // Update user posts (will be empty for new user)
        updateUserPosts()
        return true
    }

    fun logout() {
        _currentUser.value = null
        _isLoggedIn.value = false
        _userPosts.value = emptyList()
    }

    fun updateUserProfile(username: String, email: String, bio: String): Boolean {
        val currentUser = _currentUser.value ?: return false

        // Check if username or email already taken by another user
        if (users.any { it.id != currentUser.id && (it.username == username || it.email == email) }) {
            return false
        }

        // Update user
        val updatedUser = currentUser.copy(username = username, email = email, bio = bio)
        _currentUser.value = updatedUser

        // Update in users list
        val index = users.indexOfFirst { it.id == currentUser.id }
        if (index >= 0) {
            users[index] = updatedUser
        }

        return true
    }

    private fun updateUserPosts() {
        val userId = _currentUser.value?.id
        if (userId != null) {
            _userPosts.value = allPosts.filter { it.userId == userId }
        }
    }

    fun addLocationName(lat: Double, lng: Double, name: String) {
        locationNames[Pair(lat, lng)] = name
    }

    fun getLocationName(lat: Double, lng: Double): String {
        return locationNames[Pair(lat, lng)] ?: "Unknown Location"
    }

    fun getAllPosts(): List<Post> {
        return allPosts
    }
}