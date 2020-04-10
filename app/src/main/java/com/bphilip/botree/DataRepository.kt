package com.bphilip.botree

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneId

/**
 * DataRepository
 * Holds all the data that is used throughout the application.
 * Isn't much use in this application, as we only have one data source (the Room database),
 * but works regardless.
 */
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

    // Gets the average meditation.
    var averageMeditation: LiveData<Long> = reflectionDao.getAvgMeditation()

    // Gets records of the meditations of the week.
    var allMeditations : LiveData<List<Meditation>> = reflectionDao.getSortedMeditations(
        localDateToLong(LocalDate.now().minusDays(6)) as Long,
        localDateToLong(LocalDate.now()) as Long
    )

    // Creates an initial record of meditation on current day.
    var meditatedOnDay: List<LiveData<Long>> = listOf(reflectionDao.getCountMeditationsOnDate(localDateToLong(LocalDate.now()) as Long))

    init {
        // Then fills it such that it'll fit the ticker on the Meditation Fragment.
        for (i in 1..4)
        {
            meditatedOnDay = meditatedOnDay + reflectionDao.getCountMeditationsOnDate(localDateToLong(LocalDate.now().minusDays(i.toLong())) as Long)
        }
    }

    suspend fun insertReflection(reflection: Reflection) {
        reflectionDao.insertReflection(reflection)
    }

    suspend fun insertMeditation(meditation: Meditation) {
        reflectionDao.insertMeditation(meditation)
    }


    // Requires both variables, because the endTime must be changed for the LiveView to update,
    // as this is what the transformation map is set to.
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
