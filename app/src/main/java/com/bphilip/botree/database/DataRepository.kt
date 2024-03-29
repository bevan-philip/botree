package com.bphilip.botree.database

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDateTime

/**
 * DataRepository
 * Holds all the data that is used throughout the application.
 * Isn't much use in this application, as we only have one data source (the Room database),
 * but adds some element of future proofing.
 */
class DataRepository(private val reflectionDao: ReflectionDao, private val meditationDao: MeditationDao) {

    // Use the TypeConverters used for the database.
    private val converters: Converters =
        Converters()

    private val reflectionsStartTime : MutableLiveData<LocalDateTime> = MutableLiveData()
    private val reflectionsEndTime : MutableLiveData<LocalDateTime> = MutableLiveData()

    private val meditationsStartTime : MutableLiveData<LocalDate> = MutableLiveData()
    private val meditationsEndTime : MutableLiveData<LocalDate> = MutableLiveData()


    // Transformation.switchMap does some magic, such that when endTime is changed, it updates the
    // query used for the LiveData, enabling different results to be delivered. Useful for having
    // changeable data durations.
    var allReflections: LiveData<List<Reflection>> = Transformations.switchMap(reflectionsEndTime) {
        reflectionDao.getSortedReflections(
            converters.LocalDateTimeToTimestamp(reflectionsStartTime.value) as Long,
            converters.LocalDateTimeToTimestamp(reflectionsEndTime.value) as Long
        )
    }

    // Gets the average meditation for the duration specified.
    var averageMeditation: LiveData<Long> = Transformations.switchMap(meditationsEndTime) {
        meditationDao.getAvgMeditation(
            converters.LocalDateToTimestamp(meditationsStartTime.value) as Long,
            converters.LocalDateToTimestamp(meditationsEndTime.value) as Long
        )
    }

    // Gets records of the meditations for the duration specified.
    var allMeditations : LiveData<List<Meditation>> = Transformations.switchMap(meditationsEndTime) {
        meditationDao.getSortedMeditations(
            converters.LocalDateToTimestamp(meditationsStartTime.value) as Long,
            converters.LocalDateToTimestamp(meditationsEndTime.value) as Long
        )
    }

    // Creates an initial record of meditation on current day.
    var meditatedOnDay: List<LiveData<Long>> = listOf(meditationDao.getCountMeditationsOnDate(converters.LocalDateToTimestamp(LocalDate.now()) as Long))

    init {
        // Then fills it such that it'll fit the ticker on the Meditation Fragment.
        for (i in 1..4)
        {
            meditatedOnDay = meditatedOnDay + meditationDao.getCountMeditationsOnDate(converters.LocalDateToTimestamp(LocalDate.now().minusDays(i.toLong())) as Long)
        }
    }

    fun insertReflection(reflection: Reflection) {
        reflectionDao.insertReflection(reflection)
    }

    fun insertMeditation(meditation: Meditation) {
        meditationDao.insertMeditation(meditation)
    }

    // Requires both variables, because the endTime must be changed for the LiveView to update,
    // as this is what the transformation map is set to.
    fun changeTimeReflections(stime : LocalDateTime, etime: LocalDateTime) {
        reflectionsStartTime.value = stime
        reflectionsEndTime.value = etime
    }

    // Requires both variables, because the endTime must be changed for the LiveView to update,
    // as this is what the transformation map is set to.
    fun changeTimeMeditations(stime : LocalDate, etime: LocalDate) {
        meditationsStartTime.value = stime
        meditationsEndTime.value = etime
    }
}