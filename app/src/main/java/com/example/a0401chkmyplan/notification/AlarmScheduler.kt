package com.example.a0401chkmyplan.notification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import com.example.a0401chkmyplan.alarm.AlarmReceiver

object AlarmScheduler {
    fun scheduleAlarm(context: Context, triggerTime: Long, pendingIntent: PendingIntent) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (alarmManager.canScheduleExactAlarms()) {
                    alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        triggerTime,
                        pendingIntent
                    )
                } else {
                    // 권한이 없으므로 설정 화면으로 유도
                    Toast.makeText(context, "정확한 알람 권한이 필요합니다.", Toast.LENGTH_SHORT).show()
                    val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    context.startActivity(intent)
                }
            } else {
                // Android 12 미만에서는 바로 설정 가능
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    triggerTime,
                    pendingIntent
                )
            }
        } catch (e: SecurityException) {
            Log.e("AlarmScheduler", "정확한 알람 권한이 없음: ${e.message}")
            Toast.makeText(context, "알람을 설정할 수 없습니다 (권한 필요)", Toast.LENGTH_SHORT).show()
        }
    }
}