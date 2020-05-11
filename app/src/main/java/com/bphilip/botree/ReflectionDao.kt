package com.bphilip.botree

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query


@Dao
interface ReflectionDao {

    /*
     * The usage of LiveData ensures that it automatically updates any UI elements relying on it
     * when the underlying data changes, which creates fluid UX. Additional bonus, it automatically
     * ensures it all happens off the main thread, preventing blocking.
     */

    @Query("SELECT * from reflections_table WHERE date BETWEEN :start AND :end ORDER BY date DESC")
    fun getSortedReflections(start : Long, end : Long): LiveData<List<Reflection>>

    @Query("SELECT * from meditations_table WHERE date BETWEEN :start AND :end ORDER BY id DESC")
    fun getSortedMeditations(start : Long, end : Long): LiveData<List<Meditation>>

    @Query("SELECT avg(duration) from meditations_table WHERE date BETWEEN :start AND :end")
    fun getAvgMeditation(start : Long, end : Long): LiveData<Long>

    @Query("SELECT count(duration) from meditations_table WHERE date = :date")
    fun getCountMeditationsOnDate(date: Long): LiveData<Long>

    @Query("SELECT * from meditations_table")
    suspend fun getAllMeditations(): List<Meditation>

    @Query("SELECT * from reflections_table")
    suspend fun getAllReflections(): List<Reflection>

    @Query("SELECT * from reflections_table WHERE id = :id")
    fun getReflectionById(id : Int): Reflection

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertReflection(reflection: Reflection)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertMeditation(meditation : Meditation)

    @Query("DELETE FROM reflections_table")
    suspend fun deleteAllReflections()

    @Query("DELETE FROM meditations_table")
    suspend fun deleteAllMeditations()
}