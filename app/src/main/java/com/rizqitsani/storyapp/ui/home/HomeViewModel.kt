package com.rizqitsani.storyapp.ui.home

import android.content.Context
import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.rizqitsani.storyapp.data.remote.response.LoginResult
import com.rizqitsani.storyapp.data.repository.AuthRepository
import com.rizqitsani.storyapp.data.repository.StoryRepository
import com.rizqitsani.storyapp.di.Injection
import com.rizqitsani.storyapp.domain.model.Story
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class HomeViewModel(
    private val authRepository: AuthRepository,
    private val storyRepository: StoryRepository
) :
    ViewModel() {
    private val _listPagedStory = MutableLiveData<PagingData<Story>>()
    val listPagedStory: LiveData<PagingData<Story>> = _listPagedStory

    val user: LiveData<LoginResult> = authRepository.user.asLiveData()

    fun getPagedStories() {
        viewModelScope.launch {
            authRepository.user.collect { user ->
                storyRepository.getPagedStories("Bearer ${user.token}").cachedIn(viewModelScope)
                    .collect {
                        _listPagedStory.value = it
                    }
            }
        }
    }

    fun getStories(token: String) = storyRepository.getStories("Bearer $token")

    fun logout() = viewModelScope.launch {
        authRepository.logout()
    }
}

class HomeViewModelFactory(private val context: Context) :
    ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            return HomeViewModel(
                Injection.provideAuthRepository(context),
                Injection.provideStoryRepository(context)
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }
}