package com.rizqitsani.storyapp.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.rizqitsani.storyapp.data.database.StoryDatabase
import com.rizqitsani.storyapp.data.local.StoryRemoteMediator
import com.rizqitsani.storyapp.data.remote.retrofit.ApiService
import com.rizqitsani.storyapp.domain.model.Story
import kotlinx.coroutines.flow.Flow

class StoryRepository(
    private val storyDatabase: StoryDatabase,
    private val apiService: ApiService
) {
    fun getStories(token: String): Flow<PagingData<Story>> {
        @OptIn(ExperimentalPagingApi::class)
        return Pager(
            config = PagingConfig(
                pageSize = 1
            ),
            remoteMediator = StoryRemoteMediator(storyDatabase, apiService, token),
            pagingSourceFactory = {
                storyDatabase.storyDao().getAllStory()
            }
        ).flow
    }
}