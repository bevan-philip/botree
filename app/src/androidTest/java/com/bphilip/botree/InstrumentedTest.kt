package com.bphilip.botree

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.bphilip.botree.ui.reflections.ReflectionsViewModel
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

    @Mock
    private lateinit var repository: DataRepository

    private lateinit var reflectionDao: ReflectionDao
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
    fun writeUserAndReadInList() {
        reflectionDao.insertReflection(Reflection(0, "Test value", LocalDateTime.now()))

        val byName = reflectionDao.getReflectionById(1)
        assertThat(byName.reflection, equalTo("Test value"))
    }

}
