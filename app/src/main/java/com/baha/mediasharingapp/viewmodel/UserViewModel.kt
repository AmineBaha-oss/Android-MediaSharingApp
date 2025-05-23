package com.baha.mediasharingapp.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.baha.mediasharingapp.data.model.DummyPostRepository
import com.baha.mediasharingapp.data.model.Post
import com.baha.mediasharingapp.data.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.random.Random

class UserViewModel : ViewModel() {
    // User authentication state
    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn

    // Current user
    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    // User posts
    private val _userPosts = MutableStateFlow<List<Post>>(emptyList())
    val userPosts: StateFlow<List<Post>> = _userPosts.asStateFlow()

    // All posts for feed
    private val _allPosts = MutableStateFlow<List<Post>>(mutableListOf())
    val allPosts: StateFlow<List<Post>> = _allPosts.asStateFlow()

    // Follower and following counts
    private val _followerCount = MutableStateFlow(0)
    val followerCount: StateFlow<Int> = _followerCount.asStateFlow()

    private val _followingCount = MutableStateFlow(0)
    val followingCount: StateFlow<Int> = _followingCount.asStateFlow()

    // Location name cache
    private val locationNameCache = mutableMapOf<Pair<Double, Double>, String>()

    // User database (simulated)
    private val users = mutableListOf<User>()

    // Post storage
    private val userPostsMap = mutableMapOf<Long, MutableList<Post>>()

    // Last assigned user ID
    private var lastUserId = 0L

    // Last assigned post ID
    private var lastPostId = 0L

    init {
        // Add some pre-made users with random follower and following counts
        addUser(
            User(
                id = ++lastUserId,
                username = "john_doe",
                email = "john@example.com",
                password = "password",
                bio = "I love photography and travel",
                followerCount = Random.nextInt(50, 500),
                followingCount = Random.nextInt(20, 300)
            )
        )
        addUser(
            User(
                id = ++lastUserId,
                username = "jane_smith",
                email = "jane@example.com",
                password = "password",
                bio = "Adventure seeker and nature lover",
                followerCount = Random.nextInt(100, 1000),
                followingCount = Random.nextInt(50, 400)
            )
        )
        addUser(
            User(
                id = ++lastUserId,
                username = "mike_wilson",
                email = "mike@example.com",
                password = "password",
                bio = "Professional photographer",
                followerCount = Random.nextInt(200, 2000),
                followingCount = Random.nextInt(150, 500)
            )
        )

        // Add some dummy posts
        addDummyPosts()
    }

    private fun addDummyPosts() {
        // Add some dummy posts for pre-made users
        var postId = 0L

        // Posts for John Doe
        userPostsMap[1L] = mutableListOf(
            Post(
                id = ++postId,
                caption = "Beautiful sunset at the beach",
                imagePath = "https://picsum.photos/id/10/800/800",
                lat = 37.7749,
                lng = -122.4194,
                userId = 1L,
                locationName = "San Francisco"
            ),
            Post(
                id = ++postId,
                caption = "Hiking in the mountains",
                imagePath = "https://picsum.photos/id/29/800/800",
                lat = 36.778259,
                lng = -119.417931,
                userId = 1L,
                locationName = "Sierra Nevada"
            )
        )

        // Posts for Jane Smith
        userPostsMap[2L] = mutableListOf(
            Post(
                id = ++postId,
                caption = "City lights at night",
                imagePath = "https://picsum.photos/id/42/800/800",
                lat = 40.7128,
                lng = -74.0060,
                userId = 2L,
                locationName = "New York City"
            )
        )

        // Posts for Mike Wilson
        userPostsMap[3L] = mutableListOf(
            Post(
                id = ++postId,
                caption = "Wildlife photography",
                imagePath = "https://picsum.photos/id/65/800/800",
                lat = 27.9881,
                lng = 86.9250,
                userId = 3L,
                locationName = "National Park"
            ),
            Post(
                id = ++postId,
                caption = "Architecture study",
                imagePath = "https://picsum.photos/id/72/800/800",
                lat = 48.8566,
                lng = 2.3522,
                userId = 3L,
                locationName = "Paris"
            ),
            Post(
                id = ++postId,
                caption = "Street art",
                imagePath = "https://picsum.photos/id/83/800/800",
                lat = 51.5072,
                lng = 0.1276,
                userId = 3L,
                locationName = "London"
            )
        )

        lastPostId = postId

        // Update all posts
        updateAllPosts()
    }

    private fun updateAllPosts() {
        val allPosts = mutableListOf<Post>()
        userPostsMap.values.forEach { posts ->
            allPosts.addAll(posts)
        }
        _allPosts.value = allPosts.sortedByDescending { it.id }
    }

    fun login(email: String, password: String): Boolean {
        val user = users.find { it.email == email && it.password == password }
        if (user != null) {
            _currentUser.value = user
            _isLoggedIn.value = true
            refreshUserPosts()
            updateFollowerCounts()
            return true
        }
        return false
    }

    fun signup(username: String, email: String, password: String): Boolean {
        if (users.any { it.email == email }) {
            return false
        }

        // New users start with 0 followers and following
        val newUser = User(
            id = ++lastUserId,
            username = username,
            email = email,
            password = password,
            bio = "",
            followerCount = 0,
            followingCount = 0
        )
        addUser(newUser)
        _currentUser.value = newUser
        _isLoggedIn.value = true
        updateFollowerCounts()
        return true
    }

    fun logout() {
        _currentUser.value = null
        _isLoggedIn.value = false
        _userPosts.value = emptyList()
    }

    private fun addUser(user: User) {
        users.add(user)
    }

    fun getUserById(userId: Long): User? {
        return users.find { it.id == userId }
    }

    fun refreshUserPosts() {
        val userId = _currentUser.value?.id ?: return
        _userPosts.value = userPostsMap[userId] ?: emptyList()
    }

    private fun updateFollowerCounts() {
        val user = _currentUser.value ?: return
        _followerCount.value = user.followerCount
        _followingCount.value = user.followingCount
    }

    fun updateUserProfile(username: String, email: String, bio: String): Boolean {
        val currentUser = _currentUser.value ?: return false
        val updatedUser = currentUser.copy(
            username = username,
            email = email,
            bio = bio
        )

        // Update in users list
        val index = users.indexOfFirst { it.id == currentUser.id }
        if (index >= 0) {
            users[index] = updatedUser
            _currentUser.value = updatedUser
            return true
        }
        return false
    }

    fun getAllPosts(): List<Post> {
        return _allPosts.value
    }

    fun addPost(post: Post) {
        val userId = _currentUser.value?.id ?: return
        val userPosts = userPostsMap.getOrPut(userId) { mutableListOf() }

        val postId = if (post.id == 0L) {
            ++lastPostId
        } else {
            post.id
        }

        val newPost = post.copy(id = postId, userId = userId)
        userPosts.add(newPost)
        refreshUserPosts()
        updateAllPosts()
    }

    fun updatePost(post: Post) {
        val userId = post.userId
        val userPosts = userPostsMap[userId] ?: return

        val index = userPosts.indexOfFirst { it.id == post.id }
        if (index >= 0) {
            userPosts[index] = post
            refreshUserPosts()
            updateAllPosts()
        }
    }

    fun deletePost(post: Post) {
        val userId = post.userId
        val userPosts = userPostsMap[userId] ?: return

        if (userPosts.removeIf { it.id == post.id }) {
            refreshUserPosts()
            updateAllPosts()
        }
    }

    fun updateUserPostsAfterChange() {
        refreshUserPosts()
        updateAllPosts()
    }

    fun addLocationName(lat: Double, lng: Double, name: String) {
        locationNameCache[Pair(lat, lng)] = name
    }

    fun getLocationName(lat: Double, lng: Double): String? {
        return locationNameCache[Pair(lat, lng)]
    }
}