package com.bphilip.botree

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.bphilip.botree.ui.reflections.NewWordActivity
import com.bphilip.botree.ui.reflections.ReflectionsViewModel
import org.threeten.bp.LocalDateTime
import java.util.concurrent.TimeUnit

object Utility {
     fun timeFormatter (timeInMillis : Long, ctx : Context) : String {
         return ctx.getString(R.string.time_format,
            TimeUnit.MILLISECONDS.toMinutes(timeInMillis),
            TimeUnit.MILLISECONDS.toSeconds(timeInMillis) -
                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(timeInMillis))


        )
    }

    fun createReflectionFromIntent(
        requestCode: Int,
        resultCode: Int,
        data: Intent?,
        ctx : Context,
        reflectionsViewModel : ReflectionsViewModel) {
        if (requestCode == ctx.resources.getInteger(R.integer.newWordActivityRequestCode) && resultCode == Activity.RESULT_OK) {
            data?.getStringExtra(NewWordActivity.EXTRA_REPLY)?.let {
                val word = Reflection(0, it, LocalDateTime.now())
                reflectionsViewModel.insert(word)
            }
            Toast.makeText(
                ctx,
                R.string.saved,
                Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(
                ctx,
                R.string.not_saved,
                Toast.LENGTH_LONG).show()
        }
    }


}