package com.rizqitsani.storyapp.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.paging.*
import com.rizqitsani.storyapp.data.Result
import com.rizqitsani.storyapp.data.database.StoryDatabase
import com.rizqitsani.storyapp.data.local.StoryRemoteMediator
import com.rizqitsani.storyapp.data.remote.response.AddStoryResponse
import com.rizqitsani.storyapp.data.remote.retrofit.ApiService
import com.rizqitsani.storyapp.domain.model.Story
import okhttp3.MultipartBody
import okhttp3.RequestBody


class StoryRepository(
    private val storyDatabase: StoryDatabase,
    private val apiService: ApiService
) {
    fun getStories(token: String): LiveData<Result<List<Story>>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.getStories(token)
            val stories = response.listStory.map {
                Story(it.id, it.name, it.description, it.photoUrl, it.lat, it.lon)
            }
            emit(Result.Success(stories))
        } catch (e: Exception) {
            Log.d(TAG, "getStories: ${e.message.toString()} ")
            emit(Result.Error(e.message.toString()))
        }
    }

    fun getPagedStories(token: String): LiveData<PagingData<Story>> {
        @OptIn(ExperimentalPagingApi::class)
        return Pager(
            config = PagingConfig(
                pageSize = 1
            ),
            remoteMediator = StoryRemoteMediator(storyDatabase, apiService, token),
            pagingSourceFactory = {
                storyDatabase.storyDao().getAllStory()
            }
        ).liveData
    }

    fun addStory(
        token: String,
        imageMultipart: MultipartBody.Part,
        description: RequestBody
    ): LiveData<Result<AddStoryResponse>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.uploadImage(token, imageMultipart, description)
            emit(Result.Success(response))
        } catch (e: Exception) {
            Log.d(TAG, "addStory: ${e.message.toString()} ")
            emit(Result.Error(e.message.toString()))
        }
    }

    companion object {
        private const val TAG = "StoryRepository"

        @Volatile
        private var instance: StoryRepository? = null
        fun getInstance(
            storyDatabase: StoryDatabase,
            apiService: ApiService
        ): StoryRepository =
            instance ?: synchronized(this) {
                instance ?: StoryRepository(storyDatabase, apiService)
            }.also { instance = it }
    }
}