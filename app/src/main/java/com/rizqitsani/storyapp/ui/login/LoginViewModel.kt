package com.rizqitsani.storyapp.ui.login

import android.util.Log
import androidx.lifecycle.*
import com.google.gson.Gson
import com.rizqitsani.storyapp.data.preferences.AuthPreferences
import com.rizqitsani.storyapp.data.remote.response.LoginResponse
import com.rizqitsani.storyapp.data.remote.retrofit.ApiConfig
import com.rizqitsani.storyapp.data.remote.response.ErrorResponse
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class LoginViewModel(private val pref: AuthPreferences) : ViewModel() {
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _message = MutableLiveData<String>()
    val message: LiveData<String> = _message

    fun login(email: String, password: String) {
        _isLoading.value = true
        _message.value = ""
        val client = ApiConfig.getApiService().login(email, password)
        client.enqueue(object : Callback<LoginResponse> {
            override fun onResponse(
                call: Call<LoginResponse>,
                response: Response<LoginResponse>
            ) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    val responseBody = response.body()

                    viewModelScope.launch {
                        pref.saveCurrentUser(
                            responseBody?.loginResult?.name.toString(),
                            responseBody?.loginResult?.userId.toString(),
                            responseBody?.loginResult?.token.toString()
                        )
                    }

                    _message.value = "Berhasil masuk"
                } else {
                    val errorResponse: ErrorResponse = Gson().fromJson(
                        response.errorBody()?.charStream(),
                        ErrorResponse::class.java
                    )
                    _message.value = errorResponse.message
                    Log.e(TAG, "onFailure: ${errorResponse.message}")
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                _isLoading.value = false
                _message.value = "Terjadi kesalahan. Mohon coba beberapa saat lagi"
                Log.e(TAG, "onFailure: ${t.message.toString()}")
            }
        })
    }

    companion object {
        private const val TAG = "LoginViewModel"
    }
}