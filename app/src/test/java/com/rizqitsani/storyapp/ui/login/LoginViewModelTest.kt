package com.rizqitsani.storyapp.ui.login

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.rizqitsani.storyapp.DataDummy
import com.rizqitsani.storyapp.MainCoroutineRule
import com.rizqitsani.storyapp.data.Result
import com.rizqitsani.storyapp.data.remote.response.LoginResponse
import com.rizqitsani.storyapp.data.repository.AuthRepository
import com.rizqitsani.storyapp.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class LoginViewModelTest {
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineRules = MainCoroutineRule()

    @Mock
    private lateinit var authRepository: AuthRepository

    private lateinit var loginViewModel: LoginViewModel
    private val dummyUser = DataDummy.generateDummyUser()
    private val email = "test@mail.com"
    private val password = "password"

    @Before
    fun setUp() {
        loginViewModel = LoginViewModel(authRepository)
    }

    @Test
    fun `when Login Should Not Null and Return Success`() {
        val expectedResponse = MutableLiveData<Result<LoginResponse>>()
        expectedResponse.value = Result.Success(LoginResponse(dummyUser, false, "Success."))

        `when`(loginViewModel.login(email, password)).thenReturn(
            expectedResponse
        )
        val actualResponse =
            loginViewModel.login(email, password).getOrAwaitValue()

        Mockito.verify(authRepository).login(email, password)
        Assert.assertNotNull(actualResponse)
        Assert.assertTrue(actualResponse is Result.Success)
        Assert.assertEquals(false, (actualResponse as Result.Success).data.error)
    }

    @Test
    fun `when Network Error Should Return Error`() {
        val expectedResponse = MutableLiveData<Result<LoginResponse>>()
        expectedResponse.value = Result.Error("Error")

        `when`(loginViewModel.login(email, password)).thenReturn(
            expectedResponse
        )
        val actualResponse =
            loginViewModel.login(email, password).getOrAwaitValue()

        Mockito.verify(authRepository).login(email, password)
        Assert.assertNotNull(actualResponse)
        Assert.assertTrue(actualResponse is Result.Error)
    }
}