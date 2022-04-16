package com.rizqitsani.storyapp.ui.addstory

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.rizqitsani.storyapp.DataDummy
import com.rizqitsani.storyapp.MainCoroutineRule
import com.rizqitsani.storyapp.data.Result
import com.rizqitsani.storyapp.data.remote.response.AddStoryResponse
import com.rizqitsani.storyapp.data.repository.AuthRepository
import com.rizqitsani.storyapp.data.repository.StoryRepository
import com.rizqitsani.storyapp.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
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
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner
import java.io.File

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class AddStoryViewModelTest {
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineRules = MainCoroutineRule()

    @Mock
    private lateinit var authRepository: AuthRepository

    @Mock
    private lateinit var storyRepository: StoryRepository

    private lateinit var addStoryViewModel: AddStoryViewModel
    private val dummyToken = DataDummy.generateDummyToken()
    private val dummyUser = DataDummy.generateDummyUser()

    private val file = Mockito.mock(File::class.java)
    private val description =
        "Description".toRequestBody("text/plain".toMediaType())
    private val lat = (0.0).toString().toRequestBody("text/plain".toMediaType())
    private val lon = (0.0).toString().toRequestBody("text/plain".toMediaType())
    private val requestImageFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
    private val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
        "photo",
        file.name,
        requestImageFile
    )

    @Before
    fun setUp() {
        `when`(authRepository.user).thenReturn(
            flowOf(dummyUser)
        )
        addStoryViewModel = AddStoryViewModel(authRepository, storyRepository)
    }

    @Test
    fun `when Add Story Should Not Null and Return Success`() {
        val expectedResponse = MutableLiveData<Result<AddStoryResponse>>()
        expectedResponse.value = Result.Success(AddStoryResponse(false, "Success."))

        `when`(
            addStoryViewModel.addStory(
                dummyToken,
                imageMultipart,
                description,
                lat,
                lon
            )
        ).thenReturn(
            expectedResponse
        )
        val actualResponse =
            addStoryViewModel.addStory(dummyToken, imageMultipart, description, lat, lon)
                .getOrAwaitValue()

        Mockito.verify(storyRepository)
            .addStory("Bearer $dummyToken", imageMultipart, description, lat, lon)
        Assert.assertNotNull(actualResponse)
        Assert.assertTrue(actualResponse is Result.Success)
        Assert.assertEquals(false, (actualResponse as Result.Success).data.error)
    }

    @Test
    fun `when Network Error Should Return Error`() {
        val expectedResponse = MutableLiveData<Result<AddStoryResponse>>()
        expectedResponse.value = Result.Error("Error")

        `when`(
            addStoryViewModel.addStory(
                dummyToken,
                imageMultipart,
                description,
                lat,
                lon
            )
        ).thenReturn(
            expectedResponse
        )
        val actualResponse =
            addStoryViewModel.addStory(dummyToken, imageMultipart, description, lat, lon)
                .getOrAwaitValue()

        Mockito.verify(storyRepository)
            .addStory("Bearer $dummyToken", imageMultipart, description, lat, lon)
        Assert.assertNotNull(actualResponse)
        Assert.assertTrue(actualResponse is Result.Error)
    }
}