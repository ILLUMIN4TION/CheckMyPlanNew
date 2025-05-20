package com.example.a0401chkmyplan.memoDB

import android.app.Application
import androidx.room.Database
import androidx.room.Room
import kotlin.jvm.java

class MyApplication : Application() {

    companion object {
        lateinit var database: MemoDatabase
    }

    override fun onCreate() {
        super.onCreate()

        database = Room.databaseBuilder(
            applicationContext,
            MemoDatabase::class.java,
            "memo_appDatabase"   // DB 파일 이름
        ).fallbackToDestructiveMigration().build()
    }
}