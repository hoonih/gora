package com.example.gora.ui

import android.location.Geocoder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.provider.ContactsContract
import android.util.Log
import android.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gora.AddressData
import com.example.gora.Document
import com.example.gora.R
import com.example.gora.databinding.ActivitySearchBinding
import com.google.gson.Gson
import org.json.JSONArray
import org.json.JSONException
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class SearchActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySearchBinding
    private lateinit var adapter: AddressAdapter
    private lateinit var resultList: MutableList<String>
    private lateinit var coordinatesMap: MutableMap<String, Pair<Double, Double>>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initSearch()

        resultList = mutableListOf()
        coordinatesMap = mutableMapOf()
        adapter = AddressAdapter(resultList) { address ->
            val coordinates = coordinatesMap[address]
            if (coordinates != null) {
                val latitude = coordinates.first
                val longitude = coordinates.second
                // 선택된 주소의 위도와 경도 값을 사용하여 원하는 작업을 수행하세요.
            }
        }

        binding.recyclerSearch.adapter = adapter
        binding.recyclerSearch.layoutManager = LinearLayoutManager(this)

        binding.searchSearch.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(keyword: String?): Boolean {
                if(keyword!= null) searchKeyword(keyword)
                return true
            }

            override fun onQueryTextChange(keyword: String?): Boolean {
                if(keyword != null) searchKeyword(keyword)
                return true
            }
        })
    }


    private fun searchKeyword(keyword: String) {
        // 카카오 지도 API 호출 및 데이터 처리
        val thread = Thread(Runnable {
            try {
                val url = URL("https://dapi.kakao.com/v2/local/search/keyword.json?query=$keyword")
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                connection.setRequestProperty("Authorization", "KakaoAK a210fb5834143ffd59d37c106416ece7")

                val responseCode = connection.responseCode
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    val inputStream = connection.inputStream
                    val reader = BufferedReader(InputStreamReader(inputStream))
                    val response = StringBuilder()
                    var line: String?
                    while (reader.readLine().also { line = it } != null) {
                        response.append(line)
                        Log.d("TEST","q"+response)

                    }
                    reader.close()

                    val data = """$response""".trimIndent()
                    val gson = Gson()
                    val parsedData = gson.fromJson(data, AddressData::class.java)
                    val addressNames = parsedData.documents.map { it.address_name }

                    Log.d("TEST","c"+addressNames)

                    runOnUiThread {
                        updateRecyclerView(addressNames)
                    }

                    parseResponse(response.toString())
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        })
        thread.start()
    }

    private fun parseResponse(response: String) {
        try {
            val jsonArray = JSONArray(response)
            resultList.clear()
            coordinatesMap.clear()

            for (i in 0 until jsonArray.length()) {
                val jsonObject = jsonArray.getJSONObject(i)
                val address = jsonObject.getString("address_name")
                val latitude = jsonObject.getString("y").toDouble()
                val longitude = jsonObject.getString("x").toDouble()

                resultList.add(address)
                coordinatesMap[address] = Pair(latitude, longitude)
            }

            runOnUiThread {
                adapter.notifyDataSetChanged()
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    fun updateRecyclerView(newAddressList: List<String>) {
        adapter.updateAddressList(newAddressList)
        adapter.notifyDataSetChanged()
    }
    private fun initSearch() {

    }
}