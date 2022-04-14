package com.rizqitsani.storyapp.ui.login

import android.content.Context
import androidx.lifecycle.*
import com.rizqitsani.storyapp.data.repository.AuthRepository
import com.rizqitsani.storyapp.di.Injection

class LoginViewModel(private val authRepository: AuthRepository) : ViewModel() {
    fun login(email: String, password: String) = authRepository.login(email, password)
}

class LoginViewModelFactory(private val context: Context) :
    ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            return LoginViewModel(Injection.provideAuthRepository(context)) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }
}