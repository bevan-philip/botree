package com.bphilip.botree

import android.app.Application
import android.content.Context
import android.os.Build
import androidx.test.core.app.ApplicationProvider
import com.bphilip.botree.ui.meditation.MeditationViewModel
import com.google.common.truth.Truth.assertThat
import com.jakewharton.threetenabp.AndroidThreeTen
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneId
import org.threeten.bp.ZonedDateTime
import org.threeten.bp.format.DateTimeFormatter

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.N])
class UtilityTest {
    lateinit var instrumentationContext: Context

    private lateinit var z: ZonedDateTime
    private lateinit var reflection: Reflection
    private lateinit var meditation: Meditation

    @Before
    fun setup() {
        instrumentationContext = ApplicationProvider.getApplicationContext<Context>()
        AndroidThreeTen.init(instrumentationContext)

        z = ZonedDateTime.of(
            2020, 1, 1,
            9, 30, 1, 100000000,
            ZoneId.systemDefault()
        )
        reflection = Reflection(0, "Hello", LocalDateTime.from(z))

        meditation = Meditation(0, 600000, LocalDate.from(z))
    }

    @Test
    fun utility_timeFormatter_ReturnsCorrect() {
        val timeInMillis : Long = 600000
        val timeInMillisFormatted = Utility.timeFormatter(timeInMillis, instrumentationContext)

        assertThat(timeInMillisFormatted).isEqualTo("10:00")
    }

    @Test
    fun utility_startOfWeek_ReturnsSunday() {
        // Note: within the testing environment, it *should* return Sunday. However, this is
        // dependent on timezone info, so on an actual device it may be different.
        val startOfWeek = Utility.startOfWeek()
        val textual = startOfWeek.format(DateTimeFormatter.ofPattern("EEEE"))

        assertThat(textual).isEqualTo("Sunday")
    }

    @Test
    fun utility_endOfWeek_ReturnsSaturday() {
        // Note: within the testing environment, it *should* return Saturday. However, this is
        // dependent on timezone info, so on an actual device it may be different.
        val endOfWeek = Utility.endOfWeek()
        val textual = endOfWeek.format(DateTimeFormatter.ofPattern("EEEE"))

        assertThat(textual).isEqualTo("Saturday")
    }

    @Test
    fun reflections_exportHeader_IsExpected() {
        val header = reflection.csvHeader(reflection)
        assertThat(header).isEqualTo("id, reflection, date\n")
    }

    @Test
    fun reflections_exportBody_IsExpected() {
        val body = reflection.csvBody(reflection)
        assertThat(body).isEqualTo("0, Hello, 2020-01-01T09:30:01.100\n")
    }

    @Test
    fun meditation_exportHeader_IsExpected() {
        val header = meditation.csvHeader(meditation)
        assertThat(header).isEqualTo("id, duration, date\n")
    }

    @Test
    fun meditation_exportBody_IsExpected() {
        val body = meditation.csvBody(meditation)
        assertThat(body).isEqualTo("0, 600000, 2020-01-01\n")
    }
}

