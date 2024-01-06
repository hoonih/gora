package com.example.gora.ui

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.example.gora.R
import com.example.gora.databinding.FragmentSearchBinding


class SearchFragment(private val context : AppCompatActivity) {

    private lateinit var binding: FragmentSearchBinding
    private val dialog = Dialog(context)

    fun show(content: String) {
        binding = FragmentSearchBinding.inflate(context.layoutInflater)

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)   //타이틀바 제거
        dialog.setContentView(binding.root)     //다이얼로그에 사용할 xml 파일을 불러옴
        dialog.setCancelable(false)    //다이얼로그의 바깥 화면을 눌렀을 때 다이얼로그가 닫히지 않도록 함

        //binding.etSearchStart.text = content

        binding.ivSearchClose.setOnClickListener {
            dialog.dismiss()
        }

        binding.btnSearchShow.setOnClickListener {

        }
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialog.window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)

        dialog.show()
    }

}