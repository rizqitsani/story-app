package com.rizqitsani.storyapp.data.remote.retrofit

import com.rizqitsani.storyapp.data.remote.response.LoginResponse
import com.rizqitsani.storyapp.data.remote.response.SignupResponse
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface ApiService {
    @FormUrlEncoded
    @POST("register")
    fun signup(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<SignupResponse>


    @FormUrlEncoded
    @POST("login")
    fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<LoginResponse>
}