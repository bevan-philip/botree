package com.bphilip.botree

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneId

// Declares the DAO as a private property in the constructor. Pass in the DAO
// instead of the whole database, because you only need access to the DAO
class WordRepository(private val wordDao: WordDao) {

    private val startTime : MutableLiveData<LocalDateTime> = MutableLiveData()
    private val endTime : MutableLiveData<LocalDateTime> = MutableLiveData()

    var allWords: LiveData<List<Word>> = Transformations.switchMap(endTime) {
        wordDao.getAlphabetizedWords(
            conv(startTime.value) as Long,
            conv(endTime.value) as Long
        )
    }

    suspend fun insert(word: Word) {
        wordDao.insert(word)
    }

    fun changeTime(stime : LocalDateTime, etime: LocalDateTime) {
        startTime.value = stime
        endTime.value = etime
    }
}

fun conv(date: LocalDateTime?): Long? {
    return date?.atZone(ZoneId.systemDefault())?.toInstant()?.toEpochMilli()
}