package com.baha.mediasharingapp.viewmodel

import androidx.lifecycle.ViewModel
import com.baha.mediasharingapp.data.PostRepository
import com.baha.mediasharingapp.data.model.DummyPostRepository
import com.baha.mediasharingapp.data.model.Post
import com.baha.mediasharingapp.data.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.random.Random

class UserViewModel : ViewModel() {

    // User state management
    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    // User posts
    private val _userPosts = MutableStateFlow<List<Post>>(emptyList())
    val userPosts: StateFlow<List<Post>> = _userPosts.asStateFlow()

    // Social stats
    private val _followerCount = MutableStateFlow(0)
    val followerCount: StateFlow<Int> = _followerCount.asStateFlow()

    private val _followingCount = MutableStateFlow(0)
    val followingCount: StateFlow<Int> = _followingCount.asStateFlow()

    // Location name cache
    private val locationCache = mutableMapOf<Pair<Double, Double>, String>()

    // In-memory data storage
    private val users = mutableListOf<User>()
    private val allPosts = mutableListOf<Post>()

    // Demo images for new users
    private val demoImageUris = listOf(
        "https://picsum.photos/seed/pic1/800/600",
        "https://picsum.photos/seed/pic2/800/600",
        "https://picsum.photos/seed/pic3/800/600",
        "https://picsum.photos/seed/pic4/800/600"
    )

    // Demo locations for new users
    private val demoLocations = listOf(
        Triple("Central Park", 40.785091, -73.968285),
        Triple("Eiffel Tower", 48.858844, 2.294351),
        Triple("Sydney Opera House", -33.856159, 151.215256),
        Triple("Golden Gate Bridge", 37.819722, -122.478611)
    )

    init {
        // Create some demo users
        addDemoUsers()
    }

    private fun addDemoUsers() {
        // Add demo users if none exist
        if (users.isEmpty()) {
            val user1 = User(id = 1, username = "john_doe", email = "john@example.com", password = "password", bio = "Photography enthusiast | Travel lover")
            val user2 = User(id = 2, username = "jane_smith", email = "jane@example.com", password = "password", bio = "Digital artist | Nature lover")
            val user3 = User(id = 3, username = "mike_wilson", email = "mike@example.com", password = "password", bio = "Food blogger | Explorer")

            users.add(user1)
            users.add(user2)
            users.add(user3)

            // Add demo posts for each user
            createDemoPosts(user1)
            createDemoPosts(user2)
            createDemoPosts(user3)
        }
    }

    private fun createDemoPosts(user: User) {
        // Create 2 posts for the user with random content
        repeat(2) { index ->
            val randomImageIndex = Random.nextInt(demoImageUris.size)
            val randomLocationIndex = Random.nextInt(demoLocations.size)
            val location = demoLocations[randomLocationIndex]

            val post = Post(
                id = Random.nextLong(),
                caption = "This is a great ${if (index % 2 == 0) "sunrise" else "sunset"} at ${location.first}!",
                imagePath = demoImageUris[randomImageIndex],
                lat = location.second,
                lng = location.third,
                userId = user.id,
                locationName = location.first
            )

            allPosts.add(post)
        }
    }

    fun login(email: String, password: String): Boolean {
        val user = users.find { it.email == email && it.password == password }

        if (user != null) {
            _currentUser.value = user
            _isLoggedIn.value = true

            // Set follower and following counts for existing users
            _followerCount.value = Random.nextInt(50, 500)
            _followingCount.value = Random.nextInt(50, 300)

            refreshUserPosts()
            return true
        }

        return false
    }

    fun signup(username: String, email: String, password: String): Boolean {
        // Check if email already exists
        if (users.any { it.email == email }) {
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

        // Log in the new user
        _currentUser.value = newUser
        _isLoggedIn.value = true

        // New users start with 0 followers and following
        _followerCount.value = 0
        _followingCount.value = 0

        refreshUserPosts()
        return true
    }

    fun logout() {
        _currentUser.value = null
        _isLoggedIn.value = false
        _userPosts.value = emptyList()
        _followerCount.value = 0
        _followingCount.value = 0
    }

    fun updateUserProfile(username: String, email: String, bio: String): Boolean {
        val user = _currentUser.value ?: return false

        val updatedUser = user.copy(
            username = username,
            email = email,
            bio = bio
        )

        // Update in our local storage
        val index = users.indexOfFirst { it.id == user.id }
        if (index >= 0) {
            users[index] = updatedUser
            _currentUser.value = updatedUser
            return true
        }

        return false
    }

    fun getUserById(userId: Long): User? {
        return users.find { it.id == userId }
    }

    fun refreshUserPosts() {
        val user = _currentUser.value ?: return
        _userPosts.value = allPosts.filter { it.userId == user.id }
    }

    fun updateUserPostsAfterChange() {
        refreshUserPosts()
    }

    fun addLocationName(lat: Double, lng: Double, name: String) {
        locationCache[Pair(lat, lng)] = name
    }

    fun getLocationName(lat: Double, lng: Double): String {
        return locationCache[Pair(lat, lng)] ?: ""
    }

    fun getAllPosts(): List<Post> {
        return allPosts
    }

    fun getPostViewModel(): PostViewModel {
        val userPostsFlow = MutableStateFlow<List<Post>>(emptyList())
        val userId = _currentUser.value?.id

        val repository = DummyPostRepository(allPosts, userPostsFlow, userId)
        return PostViewModel(repository, this)
    }
}