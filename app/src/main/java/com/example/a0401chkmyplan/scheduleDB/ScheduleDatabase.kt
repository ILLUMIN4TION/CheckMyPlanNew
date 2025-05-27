package com.example.a0401chkmyplan.scheduleDB
import android.content.Context
import androidx.room.*

@Database(entities = [ScheduleEntity::class], version = 2) //앱 강종나서 버전 수정
abstract class ScheduleDatabase : RoomDatabase() {
    abstract fun scheduleDao(): ScheduleDao

    companion object {
        @Volatile private var INSTANCE: ScheduleDatabase? = null

        fun getDatabase(context: Context): ScheduleDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ScheduleDatabase::class.java,
                    "schedule_database"
                )
                    .fallbackToDestructiveMigration()  //앱강종나서 이전데이터삭제 후 실행
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}