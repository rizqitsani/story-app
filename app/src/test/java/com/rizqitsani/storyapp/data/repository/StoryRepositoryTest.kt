package com.rizqitsani.storyapp.data.repository

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.rizqitsani.storyapp.DataDummy
import com.rizqitsani.storyapp.MainCoroutineRule
import com.rizqitsani.storyapp.data.FakeApiService
import com.rizqitsani.storyapp.data.Result
import com.rizqitsani.storyapp.data.database.StoryDatabase
import com.rizqitsani.storyapp.data.remote.response.AddStoryResponse
import com.rizqitsani.storyapp.data.remote.retrofit.ApiService
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner
import java.io.File

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class StoryRepositoryTest {
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @Mock
    private lateinit var storyDatabase: StoryDatabase

    private lateinit var apiService: ApiService
    private lateinit var storyRepository: StoryRepository
    private val dummyToken = DataDummy.generateDummyToken()

    private val file = Mockito.mock(File::class.java)
    private val description =
        "Description".toRequestBody("text/plain".toMediaType())
    private val requestImageFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
    private val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
        "photo",
        file.name,
        requestImageFile
    )

    @Before
    fun setUp() {
        apiService = FakeApiService()
        storyRepository = StoryRepository(storyDatabase, apiService)
    }

    @Test
    fun `when Get Stories Should Not Null and Return Success`() =
        mainCoroutineRule.runBlockingTest {
            val expectedStories = DataDummy.generateDummyStoriesResponse()
            val actualStories = apiService.getStoriesWithLocation(dummyToken)
            Assert.assertNotNull(actualStories)
            Assert.assertEquals(expectedStories.size, actualStories.listStory.size)
        }

    @Test
    fun `when Add Story Should Not Null and Error Is False`() = mainCoroutineRule.runBlockingTest {
        val expectedResponse = MutableLiveData<Result<AddStoryResponse>>()
        expectedResponse.value = Result.Success(AddStoryResponse(false, "Success."))

        val actualResponse =
            apiService.uploadImage(dummyToken, imageMultipart, description)

        Assert.assertNotNull(actualResponse)
        Assert.assertEquals(false, actualResponse.error)
    }
}