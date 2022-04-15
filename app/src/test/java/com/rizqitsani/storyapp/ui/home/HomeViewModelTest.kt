package com.rizqitsani.storyapp.ui.home

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.recyclerview.widget.ListUpdateCallback
import com.rizqitsani.storyapp.DataDummy
import com.rizqitsani.storyapp.MainCoroutineRule
import com.rizqitsani.storyapp.data.Result
import com.rizqitsani.storyapp.data.repository.AuthRepository
import com.rizqitsani.storyapp.data.repository.StoryRepository
import com.rizqitsani.storyapp.domain.model.Story
import com.rizqitsani.storyapp.getOrAwaitValue
import com.rizqitsani.storyapp.ui.home.adapter.ListStoryAdapter
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

    @Mock
    private lateinit var homeViewModel: HomeViewModel

    private val dummyStories = DataDummy.generateDummyStories()
    private val dummyToken = DataDummy.generateDummyToken()
    private val dummyUser = DataDummy.generateDummyUser()

    @Before
    fun setUp() {
        `when`(authRepository.user).thenReturn(
            flowOf(dummyUser)
        )
        homeViewModel = HomeViewModel(authRepository, storyRepository)
    }

    @Test
    fun `when Get Paged Stories Should Not Null`() = mainCoroutineRules.runBlockingTest {
        val dummyStories = DataDummy.generateDummyStories()
        val data = PagedTestDataSources.snapshot(dummyStories)
        val quote = MutableLiveData<PagingData<Story>>()
        quote.value = data

        `when`(homeViewModel.getPagedStories(dummyToken)).thenReturn(quote)
        val actualStories = homeViewModel.getPagedStories(dummyToken).getOrAwaitValue()

        val differ = AsyncPagingDataDiffer(
            diffCallback = ListStoryAdapter.DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback,
            mainDispatcher = mainCoroutineRules.dispatcher,
            workerDispatcher = mainCoroutineRules.dispatcher,
        )
        differ.submitData(actualStories)

        advanceUntilIdle()

        Mockito.verify(storyRepository).getPagedStories("Bearer $dummyToken")
        Assert.assertNotNull(differ.snapshot())
        Assert.assertEquals(dummyStories.size, differ.snapshot().size)
        Assert.assertEquals(dummyStories[0].description, differ.snapshot()[0]?.description)
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

class PagedTestDataSources private constructor() :
    PagingSource<Int, LiveData<List<Story>>>() {
    companion object {
        fun snapshot(items: List<Story>): PagingData<Story> {
            return PagingData.from(items)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, LiveData<List<Story>>>): Int {
        return 0
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, LiveData<List<Story>>> {
        return LoadResult.Page(emptyList(), 0, 1)
    }
}

val noopListUpdateCallback = object : ListUpdateCallback {
    override fun onInserted(position: Int, count: Int) {}
    override fun onRemoved(position: Int, count: Int) {}
    override fun onMoved(fromPosition: Int, toPosition: Int) {}
    override fun onChanged(position: Int, count: Int, payload: Any?) {}
}