package com.bphilip.botree

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneId

// Declares the DAO as a private property in the constructor. Pass in the DAO
// instead of the whole database, because you only need access to the DAO
class DataRepository(private val wordDao: WordDao) {

    private val startTime : MutableLiveData<LocalDateTime> = MutableLiveData()
    private val endTime : MutableLiveData<LocalDateTime> = MutableLiveData()

    // Transformation.switchMap does some magic, such that when endTime is changed, it updates the
    // query used for the LiveData, enabling different results to be delivered. Useful for having
    // changeable data durations.
    var allReflections: LiveData<List<Reflection>> = Transformations.switchMap(endTime) {
        wordDao.getSortedReflections(
            LocalDateTimeToTimeStamp(startTime.value) as Long,
            LocalDateTimeToTimeStamp(endTime.value) as Long
        )
    }

    var allMeditations : LiveData<List<Meditation>> = wordDao.getSortedMeditations(
        LocalDateTimeToTimeStamp(LocalDateTime.now().minusDays(6)) as Long,
        LocalDateTimeToTimeStamp(LocalDateTime.now()) as Long
    )

    suspend fun insertReflection(reflection: Reflection) {
        wordDao.insertReflection(reflection)
    }

    suspend fun insertMeditation(meditation: Meditation) {
        wordDao.insertMeditation(meditation)
    }

    fun changeTime(stime : LocalDateTime, etime: LocalDateTime) {
        startTime.value = stime
        endTime.value = etime
    }
}

fun LocalDateTimeToTimeStamp(date: LocalDateTime?): Long? {
    return date?.atZone(ZoneId.systemDefault())?.toInstant()?.toEpochMilli()
}