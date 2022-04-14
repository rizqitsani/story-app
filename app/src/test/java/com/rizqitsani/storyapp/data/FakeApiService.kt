package com.rizqitsani.storyapp.data

import com.rizqitsani.storyapp.DataDummy
import com.rizqitsani.storyapp.data.remote.response.AddStoryResponse
import com.rizqitsani.storyapp.data.remote.response.ListStoryResponse
import com.rizqitsani.storyapp.data.remote.response.LoginResponse
import com.rizqitsani.storyapp.data.remote.response.SignupResponse
import com.rizqitsani.storyapp.data.remote.retrofit.ApiService
import okhttp3.MultipartBody
import okhttp3.RequestBody

class FakeApiService : ApiService {
    private val dummyStories = DataDummy.generateDummyStoriesResponse()
    private val dummyUser = DataDummy.generateDummyUser()

    override suspend fun signup(name: String, email: String, password: String): SignupResponse {
        return SignupResponse(false, "Success.")
    }

    override suspend fun login(email: String, password: String): LoginResponse {
        return LoginResponse(dummyUser, false, "Success.")
    }

    override suspend fun getStories(token: String): ListStoryResponse {
        return ListStoryResponse(dummyStories, false, "Success.")
    }

    override suspend fun getPagedStories(token: String, page: Int, size: Int): ListStoryResponse {
        return ListStoryResponse(dummyStories, false, "Success.")
    }

    override suspend fun uploadImage(
        token: String,
        file: MultipartBody.Part,
        description: RequestBody
    ): AddStoryResponse {
        return AddStoryResponse(false, "Success.")
    }
}