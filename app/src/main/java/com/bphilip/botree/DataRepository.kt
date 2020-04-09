package com.bphilip.botree

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneId

// Declares the DAO as a private property in the constructor. Pass in the DAO
// instead of the whole database, because you only need access to the DAO
class DataRepository(private val reflectionDao: ReflectionDao) {

    private val startTime : MutableLiveData<LocalDateTime> = MutableLiveData()
    private val endTime : MutableLiveData<LocalDateTime> = MutableLiveData()


    // Transformation.switchMap does some magic, such that when endTime is changed, it updates the
    // query used for the LiveData, enabling different results to be delivered. Useful for having
    // changeable data durations.
    var allReflections: LiveData<List<Reflection>> = Transformations.switchMap(endTime) {
        reflectionDao.getSortedReflections(
            localDateTimeToLong(startTime.value) as Long,
            localDateTimeToLong(endTime.value) as Long
        )
    }

    var averageMeditation: LiveData<Long> = reflectionDao.getAvgMeditation()

    var allMeditations : LiveData<List<Meditation>> = reflectionDao.getSortedMeditations(
        localDateToLong(LocalDate.now().minusDays(6)) as Long,
        localDateToLong(LocalDate.now()) as Long
    )

    var meditatedOnDay: List<LiveData<Long>> = listOf(reflectionDao.getCountMeditationsOnDate(localDateToLong(LocalDate.now()) as Long))

    init {
        for (i in 1..4)
        {
            meditatedOnDay = meditatedOnDay + reflectionDao.getCountMeditationsOnDate(localDateToLong(LocalDate.now().minusDays(i.toLong())) as Long)
        }
    }

    var meditatedToday = reflectionDao.getCountMeditationsOnDate(localDateToLong(LocalDate.now()) as Long)

    suspend fun insertReflection(reflection: Reflection) {
        reflectionDao.insertReflection(reflection)
    }

    suspend fun insertMeditation(meditation: Meditation) {
        reflectionDao.insertMeditation(meditation)
    }


    // Requires both variables, because the endTime must be changed for the LiveView to update.
    fun changeTime(stime : LocalDateTime, etime: LocalDateTime) {
        startTime.value = stime
        endTime.value = etime
    }

}

fun localDateToLong (date: LocalDate?): Long? {
    return date?.toEpochDay()
}
fun localDateTimeToLong(date: LocalDateTime?): Long? {
    return date?.atZone(ZoneId.systemDefault())?.toInstant()?.toEpochMilli()
}
