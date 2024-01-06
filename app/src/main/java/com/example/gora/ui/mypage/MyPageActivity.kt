package com.example.gora.ui.mypage

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.gora.R
import com.example.gora.databinding.ActivityMyPageBinding
import com.example.gora.remote.RetrofitClient
import com.example.gora.remote.service.LoginRequest
import com.example.gora.remote.service.LoginService
import com.example.gora.remote.service.User
import com.example.gora.ui.main.MainActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

class MyPageActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMyPageBinding

    private fun userinfo(token : String) {

        val loginservice = RetrofitClient.getRetrofit().create(LoginService::class.java)


        loginservice.user(token).enqueue(object : Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) {
                if (response.isSuccessful()) {
                    Log.d("test", "test")
                    binding.txName.text = response.body()?.userName
                    binding.txPoint.text = "온도: ${response.body()?.userName}°C"
                }
                else {
                    try {
                        val body = response.errorBody()!!.string()
                        Log.d("test", "error - body : $body")
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                Log.d("theia", "API FAIL: ${call}")
            }

        })


    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sp = getSharedPreferences("autoLogin", MODE_PRIVATE);
        val userid = sp.getString("token", null)

        userinfo(userid.toString())

    }
}