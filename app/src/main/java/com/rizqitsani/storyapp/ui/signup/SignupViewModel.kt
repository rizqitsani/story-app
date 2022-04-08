package com.rizqitsani.storyapp.ui.signup

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.rizqitsani.storyapp.data.remote.response.SignupResponse
import com.rizqitsani.storyapp.data.remote.retrofit.ApiConfig
import com.rizqitsani.storyapp.data.remote.response.ErrorResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignupViewModel : ViewModel() {
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _isSuccess = MutableLiveData<Boolean>()
    val isSuccess: LiveData<Boolean> = _isSuccess

    private val _message = MutableLiveData<String>()
    val message: LiveData<String> = _message

    fun signup(name: String, email: String, password: String) {
        _isLoading.value = true
        _message.value = ""
        val client = ApiConfig.getApiService().signup(name, email, password)
        client.enqueue(object : Callback<SignupResponse> {
            override fun onResponse(
                call: Call<SignupResponse>,
                response: Response<SignupResponse>
            ) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    _isSuccess.value = true
                    _message.value = "Berhasil membuat akun"
                } else {
                    val errorResponse: ErrorResponse = Gson().fromJson(
                        response.errorBody()?.charStream(),
                        ErrorResponse::class.java
                    )
                    _message.value = errorResponse.message
                    Log.e(TAG, "onFailure: ${errorResponse.message}")
                }
            }

            override fun onFailure(call: Call<SignupResponse>, t: Throwable) {
                _isLoading.value = false
                _message.value = "Terjadi kesalahan. Mohon coba beberapa saat lagi"
                Log.e(TAG, "onFailure: ${t.message.toString()}")
            }
        })
    }

    companion object {
        private const val TAG = "SignupViewModel"
    }
}