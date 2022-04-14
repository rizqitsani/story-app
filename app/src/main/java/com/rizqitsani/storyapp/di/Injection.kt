package com.rizqitsani.storyapp.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.rizqitsani.storyapp.data.database.StoryDatabase
import com.rizqitsani.storyapp.data.preferences.AuthPreferences
import com.rizqitsani.storyapp.data.remote.retrofit.ApiConfig
import com.rizqitsani.storyapp.data.repository.AuthRepository
import com.rizqitsani.storyapp.data.repository.StoryRepository

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "auth")

object Injection {
    fun provideStoryRepository(context: Context): StoryRepository {
        val database = StoryDatabase.getDatabase(context)
        val apiService = ApiConfig.getApiService()
        return StoryRepository.getInstance(database, apiService)
    }

    fun provideAuthRepository(context: Context): AuthRepository {
        val apiService = ApiConfig.getApiService()
        val authPreference = AuthPreferences.getInstance(context.dataStore)
        return AuthRepository.getInstance(authPreference, apiService)
    }
}