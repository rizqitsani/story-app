package com.rizqitsani.storyapp.di

import android.content.Context
import com.rizqitsani.storyapp.data.database.StoryDatabase
import com.rizqitsani.storyapp.data.remote.retrofit.ApiConfig
import com.rizqitsani.storyapp.data.repository.StoryRepository

object Injection {
    fun provideRepository(context: Context): StoryRepository {
        val database = StoryDatabase.getDatabase(context)
        val apiService = ApiConfig.getApiService()
        return StoryRepository(database, apiService)
    }
}