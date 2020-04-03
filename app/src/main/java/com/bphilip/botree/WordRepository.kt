package com.bphilip.botree

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneId

// Declares the DAO as a private property in the constructor. Pass in the DAO
// instead of the whole database, because you only need access to the DAO
class WordRepository(private val wordDao: WordDao) {

    private val startTime : MutableLiveData<LocalDateTime> = MutableLiveData()
    private val endTime : MutableLiveData<LocalDateTime> = MutableLiveData()

    var allReflections: LiveData<List<Reflection>> = Transformations.switchMap(endTime) {
        wordDao.getSortedReflections(
            LocalDateTimeToTimeStamp(startTime.value) as Long,
            LocalDateTimeToTimeStamp(endTime.value) as Long
        )
    }

    suspend fun insertReflection(reflection: Reflection) {
        wordDao.insertReflection(reflection)
    }

    fun changeTime(stime : LocalDateTime, etime: LocalDateTime) {
        startTime.value = stime
        endTime.value = etime
    }
}

fun LocalDateTimeToTimeStamp(date: LocalDateTime?): Long? {
    return date?.atZone(ZoneId.systemDefault())?.toInstant()?.toEpochMilli()
}