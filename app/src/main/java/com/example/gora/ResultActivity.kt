package com.example.gora

import android.os.Binder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.gora.databinding.ActivityResultBinding
import com.example.gora.remote.RetrofitClient
import com.example.gora.remote.service.LoginService
import com.example.gora.remote.service.ResultRequest
import com.example.gora.remote.service.ResultResponse
import com.example.gora.remote.service.ResultService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ResultActivity : AppCompatActivity() {

    private lateinit var binding: ActivityResultBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initLocation()
        postResult()
    }

    private fun initLocation() {
        binding.tvResultStart.text = "현위치 : $START_ADDRESS"
        binding.tvResultEnd.text = "목적지 : $END_ADDRESS"
    }

    private fun postResult() {
        val resultService = RetrofitClient.getRetrofit().create(ResultService::class.java)

        /*val resultRequest = ResultRequest(

        )*/
        resultService.result().enqueue(object : Callback<List<ResultResponse>> {
            override fun onResponse(
                call: Call<List<ResultResponse>>,
                response: Response<List<ResultResponse>>
            ) {
                if(response.code() == 200) {
                    binding.txTime.text = "${response.body()!![0].time}분"
                    binding.txCard.text = "카드 ${response.body()!![0].fee}원"
                }
            }

            override fun onFailure(call: Call<List<ResultResponse>>, t: Throwable) {
                Toast.makeText(baseContext,"서버통신에 실패하였습니다",Toast.LENGTH_SHORT).show()
                Log.d("TEST","t"+t)
            }

        })

    }
}