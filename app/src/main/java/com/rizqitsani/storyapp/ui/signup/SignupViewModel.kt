package com.rizqitsani.storyapp.ui.signup

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.rizqitsani.storyapp.data.repository.AuthRepository
import com.rizqitsani.storyapp.di.Injection

class SignupViewModel(private val authRepository: AuthRepository) : ViewModel() {
    fun signup(name: String, email: String, password: String) =
        authRepository.signup(name, email, password)
}

class SignupViewModelFactory(private val context: Context) :
    ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SignupViewModel::class.java)) {
            return SignupViewModel(Injection.provideAuthRepository(context)) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }
}