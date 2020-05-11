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

    @Query("SELECT * from reflections_table")
    fun getAllReflections(): List<Reflection>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertReflection(reflection: Reflection)

    @Query("DELETE FROM reflections_table")
    suspend fun deleteAllReflections()

}