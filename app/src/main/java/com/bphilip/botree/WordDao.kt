package com.bphilip.botree

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import java.util.*


@Dao
interface WordDao {

    @Query("SELECT * from word_table WHERE date BETWEEN :start AND :end ORDER BY date DESC")
    fun getAlphabetizedWords(start : Long, end : Long): LiveData<List<Word>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(word: Word)

    @Query("DELETE FROM word_table")
    suspend fun deleteAll()
}