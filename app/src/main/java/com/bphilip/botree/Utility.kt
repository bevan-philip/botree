package com.bphilip.botree

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


}