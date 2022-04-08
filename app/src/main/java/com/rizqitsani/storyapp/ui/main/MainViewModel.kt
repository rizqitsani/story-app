package com.rizqitsani.storyapp.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.rizqitsani.storyapp.data.preferences.AuthPreferences
import com.rizqitsani.storyapp.data.remote.response.LoginResult

class MainViewModel(private val pref: AuthPreferences) : ViewModel() {
    fun getCurrentUser(): LiveData<LoginResult> {
        return pref.getCurrentUser().asLiveData()
    }
}