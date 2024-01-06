package com.example.gora

import android.graphics.Color
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
import net.daum.mf.map.api.MapPoint
import net.daum.mf.map.api.MapPolyline
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

        val sp = getSharedPreferences("autoLogin", MODE_PRIVATE);
        val userid = sp.getString("token", null)

        val resultRequest = ResultRequest(
            token = userid!!,
            /*startX = intent.extras?.getString("start_x")!!,
            startY = intent.extras?.getString("start_y")!!,
            endX = intent.extras?.getString("end_x")!!,
            endY = intent.extras?.getString("end_y")!!*/
            startX = "126.9255",
            startY = "37.55076",
            endX = "127.1006",
            endY = "37.48822"
        )
        resultService.result(resultRequest).enqueue(object : Callback<List<ResultResponse>> {
            override fun onResponse(
                call: Call<List<ResultResponse>>,
                response: Response<List<ResultResponse>>
            ) {
                if(response.code() == 200) {

                    binding.txTime.text = "${response.body()?.get(0)?.time}분"
                    binding.txCard.text = "카드 ${response.body()?.get(0)?.fee}원"

                    val polyline = MapPolyline()
                    polyline.lineColor = Color.BLUE

                    response.body()!![0].apply {
                        for (i in 0..subway.size)
                            polyline.addPoint(MapPoint.mapPointWithGeoCoord(subway[i].lat,subway[i].lon))
                    }
                    polyline.addPoint(MapPoint.mapPointWithGeoCoord(127.0,35.0))

                    binding.mapviewMain.addPolyline(polyline)

                }
            }

            override fun onFailure(call: Call<List<ResultResponse>>, t: Throwable) {
                Toast.makeText(baseContext,"서버통신에 실패하였습니다",Toast.LENGTH_SHORT).show()
                Log.d("TEST","t"+t)
            }

        })

    }
}