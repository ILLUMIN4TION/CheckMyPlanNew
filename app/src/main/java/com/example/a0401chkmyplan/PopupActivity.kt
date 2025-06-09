package com.example.a0401chkmyplan

import android.app.Activity
import android.os.Bundle
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import com.example.a0401chkmyplan.R

class PopupActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 전체화면 알림인 경우 전체화면 플래그 적용
        window.addFlags(
            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON or
                    WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                    WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON or
                    WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        setContentView(R.layout.activity_popup)

        val desc = intent.getStringExtra("desc") ?: "알림 내용이 없습니다."
        val tvDesc = findViewById<TextView>(R.id.tvPopupDesc)
        val btnClose = findViewById<Button>(R.id.btnPopupClose)

        tvDesc.text = desc

        btnClose.setOnClickListener {
            finish()
        }
    }
}