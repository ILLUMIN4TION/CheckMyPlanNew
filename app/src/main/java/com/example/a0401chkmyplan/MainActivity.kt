package com.example.activityandfragment

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.example.a0401chkmyplan.R
import com.example.a0401chkmyplan.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
    }

    //바텀 시트 프래그먼트 (메인화면에서 특정 버튼을 누르면 아래에서 나오는 창 구현을 위함)
    private fun BottomSheetFragment() {
        val modal = BottomSheetFragment()
    }
}
