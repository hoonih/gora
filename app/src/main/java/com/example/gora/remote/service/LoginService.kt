package com.example.gora.remote.service

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query

data class LoginRequest(
    val token: String,
    val userName: String
)

interface LoginService {
    @POST("/login")
    fun login(
        @Body loginRequest: LoginRequest
    ): Call<String>
}
