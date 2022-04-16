package com.rizqitsani.storyapp.ui.signup

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.rizqitsani.storyapp.MainCoroutineRule
import com.rizqitsani.storyapp.data.Result
import com.rizqitsani.storyapp.data.remote.response.SignupResponse
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
class SignupViewModelTest {
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineRules = MainCoroutineRule()

    @Mock
    private lateinit var authRepository: AuthRepository

    private lateinit var signupViewModel: SignupViewModel
    private val name = "User"
    private val email = "test@mail.com"
    private val password = "password"

    @Before
    fun setUp() {
        signupViewModel = SignupViewModel(authRepository)
    }

    @Test
    fun `when Signup Should Not Null and Return Success`() {
        val expectedResponse = MutableLiveData<Result<SignupResponse>>()
        expectedResponse.value = Result.Success(SignupResponse(false, "Success."))

        `when`(signupViewModel.signup(name, email, password)).thenReturn(
            expectedResponse
        )
        val actualResponse =
            signupViewModel.signup(name, email, password).getOrAwaitValue()

        Mockito.verify(authRepository).signup(name, email, password)
        Assert.assertNotNull(actualResponse)
        Assert.assertTrue(actualResponse is Result.Success)
        Assert.assertEquals(false, (actualResponse as Result.Success).data.error)
    }

    @Test
    fun `when Network Error Should Return Error`() {
        val expectedResponse = MutableLiveData<Result<SignupResponse>>()
        expectedResponse.value = Result.Error("Error")

        `when`(signupViewModel.signup(name, email, password)).thenReturn(
            expectedResponse
        )
        val actualResponse =
            signupViewModel.signup(name, email, password).getOrAwaitValue()

        Mockito.verify(authRepository).signup(name, email, password)
        Assert.assertNotNull(actualResponse)
        Assert.assertTrue(actualResponse is Result.Error)
    }
}