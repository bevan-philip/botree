package com.bphilip.botree

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.bphilip.botree.ui.reflections.ReflectionsViewModel
import org.threeten.bp.LocalDateTime
import java.util.concurrent.TimeUnit


object Utility {
     fun timeFormatter (timeInMillis : Long) : String {
        return String.format("%02d:%02d",
            TimeUnit.MILLISECONDS.toMinutes(timeInMillis),
            TimeUnit.MILLISECONDS.toSeconds(timeInMillis) -
                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(timeInMillis))
        )
    }

    fun timeToMinutes(timeInMillis : Long) : String {
        return String.format("%02d", TimeUnit.MILLISECONDS.toMinutes(timeInMillis))
    }

    fun timeToSecondsWithoutMinutes(timeInMillis: Long): String {
        return String.format("%02d", TimeUnit.MILLISECONDS.toSeconds(timeInMillis) -
                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(timeInMillis)))
    }

    fun createReflectionFromIntent(
        requestCode: Int,
        resultCode: Int,
        data: Intent?,
        applicationContext : Context,
        reflectionsViewModel : ReflectionsViewModel) {
        if (requestCode == applicationContext.resources.getInteger(R.integer.newWordActivityRequestCode) && resultCode == Activity.RESULT_OK) {
            data?.getStringExtra(NewWordActivity.EXTRA_REPLY)?.let {
                val word = Reflection(0, it, LocalDateTime.now())
                reflectionsViewModel.insert(word)
            }
        } else {
            Toast.makeText(
                applicationContext,
                R.string.empty_not_saved,
                Toast.LENGTH_LONG).show()
        }
    }


}