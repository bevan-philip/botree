package com.bphilip.botree

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.jakewharton.threetenabp.AndroidThreeTen
import org.hamcrest.CoreMatchers.equalTo
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDateTime
import java.io.IOException


/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class InstrumentedTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    // Use the TypeConverters used for the database.
    private val converters: Converters = Converters()

    private lateinit var reflectionDao: ReflectionDao
    private lateinit var meditationDao: MeditationDao
    private lateinit var db: ReflectionRoomDatabase

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context, ReflectionRoomDatabase::class.java)
            // Allowing main thread queries, just for testing.
            .allowMainThreadQueries()
            .build()
        reflectionDao = db.reflectionDao()
        meditationDao = db.meditationDao()
        AndroidThreeTen.init(context)

    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.bphilip.botree", appContext.packageName)
    }


    @Test
    @Throws(Exception::class)
    fun insertReflectionAndTestWritten() {
        reflectionDao.insertReflection(Reflection(0, "Test value", LocalDateTime.now()))

        val reflection = reflectionDao.getAllReflections()[0]
        assertThat(reflection.reflection, equalTo("Test value"))
    }

    @Test
    @Throws(Exception::class)
    fun insertMeditationAndTestWritten() {
        val duration: Long = 60000
        meditationDao.insertMeditation(Meditation(0, duration, LocalDate.now()))

        val meditation = meditationDao.getAllMeditations()[0]
        assertThat(meditation.duration, equalTo(duration))
    }

    @Test
    @Throws(Exception::class)
    fun checkAverageMeditation() {
        val duration: Long = 60000
        meditationDao.insertMeditation(Meditation(0, duration, LocalDate.now()))

        val average = meditationDao.getAvgMeditation(
            converters.LocalDateToTimestamp(Utility.startOfWeek()) as Long,
            converters.LocalDateToTimestamp(Utility.endOfWeek()) as Long
        ).waitForValue()

        assertThat(average, equalTo(duration))
    }

    @Test
    @Throws(Exception::class)
    fun checkMeditationsOnDate() {
        val meditations: Long = 5
        val duration: Long = 120000

        for (i in 1 .. meditations) {
            meditationDao.insertMeditation(Meditation(0, duration, LocalDate.now()))
        }

        val meditationsOnDate = meditationDao.getCountMeditationsOnDate(
            converters.LocalDateToTimestamp(LocalDate.now()) as Long
        ).waitForValue()

        assertThat(meditationsOnDate, equalTo(meditations))
    }

    @Test
    @Throws(Exception::class)
    fun checkSortedMeditations() {
        val meditations: Long = 2
        val today = LocalDate.now()

        for (i in 0 .. meditations) {
            meditationDao.insertMeditation(Meditation(0, 60000, today.plusDays(i)))
        }

        val sortedMeditations = meditationDao.getSortedMeditations(
            converters.LocalDateToTimestamp(today) as Long,
            converters.LocalDateToTimestamp(today.plusDays(meditations)) as Long
        ).waitForValue()

        assertThat(sortedMeditations[0].date, equalTo(today.plusDays(meditations)))
    }

    @Test
    @Throws(Exception::class)
    fun checkSortedReflections() {
        val reflections: Long = 2
        val today = LocalDateTime.now()

        for (i in 0.. reflections) {
            reflectionDao.insertReflection(Reflection(0, "Reflection", today.plusDays(i)))
        }

        val sortedReflections = reflectionDao.getSortedReflections(
            converters.LocalDateTimeToTimestamp(today) as Long,
            converters.LocalDateTimeToTimestamp(today.plusDays(reflections)) as Long
        ).waitForValue()

        assertThat(sortedReflections[0].date, equalTo(today.plusDays(reflections)))
    }
}
