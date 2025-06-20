package com.example.a0401chkmyplan.notification

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.a0401chkmyplan.PopupActivity
import com.example.a0401chkmyplan.R

object NotificationHelper {
    private const val CHANNEL_ID = "schedule_channel"

    fun showNotification(context: Context, id: String, desc: String, type: String) {
        createNotificationChannel(context)

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle("일정 알림")
            .setContentText(desc)
            .setSmallIcon(R.drawable.alarm_24dp_e3e3e3_fill0_wght400_grad0_opsz24)
            .setAutoCancel(true)

        when (type) {
            "popup", "fullscreen" -> {
                val intent = Intent(context, PopupActivity::class.java).apply {
                    putExtra("desc", desc)
                }
                val pending = PendingIntent.getActivity(
                    context, id.toInt(), intent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )

                if (type == "fullscreen") {
                    builder.setFullScreenIntent(pending, true)
                } else {
                    builder.setContentIntent(pending)
                }
            }
        }

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(id.toInt(), builder.build())
    }

    fun cancelAlarm(context: Context, scheduleId: Long) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context, scheduleId.toInt(), intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
        pendingIntent.cancel()
    }

    private fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID, "일정 알림 채널", NotificationManager.IMPORTANCE_HIGH
            )
            val manager = context.getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }
}
