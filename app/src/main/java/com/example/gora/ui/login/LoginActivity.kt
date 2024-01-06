package com.example.gora.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.gora.R
import com.example.gora.databinding.ActivityLoginBinding
import com.example.gora.remote.RetrofitClient
import com.example.gora.remote.service.LoginRequest
import com.example.gora.remote.service.LoginService
import com.example.gora.ui.main.MainActivity
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.KakaoSdk
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.common.util.Utility
import com.kakao.sdk.user.UserApiClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    private fun goralogin(token: String, username: String) {
        val loginservice = RetrofitClient.getRetrofit().create(LoginService::class.java)


        val loginRequest = LoginRequest(
            token = token,
            userName = username
        )


        loginservice.login(loginRequest).enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                if (response.isSuccessful()) {
                    Log.d("test", "test")
                    if(response.code() == 204) {
                        val auto = getSharedPreferences("autoLogin", MODE_PRIVATE)
                        val autoLoginEdit = auto.edit()
                        autoLoginEdit.putString("token", token)
                        autoLoginEdit.commit()

                        val intent = Intent(baseContext,MainActivity::class.java)
                        startActivity(intent)
                    }
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

            override fun onFailure(call: Call<String>, t: Throwable) {
                Log.d("theia", "API FAIL: ${call}")
            }

        })

    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Log.d("test", "keyhash : ${Utility.getKeyHash(this)}")


        KakaoSdk.init(this, "2e4a12f99b33b3180c21c55b3a3c4552")

        binding.btKakao.setOnClickListener {
            val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
                if (error != null) {
                    Log.e("LOGIN", "카카오계정으로 로그인 실패", error)
                } else if (token != null) {
                    Log.i("LOGIN", "카카오계정으로 로그인 성공 ${token.accessToken}")
                    UserApiClient.instance.me { user, error ->
                        goralogin(user?.id.toString(), user?.kakaoAccount?.name.toString())
                    }
                }
            }
            // 카카오톡이 설치되어 있으면 카카오톡으로 로그인, 아니면 카카오계정으로 로그인
            val context = this
            if (UserApiClient.instance.isKakaoTalkLoginAvailable(context)) {
                UserApiClient.instance.loginWithKakaoTalk(context) { token, error ->
                    if (error != null) {
                        Log.e("LOGIN", "카카오톡으로 로그인 실패", error)

                        // 사용자가 카카오톡 설치 후 디바이스 권한 요청 화면에서 로그인을 취소한 경우,
                        // 의도적인 로그인 취소로 보고 카카오계정으로 로그인 시도 없이 로그인 취소로 처리 (예: 뒤로 가기)
                        if (error is ClientError && error.reason == ClientErrorCause.Cancelled) {
                            return@loginWithKakaoTalk
                        }

                        // 카카오톡에 연결된 카카오계정이 없는 경우, 카카오계정으로 로그인 시도
                        UserApiClient.instance.loginWithKakaoAccount(context, callback = callback)
                    } else if (token != null) {
                        Log.i("LOGIN", "카카오톡으로 로그인 성공 ${token.accessToken}")
                        UserApiClient.instance.me { user, error ->
                            goralogin(user?.id.toString(), user?.kakaoAccount?.name.toString())
                        }
                    }
                }
            } else {
                UserApiClient.instance.loginWithKakaoAccount(context, callback = callback)
            }
        }
    }
}