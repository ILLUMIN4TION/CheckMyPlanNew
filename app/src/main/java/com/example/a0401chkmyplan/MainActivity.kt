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

// todayBox1: 첫 번째 할 일 박스의 뷰를 변수에 저장
        val todayBox1 = binding.todayBox1

// todayCheck1: 해당 박스 안에 있는 체크박스 뷰를 변수에 저장
        val todayCheck1 = binding.todayCheck1

// todayList: '오늘 할 일' 리스트(LinearLayout)를 변수에 저장
        val todayList = binding.todayListLayout

// doneList: '완료된 일' 리스트(LinearLayout)를 변수에 저장
        val doneList = binding.doneListLayout

// 체크박스의 체크 상태가 바뀔 때 실행되는 리스너 등록
        todayCheck1.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                // 체크되면(todayBox1을) '오늘 할 일' 리스트에서 제거하고,
                // '완료된 일' 리스트에 추가
                todayList.removeView(todayBox1)
                doneList.addView(todayBox1)
            } else {
                // 체크 해제되면(todayBox1을) '완료된 일' 리스트에서 제거하고,
                // '오늘 할 일' 리스트 맨 위에 다시 추가
                doneList.removeView(todayBox1)
                todayList.addView(todayBox1, 0)  // 0: 리스트 제일 위에 추가
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
