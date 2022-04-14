package com.rizqitsani.storyapp.data.repository

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.rizqitsani.storyapp.DataDummy
import com.rizqitsani.storyapp.MainCoroutineRule
import com.rizqitsani.storyapp.data.FakeApiService
import com.rizqitsani.storyapp.data.preferences.AuthPreferences
import com.rizqitsani.storyapp.data.remote.retrofit.ApiService
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class AuthRepositoryTest {
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @Mock
    private lateinit var authPreferences: AuthPreferences

    private lateinit var apiService: ApiService
    private lateinit var authRepository: AuthRepository
    private val dummyUser = DataDummy.generateDummyUser()
    private val name = "User"
    private val email = "test@mail.com"
    private val password = "password"

    @Before
    fun setUp() {
        `when`(authPreferences.getCurrentUser()).thenReturn(
            flowOf(dummyUser)
        )
        apiService = FakeApiService()
        authRepository = AuthRepository(authPreferences, apiService)
    }

    @Test
    fun `when Login Should Not Null and Error Is False`() =
        mainCoroutineRule.runBlockingTest {
            val expectedResponse = false
            val actualResponse = apiService.login(email, password)
            Assert.assertNotNull(actualResponse)
            Assert.assertEquals(expectedResponse, actualResponse.error)
        }

    @Test
    fun `when Signup Should Not Null and Error Is False`() =
        mainCoroutineRule.runBlockingTest {
            val expectedResponse = false
            val actualResponse = apiService.signup(name, email, password)
            Assert.assertNotNull(actualResponse)
            Assert.assertEquals(expectedResponse, actualResponse.error)
        }

    @Test
    fun `when Get User Should Not Null`() = mainCoroutineRule.runBlockingTest {
        val actualResponse =
            authRepository.user.first()

        Assert.assertNotNull(actualResponse)
        Assert.assertEquals(dummyUser, actualResponse)
    }
}