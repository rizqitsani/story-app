package com.rizqitsani.storyapp.ui.main

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import com.rizqitsani.storyapp.data.remote.response.LoginResult
import com.rizqitsani.storyapp.data.repository.AuthRepository
import com.rizqitsani.storyapp.di.Injection

class MainViewModel(authRepository: AuthRepository) : ViewModel() {
    val user: LiveData<LoginResult> = authRepository.user.asLiveData()
}

class MainViewModelFactory(private val context: Context) :
    ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            return MainViewModel(Injection.provideAuthRepository(context)) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }
}