package com.baha.mediasharingapp.viewmodel

import androidx.lifecycle.ViewModel
import com.baha.mediasharingapp.data.model.DummyPostRepository
import com.baha.mediasharingapp.data.model.Post
import com.baha.mediasharingapp.data.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class UserViewModel : ViewModel() {
    private val users = mutableListOf<User>()
    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser

    private val allPosts = mutableListOf<Post>()
    private val _userPosts = MutableStateFlow<List<Post>>(emptyList())
    val userPosts: StateFlow<List<Post>> = _userPosts

    // Map to store location names
    private val locationNames = mutableMapOf<Pair<Double, Double>, String>()

    init {
        // Add users with bios
        users.add(User(1, "DemoUser", "demo@example.com", "password", "Photography enthusiast exploring urban landscapes"))
        users.add(User(2, "beaster", "mirf@gmail.com", "mirf123", "Travel blogger sharing adventures from around the world"))
        users.add(User(3, "testuser", "test@example.com", "test123", "Nature lover documenting wildlife and scenic views"))
        users.add(User(4, "photogeek", "photo@example.com", "photo123", "Professional photographer specializing in portraits"))
        users.add(User(5, "traveler", "travel@example.com", "travel123", "Backpacker sharing global experiences"))

        // Add location names
        locationNames[Pair(43.6532, -79.3832)] = "Toronto, Canada"
        locationNames[Pair(40.7128, -74.0060)] = "New York City, USA"
        locationNames[Pair(51.1784, -115.5708)] = "Banff, Canada"
        locationNames[Pair(34.0522, -118.2437)] = "Los Angeles, USA"
        locationNames[Pair(48.8566, 2.3522)] = "Paris, France"
        locationNames[Pair(35.6762, 139.6503)] = "Tokyo, Japan"
        locationNames[Pair(25.2048, 55.2708)] = "Dubai, UAE"
        locationNames[Pair(-33.8688, 151.2093)] = "Sydney, Australia"

        // Add sample posts with locations
        allPosts.add(
            Post(
                id = 1,
                caption = "Beautiful sunset view over the city skyline",
                lat = 43.6532,
                lng = -79.3832,
                imagePath = "https://images.unsplash.com/photo-1506744038136-46273834b3fb",
                userId = 1,
                locationName = "Toronto, Canada"
            )
        )

        allPosts.add(
            Post(
                id = 2,
                caption = "City skyline at night - amazing lights!",
                lat = 40.7128,
                lng = -74.0060,
                imagePath = "https://images.unsplash.com/photo-1519501025264-65ba15a82390",
                userId = 2,
                locationName = "New York City, USA"
            )
        )

        allPosts.add(
            Post(
                id = 3,
                caption = "Mountain adventures in the Canadian Rockies",
                lat = 51.1784,
                lng = -115.5708,
                imagePath = "https://images.unsplash.com/photo-1464822759023-fed622ff2c3b",
                userId = 3,
                locationName = "Banff, Canada"
            )
        )

        allPosts.add(
            Post(
                id = 4,
                caption = "Hollywood dreams and palm trees",
                lat = 34.0522,
                lng = -118.2437,
                imagePath = "https://images.unsplash.com/photo-1515896769750-31548aa180ed",
                userId = 4,
                locationName = "Los Angeles, USA"
            )
        )

        allPosts.add(
            Post(
                id = 5,
                caption = "Eiffel Tower glowing at sunset",
                lat = 48.8566,
                lng = 2.3522,
                imagePath = "https://images.unsplash.com/photo-1502602898657-3e91760cbb34",
                userId = 5,
                locationName = "Paris, France"
            )
        )

        allPosts.add(
            Post(
                id = 6,
                caption = "Urban life in Tokyo",
                lat = 35.6762,
                lng = 139.6503,
                imagePath = "https://images.unsplash.com/photo-1503899036084-c55cdd92da26",
                userId = 2,
                locationName = "Tokyo, Japan"
            )
        )

        allPosts.add(
            Post(
                id = 7,
                caption = "Desert adventures and modern architecture",
                lat = 25.2048,
                lng = 55.2708,
                imagePath = "https://images.unsplash.com/photo-1496568816309-51d7c20e3b21",
                userId = 3,
                locationName = "Dubai, UAE"
            )
        )

        allPosts.add(
            Post(
                id = 8,
                caption = "Sydney Harbor on a clear day",
                lat = -33.8688,
                lng = 151.2093,
                imagePath = "https://images.unsplash.com/photo-1506973035872-a4ec16b8e8d9",
                userId = 1,
                locationName = "Sydney, Australia"
            )
        )
    }

    fun register(username: String, email: String, password: String, bio: String = ""): Boolean {
        if (users.any { it.email == email }) {
            return false
        }

        val newId = (users.maxOfOrNull { it.id } ?: 0) + 1
        val newUser = User(newId, username, email, password, bio)
        users.add(newUser)
        _currentUser.value = newUser
        _userPosts.value = emptyList()
        return true
    }

    fun login(email: String, password: String): Boolean {
        val user = users.find { it.email == email && it.password == password }
        if (user != null) {
            _currentUser.value = user
            _userPosts.value = allPosts.filter { it.userId == user.id }
            return true
        }
        return false
    }

    fun logout() {
        _currentUser.value = null
        _userPosts.value = emptyList()
    }

    fun getAllPosts(): List<Post> = allPosts

    fun getUserPosts(): List<Post> {
        return allPosts.filter { it.userId == _currentUser.value?.id }
    }

    fun updateUserProfile(username: String, email: String, bio: String): Boolean {
        _currentUser.value?.let { currentUser ->
            val updatedUser = currentUser.copy(username = username, email = email, bio = bio)
            val index = users.indexOfFirst { it.id == currentUser.id }
            if (index != -1) {
                users[index] = updatedUser
                _currentUser.value = updatedUser
                return true
            }
        }
        return false
    }

    fun getLocationName(lat: Double, lng: Double): String {
        return locationNames[Pair(lat, lng)] ?: "Unknown Location"
    }

    fun addLocationName(lat: Double, lng: Double, name: String) {
        locationNames[Pair(lat, lng)] = name
    }

    fun getPostViewModel(): PostViewModel {
        val repository = DummyPostRepository(allPosts, _userPosts, _currentUser.value?.id)
        return PostViewModel(repository)
    }

    fun getUserById(userId: Long): User? {
        return users.find { it.id == userId }
    }
}