package com.example.a0401chkmyplan

import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.TimePicker
import androidx.appcompat.app.AppCompatActivity
import com.example.a0401chkmyplan.databinding.ActivityDetailSetBinding
import java.util.*

class ActivityDetailSet : AppCompatActivity() {
    private lateinit var binding: ActivityDetailSetBinding

    private var startTime: String? = null
    private var endTime: String? = null
    private var isRangeMode = false  // 시간 범위 설정 모드

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailSetBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 일반 클릭 → 단일 시각 설정
        binding.taskDetailSetTimebox.setOnClickListener {
            isRangeMode = false
            showTimePickerDialog(isStart = true)
        }

        // 길게 클릭 → 시각 범위 설정
        binding.taskDetailSetTimebox.setOnLongClickListener {
            isRangeMode = true
            showTimePickerDialog(isStart = true)
            true
        }

        //위치설정 액티비티로 이동
        binding.taskDetailSetLocationBox.setOnClickListener {
            val intent = Intent(this, LocationSetActivity::class.java)
            startActivity(intent)
        }
    }

    private fun showTimePickerDialog(isStart: Boolean) {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        //===================================타임피커다이얼로그 생성
        val timePicker = TimePickerDialog(
            this,
            TimePickerDialog.OnTimeSetListener { _: TimePicker, selectedHour: Int, selectedMinute: Int ->
                val timeString = String.format("%02d:%02d", selectedHour, selectedMinute)
                //선택한 시간, 분을 String 포맷으로 변경하고 timeString에 저장

                if (!isRangeMode) {
                    // 단일 시각 모드
                    binding.taskDetailSetTimebox.text = timeString
                    //실제 액티비티의 텍스트뷰에 선택한 시간 저장
                } else {
                    // 범위 설정 모드
                    if (isStart) {
                        startTime = timeString
                        showTimePickerDialog(isStart = false) // 종료 시간 선택
                    } else {
                        endTime = timeString
                        binding.taskDetailSetTimebox.text = "$startTime ~ $endTime"
                        isRangeMode = false
                    }
                }
            },
            hour,
            minute,
            false // false로 설정해서 AM/PM을 사용자가 선택하게끔 함
        )

        //타임피커다일러로그 생성=============================================

//       일부 기기에서 타임피커의 시계를 디지털 시계로 변환 <- 에뮬에서는 작동 안함
//        val tp = timePicker.findViewById<TimePicker>(
//            resources.getIdentifier("timePicker", "id", "android")
//        )
//        tp?.setIs24HourView(true)

        timePicker.show()
    }
}