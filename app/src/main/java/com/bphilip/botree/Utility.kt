package com.bphilip.botree

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.bphilip.botree.database.Reflection
import com.bphilip.botree.ui.reflections.NewReflectionActivity
import com.bphilip.botree.ui.reflections.ReflectionsViewModel
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDateTime
import org.threeten.bp.temporal.WeekFields
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * Basic utility class to reduce code repetition.
 */
object Utility {

    /**
     * timeFormatter(timeInMillis, context)
     * Format the time according to the app wide formatting.
     */
     fun timeFormatter (timeInMillis : Long, ctx : Context) : String {
         return ctx.getString(R.string.time_format,
            TimeUnit.MILLISECONDS.toMinutes(timeInMillis),
            TimeUnit.MILLISECONDS.toSeconds(timeInMillis) -
                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(timeInMillis))
        )
    }

    /**
     * createReflectionFromIntent(requestCode, resultCode, data, ctx, reflectionsViewModel)
     * Store a reflection given a intent.
     */
    fun createReflectionFromIntent(
        requestCode: Int,
        resultCode: Int,
        data: Intent?,
        ctx : Context,
        reflectionsViewModel : ReflectionsViewModel) {
        if (requestCode == ctx.resources.getInteger(R.integer.newWordActivityRequestCode) && resultCode == Activity.RESULT_OK) {
            // Takes the reflection, gets the data and stores it in the database.
            data?.getStringExtra(NewReflectionActivity.EXTRA_REPLY)?.let {
                val reflection = Reflection(
                    0,
                    it,
                    LocalDateTime.now()
                )
                reflectionsViewModel.insert(reflection)
            }
        // Create Toasts depending on outcome.
            Toast.makeText(
                ctx,
                R.string.reflection_saved,
                Toast.LENGTH_LONG).show()
        }
        else {
            Toast.makeText(
                ctx,
                R.string.reflection_not_saved,
                Toast.LENGTH_LONG).show()
        }
    }

    /**
     * startOfWeek()
     * Return the start of the week for the current time.
     */
    fun startOfWeek(): LocalDate {
        return LocalDate.now().with(WeekFields.of(Locale.getDefault()).dayOfWeek(), 1)
    }

    /**
     * endOfWeek()
     * Return the end of the week for the current time.
     */
    fun endOfWeek(): LocalDate {
        return LocalDate.now().with(WeekFields.of(Locale.getDefault()).dayOfWeek(), 1).plusDays(6)
    }
}