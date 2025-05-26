package com.example.a0401chkmyplan.scheduleDB

import androidx.lifecycle.LiveData
import androidx.room.*


@Dao
interface ScheduleDao {
    @Insert
    suspend fun insert(schedule: ScheduleEntity)

    @Update
    suspend fun update(schedule: ScheduleEntity)

    @Delete
    suspend fun delete(schedule: ScheduleEntity)

    //일정 시간순으로 정렬
    @Query("SELECT * FROM schedule_table ORDER BY timeMillis ASC")
    suspend fun getAllSchedules(): MutableList<ScheduleEntity>

    @Query("SELECT * FROM schedule_table WHERE isComplete = 0 ORDER BY timeMillis ASC")
    fun getIncompleteSchedules(): MutableList<ScheduleEntity>

    @Query("SELECT * FROM schedule_table WHERE isComplete = 1 ORDER BY timeMillis ASC")
    fun getCompleteSchedules(): MutableList<ScheduleEntity>

    //캘린더뷰에서 선택한 날짜 및, 선택한 날짜의 일정 가져오기
    @Query("SELECT * FROM schedule_table WHERE date(timeMillis / 1000, 'unixepoch') = date(:millis / 1000, 'unixepoch')")
    suspend fun getSchedulesByDate(millis: Long): List<ScheduleEntity>

    @Query("SELECT * FROM schedule_table WHERE timeMillis BETWEEN :startOfDay AND :endOfDay ORDER BY timeMillis ASC")
    suspend fun getSchedulesByDateRange(startOfDay: Long, endOfDay: Long): List<ScheduleEntity>
}