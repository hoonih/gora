package com.example.gora.ui.main

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.gora.FirebaseToken
import com.example.gora.createNotificationChannel
import com.example.gora.databinding.ActivityMainBinding
import com.example.gora.ui.SearchActivity
import com.example.gora.ui.SearchDialog
import com.example.gora.ui.mypage.MyPageActivity
import com.google.firebase.messaging.FirebaseMessaging
import net.daum.mf.map.api.MapView

class MainActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding:ActivityMainBinding
    private val ACCESS_FINE_LOCATION = 1000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initMapView()
        initFirebaseToken()
        topbar()
        createNotificationChannel(baseContext,"1000","AppJam")
    }

    private fun initFirebaseToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val token = task.result
                FirebaseToken = token
                Log.d("TEST","firebaseToken"+ token)
            } else {
                // 토큰 등록 실패 시 처리 로직을 작성합니다.
            }
        }
    }

    private fun initMapView() {
        //val mapView = MapView(this)
        //binding.mapviewMain.addView(mapView)
        if(checkLocationService()) {
            permissionCheck()
        } else {
            //GPS 꺼짐
        }
    }

    private fun permissionCheck() {
        val preference = getPreferences(MODE_PRIVATE)
        val isFirstCheck = preference.getBoolean("isFirstPermissionCheck", true)
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // 권한이 없는 상태
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                // 권한 거절 (다시 한 번 물어봄)
                val builder = AlertDialog.Builder(this)
                builder.setMessage("현재 위치를 확인하시려면 위치 권한을 허용해주세요.")
                builder.setPositiveButton("확인") { dialog, which ->
                    ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), ACCESS_FINE_LOCATION)
                }
                builder.setNegativeButton("취소") { dialog, which ->

                }
                builder.show()
            } else {
                if (isFirstCheck) {
                    // 최초 권한 요청
                    preference.edit().putBoolean("isFirstPermissionCheck", false).apply()
                    ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), ACCESS_FINE_LOCATION)
                } else {
                    // 다시 묻지 않음 클릭 (앱 정보 화면으로 이동)
                    val builder = AlertDialog.Builder(this)
                    builder.setMessage("현재 위치를 확인하시려면 설정에서 위치 권한을 허용해주세요.")
                    builder.setPositiveButton("설정으로 이동") { dialog, which ->
                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:$packageName"))
                        startActivity(intent)
                    }
                    builder.setNegativeButton("취소") { dialog, which ->

                    }
                    builder.show()
                }
            }
        } else {
            // 권한이 있는 상태
            startTracking()
        }
    }

    // 권한 요청 후 행동
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == ACCESS_FINE_LOCATION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 권한 요청 후 승인됨 (추적 시작)
                Toast.makeText(this, "위치 권한이 승인되었습니다", Toast.LENGTH_SHORT).show()
                startTracking()
            } else {
                // 권한 요청 후 거절됨 (다시 요청 or 토스트)
                Toast.makeText(this, "위치 권한이 거절되었습니다", Toast.LENGTH_SHORT).show()
                permissionCheck()
            }
        }
    }

    // GPS가 켜져있는지 확인
    private fun checkLocationService(): Boolean {
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }

    // 위치추적 시작
    private fun startTracking() {
        binding.mapviewMain.currentLocationTrackingMode = MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeading
        //setBuildingMaker()
    }

    // 위치추적 중지
    private fun stopTracking() {
        binding.mapviewMain.currentLocationTrackingMode = MapView.CurrentLocationTrackingMode.TrackingModeOff
    }

    private fun topbar() {
        binding.btSearch.setOnClickListener {
            val intent = Intent(this, SearchActivity::class.java)
            startActivity(intent)
        }
        binding.igProfile.setOnClickListener {
            val intent = Intent(this, MyPageActivity::class.java)
            startActivity(intent)
        }
    }


    override fun onClick(view: View?) {

    }

    /*private fun setBuildingMaker() {

        val map = binding.mapviewMain.getMapAsync { // 지도가 로드된 후에 실행되는 콜백
            val markerImage = MarkerImage.fromResource(R.drawable.marker_building)

            // 건물 위치 정보 수집
            val buildingLocations = getBuildingLocations() // 건물 위치 정보를 얻어오는 함수

            // 건물 위치 정보를 바탕으로 마커 추가
            for (location in buildingLocations) {
                val marker = Marker()
                marker.position = MapPoint.mapPointWithGeoCoord(location.latitude, location.longitude) // 건물의 위도, 경도 좌표
                marker.markerType = MarkerType.CustomImage
                marker.customImage = markerImage
                marker.tag = location.name // 건물에 대한 추가 정보를 태그로 저장할 수 있습니다.

                map.addPOIItem(marker) // 마커를 지도에 추가합니다.
            }

            // 마커 클릭 이벤트 처리
            map.setPOIItemEventListener(object : MapPOIItemEventListener {
                override fun onPOIItemSelected(mapView: MapView?, marker: MapPOIItem?) {
                    val buildingTag = marker?.tag // 선택된 마커의 태그 정보를 확인할 수 있습니다.
                    // 선택된 건물에 대한 추가 작업을 수행합니다.
                }

                override fun onCalloutBalloonOfPOIItemTouched(mapView: MapView?, marker: MapPOIItem?) {
                    // 말풍선이 터치됐을 때의 이벤트 처리 (선택적으로 구현)
                }

                override fun onCalloutBalloonOfPOIItemTouched(mapView: MapView?, marker: MapPOIItem?, buttonType: MapPOIItem.CalloutBalloonButtonType?) {
                    // 말풍선 내의 버튼이 터치됐을 때의 이벤트 처리 (선택적으로 구현)
                }

                override fun onDraggablePOIItemMoved(mapView: MapView?, marker: MapPOIItem?, mapPoint: MapPoint?) {
                    // 드래그 가능한 마커가 이동됐을 때의 이벤트 처리 (선택적으로 구현)
                }
            })
        }
    }*/
}