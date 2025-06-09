package com.example.a0401chkmyplan.memoDB

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "memo_table")
data class MemoEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,  // 자동 증가 ID
    val title: String,   // 일정 제목
    val desc: String,     // 일정 내용

)