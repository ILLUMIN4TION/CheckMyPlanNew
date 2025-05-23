package com.example.a0401chkmyplan.scheduleDB

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
    suspend fun getAllSchedules(): List<ScheduleEntity>
}