package com.example.a0401chkmyplan.scheduleDB

import androidx.room.*

@Entity(tableName = "schedule_table")
data class ScheduleEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val desc: String,
    val timeMillis: Long // 날짜 및 시간을 저장 (millis 기준)
)