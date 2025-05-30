package com.example.a0401chkmyplan.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.a0401chkmyplan.notification.NotificationHelper

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val id = intent.getIntExtra("scheduleId", -1)
        val desc = intent.getStringExtra("desc") ?: "알림"
        val type = intent.getStringExtra("type") ?: "status"

        Log.d("AlarmDebug", "🔔 알림 수신 - ID: $id, 내용: $desc, 타입: $type")

        NotificationHelper.showNotification(context, id.toString(), desc, type)
    }
}