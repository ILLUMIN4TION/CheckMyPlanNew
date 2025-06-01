package com.example.a0401chkmyplan.scheduleDB

import androidx.room.*

@Entity(tableName = "schedule_table")
data class ScheduleEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val desc: String,
    val timeMillis: Long, // 날짜 및 시간을 저장 (millis 기준)
    val isComplete: Boolean = false, //일정이 완료되었는 지 확인 기본 false

    //위치정보를 저장하기 위한 변수들
    val latitude: Double? = null,
    val longitude: Double? = null,
    val alarmType: String = "status",             // ✅ 추가
    val alarmOffsetMinutes: Int = 30
)