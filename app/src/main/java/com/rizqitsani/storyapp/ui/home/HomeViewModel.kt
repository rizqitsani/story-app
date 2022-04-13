package com.rizqitsani.storyapp.ui.home

import android.content.Context
import android.util.Log
import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.google.gson.Gson
import com.rizqitsani.storyapp.data.preferences.AuthPreferences
import com.rizqitsani.storyapp.data.remote.response.ErrorResponse
import com.rizqitsani.storyapp.data.remote.response.ListStoryResponse
import com.rizqitsani.storyapp.data.remote.response.LoginResult
import com.rizqitsani.storyapp.data.remote.retrofit.ApiConfig
import com.rizqitsani.storyapp.data.repository.StoryRepository
import com.rizqitsani.storyapp.di.Injection
import com.rizqitsani.storyapp.domain.model.Story
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeViewModel(
    private val pref: AuthPreferences,
    private val storyRepository: StoryRepository
) :
    ViewModel() {
    private val _listStory = MutableLiveData<List<Story>>()
    val listStory: LiveData<List<Story>> = _listStory

    private val _listPagedStory = MutableLiveData<PagingData<Story>>()
    val listPagedStory: LiveData<PagingData<Story>> = _listPagedStory

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _message = MutableLiveData<String>()
    val message: LiveData<String> = _message

    fun getPagedStories(token: String) {
        viewModelScope.launch {
            storyRepository.getStories("Bearer $token").cachedIn(viewModelScope).collect {
                _listPagedStory.value = it
            }
        }
    }

    fun getStories(token: String) {
        _isLoading.value = true
        _message.value = ""

        val client = ApiConfig.getApiService().getStories("Bearer $token")
        client.enqueue(object : Callback<ListStoryResponse> {
            override fun onResponse(
                call: Call<ListStoryResponse>,
                response: Response<ListStoryResponse>
            ) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    val convertedStories = ArrayList<Story>()

                    responseBody?.listStory?.forEach {
                        convertedStories.add(
                            Story(
                                it.id,
                                it.name,
                                it.description,
                                it.photoUrl,
                                it.lat,
                                it.lon
                            )
                        )
                    }

                    _message.value = ""
                    _listStory.value = convertedStories
                } else {
                    val errorResponse: ErrorResponse = Gson().fromJson(
                        response.errorBody()?.charStream(),
                        ErrorResponse::class.java
                    )
                    _message.value = errorResponse.message
                    Log.e(TAG, "onFailure: ${errorResponse.message}")
                }
            }

            override fun onFailure(call: Call<ListStoryResponse>, t: Throwable) {
                _isLoading.value = false
                _message.value = "Terjadi kesalahan. Mohon coba beberapa saat lagi"
                Log.e(TAG, "onFailure: ${t.message.toString()}")
            }
        })
    }

    fun getCurrentUser(): LiveData<LoginResult> {
        return pref.getCurrentUser().asLiveData()
    }

    fun logout() {
        viewModelScope.launch {
            pref.saveCurrentUser(
                "",
                "",
                ""
            )
        }
    }

    companion object {
        private const val TAG = "HomeViewModel"
    }
}

class HomeViewModelFactory(private val pref: AuthPreferences, private val context: Context) :
    ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            return HomeViewModel(pref, Injection.provideRepository(context = context)) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }
}