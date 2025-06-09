package com.example.a0401chkmyplan.memoDB

import androidx.room.*

@Dao
interface MemoDao {
    @Insert
    suspend fun insert(memo: MemoEntity)   // 일정 추가

    @Query("SELECT * FROM memo_table")
    suspend fun getAllMemo(): MutableList<MemoEntity>   // 모든 일정 가져오기

    @Delete
    suspend fun delete(memo: MemoEntity)   // 일정 삭제

    @Update
    suspend fun update(memo: MemoEntity)

    @Query("SELECT * FROM memo_table WHERE title LIKE '%' || :query || '%' OR `desc` LIKE '%' || :query || '%'")
    fun searchMemo(query: String): List<MemoEntity>
}