package com.example.gora.remote.service

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

data class LoginRequest(
    val token: String,
    val userName: String
)

data class User(
    val id: Long,
    val userName: String,
    val point: Int,
    val token: String,
)

interface LoginService {
    @POST("api/auth/login")
    fun login(
        @Body loginRequest: LoginRequest
    ): Call<String>

    @GET("/api/auth/profile")
    fun user(
        @Query("token") token: String
    ): Call<User>
}
