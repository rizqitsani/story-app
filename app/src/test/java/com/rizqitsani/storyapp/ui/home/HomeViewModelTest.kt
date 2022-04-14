package com.rizqitsani.storyapp.ui.home

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.rizqitsani.storyapp.DataDummy
import com.rizqitsani.storyapp.MainCoroutineRule
import com.rizqitsani.storyapp.data.Result
import com.rizqitsani.storyapp.data.repository.AuthRepository
import com.rizqitsani.storyapp.data.repository.StoryRepository
import com.rizqitsani.storyapp.domain.model.Story
import com.rizqitsani.storyapp.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runBlockingTest
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
class HomeViewModelTest {
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineRules = MainCoroutineRule()

    @Mock
    private lateinit var authRepository: AuthRepository

    @Mock
    private lateinit var storyRepository: StoryRepository

    private lateinit var homeViewModel: HomeViewModel
    private val dummyStories = DataDummy.generateDummyStories()
    private val dummyToken = DataDummy.generateDummyToken()
    private val dummyUser= DataDummy.generateDummyUser()

    @Before
    fun setUp() {
        `when`(authRepository.user).thenReturn(
            flowOf(dummyUser)
        )
        homeViewModel = HomeViewModel(authRepository, storyRepository)
    }

    @Test
    fun `when Get Stories Should Not Null and Return Success`() {
        val expectedStories = MutableLiveData<Result<List<Story>>>()
        expectedStories.value = Result.Success(dummyStories)

        `when`(homeViewModel.getStories(dummyToken)).thenReturn(expectedStories)
        val actualStories = homeViewModel.getStories(dummyToken).getOrAwaitValue()

        Mockito.verify(storyRepository).getStories("Bearer $dummyToken")
        Assert.assertNotNull(actualStories)
        Assert.assertTrue(actualStories is Result.Success)
        Assert.assertEquals(dummyStories.size, (actualStories as Result.Success).data.size)
    }

    @Test
    fun `when Network Error Should Return Error`() {
        val stories = MutableLiveData<Result<List<Story>>>()
        stories.value = Result.Error("Error")

        `when`(homeViewModel.getStories(dummyToken)).thenReturn(stories)
        val actualStories = homeViewModel.getStories(dummyToken).getOrAwaitValue()

        Mockito.verify(storyRepository).getStories("Bearer $dummyToken")
        Assert.assertNotNull(actualStories)
        Assert.assertTrue(actualStories is Result.Error)
    }

    @Test
    fun `when Logout Should Call logout On AuthRepository`() = mainCoroutineRules.runBlockingTest {
        homeViewModel.logout()
        Mockito.verify(authRepository).logout()
    }
}