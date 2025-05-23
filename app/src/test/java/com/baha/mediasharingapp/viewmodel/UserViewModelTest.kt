package com.baha.mediasharingapp

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.baha.mediasharingapp.data.model.User
import com.baha.mediasharingapp.viewmodel.UserViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class UserViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = TestCoroutineDispatcher()
    private lateinit var viewModel: UserViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel = UserViewModel()
        addUserForTesting()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        testDispatcher.cleanupTestCoroutines()
    }

    private fun addUserForTesting() {
        viewModel.signup("testuser", "test@example.com", "password")
    }

    private fun getUserByEmail(email: String): User? {
        return viewModel.currentUser.value?.takeIf { it.email == email }
    }

    @Test
    fun loginSuccess() = runTest {

        val result = viewModel.login("test@example.com", "password")

        assertTrue(result)
        assertTrue(viewModel.isLoggedIn.value)
        assertEquals("testuser", viewModel.currentUser.value?.username)
    }

    @Test
    fun loginFailure() = runTest {
        viewModel.signup("testuser", "test@example.com", "correctpassword")

        viewModel.logout()

        val result = viewModel.login("test@example.com", "wrongpassword")

        assertFalse("Login should fail with incorrect password", result)
        assertFalse("User should not be logged in after failed login", viewModel.isLoggedIn.value)
        assertNull("Current user should be null after failed login", viewModel.currentUser.value)
    }

    @Test
    fun signupSuccess() = runTest {
        val result = viewModel.signup("newuser", "new@example.com", "password")

        assertTrue(result)
        val user = getUserByEmail("new@example.com")
        assertEquals("newuser", user?.username)
    }
}