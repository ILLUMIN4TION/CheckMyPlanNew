package com.example.a0401chkmyplan

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.a0401chkmyplan.MainActivity



class CheckMyPlanSplash : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_check_my_plan_splash)
        Handler(Looper.getMainLooper()).postDelayed({ //일정 시간 지연이후 실행을 위한 코드
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)

            finish()
        }, 2000)


    }
}

//스플래시 화면 구현 코드 출저  https://dailycoding365.tistory.com/entry/%EC%95%88%EB%93%9C%EB%A1%9C%EC%9D%B4%EB%93%9C-Splash-%ED%99%94%EB%A9%B4-%EA%B5%AC%ED%98%84%ED%95%98%EA%B8%B0