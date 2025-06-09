package com.example.a0401chkmyplan.notification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import com.example.a0401chkmyplan.notification.AlarmReceiver
import com.example.a0401chkmyplan.scheduleDB.ScheduleEntity

object AlarmScheduler {

    fun scheduleAlarm(context: Context,schedule: ScheduleEntity, minutesBefore: Int) {
        val manager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        // 알람 예약 시간 계산
        val triggerTime = schedule.timeMillis - (minutesBefore * 60 * 1000)
        val now = System.currentTimeMillis()

        Log.d("AlarmSet", "예약된 알람 시간: ${java.util.Date(triggerTime)}")
        Log.d("AlarmSet", "현재 시간: ${java.util.Date(now)}")

        // ▶ 예약 시간이 현재보다 이전이면 보정
        val finalTriggerTime = if (triggerTime < now) {
            Log.w("AlarmSet", "예약 시간이 현재보다 이전입니다. 현재 시간 + 5초 후로 알람 예약 보정합니다.")
            now + 5 * 1000 // 5초 후로 예약
        } else {
            triggerTime
        }

        // PendingIntent 생성
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra("scheduleId", schedule.id)
            putExtra("desc", schedule.desc)
        }

        val pending = PendingIntent.getBroadcast(
            context,
            schedule.id.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // ▶ 정확한 시간에 알람 예약 (Doze 모드 고려)
        manager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, finalTriggerTime, pending)

        Log.d("AlarmSet", "알람 예약 완료: $finalTriggerTime")
    }
//    fun scheduleAlarm(context: Context, triggerTime: Long, pendingIntent: PendingIntent) {
//        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
//
//        try {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
//                if (alarmManager.canScheduleExactAlarms()) {
//                    alarmManager.setExactAndAllowWhileIdle(
//                        AlarmManager.RTC_WAKEUP,
//                        triggerTime,
//                        pendingIntent
//                    )
//                } else {
//                    // 권한이 없으므로 설정 화면으로 유도
//                    Toast.makeText(context, "정확한 알람 권한이 필요합니다.", Toast.LENGTH_SHORT).show()
//                    val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
//                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
//                    context.startActivity(intent)
//                }
//            } else {
//                // Android 12 미만에서는 바로 설정 가능
//                alarmManager.setExactAndAllowWhileIdle(
//                    AlarmManager.RTC_WAKEUP,
//                    triggerTime,
//                    pendingIntent
//                )
//            }
//        } catch (e: SecurityException) {
//            Log.e("AlarmScheduler", "정확한 알람 권한이 없음: ${e.message}")
//            Toast.makeText(context, "알람을 설정할 수 없습니다 (권한 필요)", Toast.LENGTH_SHORT).show()
//        }
//    }
}