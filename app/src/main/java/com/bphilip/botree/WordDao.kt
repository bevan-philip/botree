package com.bphilip.botree

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query


@Dao
interface WordDao {

    @Query("SELECT * from reflections_table WHERE date BETWEEN :start AND :end ORDER BY date DESC")
    fun getSortedReflections(start : Long, end : Long): LiveData<List<Reflection>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertReflection(reflection: Reflection)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertMeditation(meditation : Meditation)

    @Query("DELETE FROM reflections_table")
    suspend fun deleteAllReflections()
}