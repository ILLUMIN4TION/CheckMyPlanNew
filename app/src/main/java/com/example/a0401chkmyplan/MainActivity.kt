package com.example.a0401chkmyplan

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import com.example.a0401chkmyplan.R
import com.example.a0401chkmyplan.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {
    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val todayBox1 = binding.todayBox1
        val todayCheck1 = binding.todayCheck1
        val todayList = binding.todayListLayout
        val doneList = binding.doneListLayout


        todayCheck1.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                todayList.removeView(todayBox1)
                doneList.addView(todayBox1)
            } else {
                doneList.removeView(todayBox1)
                todayList.addView(todayBox1, 0)
            }
        }

        val goDetailSet = binding.todayBox1
        goDetailSet.setOnClickListener{
            //임시로 상세페이지로 넘어가게 설정, 먼저 간단한 페이지를 보여주고 그 이후 상세페이지로
            val intent = Intent(this, ActivityDetailSet::class.java)
            startActivity(intent)
        }
    }

    //바텀 시트 프래그먼트 (메인화면에서 특정 버튼을 누르면 아래에서 나오는 창 구현을 위함)
//    private fun BottomSheetFragment() {
//        val modal = BottomSheetFragment()
//    }
}
