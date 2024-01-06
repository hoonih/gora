package com.example.gora

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class FirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        // 푸시 알림 메시지 수신 시 호출되는 메서드
        // 수신한 메시지 처리 로직을 작성합니다.
        remoteMessage.notification?.let { notification ->
            val title = notification.title
            val body = notification.body
            Log.d("FCM", "푸시 알림 메시지 수신 - 제목: $title, 내용: $body")

            // 수신한 메시지에 대한 추가적인 로직을 작성할 수 있습니다.
        }

        Log.d("TEST","token"+ FirebaseToken)
    }

    override fun onNewToken(token: String) {
        // FCM 토큰 갱신 시 호출되는 메서드
        // 토큰 갱신 로직을 작성합니다.
    }
}