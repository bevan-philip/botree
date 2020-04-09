package com.bphilip.botree

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query


@Dao
interface ReflectionDao {

    @Query("SELECT * from reflections_table WHERE date BETWEEN :start AND :end ORDER BY date DESC")
    fun getSortedReflections(start : Long, end : Long): LiveData<List<Reflection>>

    @Query("SELECT * from meditations_table WHERE date BETWEEN :start AND :end ORDER BY date DESC")
    fun getSortedMeditations(start : Long, end : Long): LiveData<List<Meditation>>

    @Query("SELECT avg(duration) from meditations_table")
    fun getAvgMeditation(): LiveData<Long>

    @Query("SELECT count(duration) from meditations_table WHERE date = :date")
    fun getCountMeditationsOnDate(date: Long): LiveData<Long>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertReflection(reflection: Reflection)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertMeditation(meditation : Meditation)

    @Query("DELETE FROM reflections_table")
    suspend fun deleteAllReflections()

    @Query("DELETE FROM meditations_table")
    suspend fun deleteAllMeditations()
}