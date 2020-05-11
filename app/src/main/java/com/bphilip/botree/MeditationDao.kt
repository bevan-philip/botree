package com.bphilip.botree

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface MeditationDao {
    @Query("SELECT * from meditations_table WHERE date BETWEEN :start AND :end ORDER BY id DESC")
    fun getSortedMeditations(start : Long, end : Long): LiveData<List<Meditation>>

    @Query("SELECT avg(duration) from meditations_table WHERE date BETWEEN :start AND :end")
    fun getAvgMeditation(start : Long, end : Long): LiveData<Long>

    @Query("SELECT count(duration) from meditations_table WHERE date = :date")
    fun getCountMeditationsOnDate(date: Long): LiveData<Long>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertMeditation(meditation : Meditation)

    @Query("SELECT * from meditations_table")
    fun getAllMeditations(): List<Meditation>

    @Query("DELETE FROM meditations_table")
    suspend fun deleteAllMeditations()
}
