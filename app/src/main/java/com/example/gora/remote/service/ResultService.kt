package com.example.gora.remote.service

import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

data class ResultRequest(
    val token : String,
    val startX : String,
    val startY : String,
    val endX: String,
    val endY : String
)

data class ResultResponse (
    val id : Int,
    val fee: Int,
    val time : Int,
    val subway : List<Subway>
)

data class Subway (
    val subwayName : String,
    val startGate : String,
    val endGate : String,
    val lon : Double,
    val lat : Double,
    val congestion : List<Int>
)
interface ResultService {

    @GET("/test")
    fun result(
        //@Body request: ResultRequest
    ): Call<List<ResultResponse>>
}
