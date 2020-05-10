package com.bphilip.botree

import android.content.Context
import android.os.Build
import androidx.test.core.app.ApplicationProvider
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.platform.app.InstrumentationRegistry
import com.google.common.truth.Truth.assertThat
import org.junit.Test
import org.junit.Assert.*
import org.junit.Before
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.N])
class UtilityTest {
    lateinit var instrumentationContext: Context

    @Before
    fun setup() {
        instrumentationContext = ApplicationProvider.getApplicationContext<Context>()
    }

    @Test
    fun utility_timeFormatter_ReturnsCorrect() {
        val timeInMillis : Long = 600000
        val timeInMillisFormatted = Utility.timeFormatter(timeInMillis, instrumentationContext)

        assertThat(timeInMillisFormatted).isEqualTo("10:00")

    }

}
