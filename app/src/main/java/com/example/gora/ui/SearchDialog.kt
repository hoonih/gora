package com.example.gora.ui

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.example.gora.databinding.DialogSearchBinding


class SearchDialog(private val context : AppCompatActivity) {

    private lateinit var binding: DialogSearchBinding
    private val dialog = Dialog(context)

    fun show(content: String) {
        binding = DialogSearchBinding.inflate(context.layoutInflater)

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)   //타이틀바 제거
        dialog.setContentView(binding.root)     //다이얼로그에 사용할 xml 파일을 불러옴
        dialog.setCancelable(false)    //다이얼로그의 바깥 화면을 눌렀을 때 다이얼로그가 닫히지 않도록 함

        //binding.etSearchStart.text = content

        binding.ivSearchClose.setOnClickListener {
            dialog.dismiss()
        }

        binding.btnSearchShow.setOnClickListener {

        }

        binding.etSearchStart.setOnClickListener {
            val intent = Intent(context,SearchActivity::class.java)
            context.startActivity(intent)
        }
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialog.window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)

        dialog.show()
    }

}