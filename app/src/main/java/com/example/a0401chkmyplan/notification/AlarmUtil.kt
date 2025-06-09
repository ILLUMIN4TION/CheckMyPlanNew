package com.example.a0401chkmyplan.notification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.a0401chkmyplan.notification.AlarmReceiver
import com.example.a0401chkmyplan.scheduleDB.ScheduleEntity
import java.util.Date

fun scheduleAlarm(context: Context, schedule: ScheduleEntity, type: String, minutesBefore: Int) {
    val intent = Intent(context, AlarmReceiver::class.java).apply {
        putExtra("scheduleId", schedule.id)
        putExtra("desc", schedule.desc)
        putExtra("type", type)
    }

    val pending = PendingIntent.getBroadcast(
        context,
        schedule.id,
        intent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    val triggerTime = schedule.timeMillis - (minutesBefore * 60 * 1000)

    val manager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    manager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTime, pending)

    Log.d("AlarmSet", "📅 알람 예약됨: ${Date(triggerTime)} (ID: ${schedule.id})")
}