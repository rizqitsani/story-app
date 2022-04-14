package com.rizqitsani.storyapp.ui.addstory

import android.content.Context
import androidx.lifecycle.*
import com.rizqitsani.storyapp.data.remote.response.LoginResult
import com.rizqitsani.storyapp.data.repository.AuthRepository
import com.rizqitsani.storyapp.data.repository.StoryRepository
import com.rizqitsani.storyapp.di.Injection
import okhttp3.MultipartBody
import okhttp3.RequestBody

class AddStoryViewModel(
    authRepository: AuthRepository,
    private val storyRepository: StoryRepository
) : ViewModel() {
    val user: LiveData<LoginResult> = authRepository.user.asLiveData()

    fun addStory(token: String, imageMultipart: MultipartBody.Part, description: RequestBody) =
        storyRepository.addStory("Bearer $token", imageMultipart, description)
}

class AddStoryViewModelFactory(private val context: Context) :
    ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AddStoryViewModel::class.java)) {
            return AddStoryViewModel(
                Injection.provideAuthRepository(context),
                Injection.provideStoryRepository(context)
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }
}