package com.bphilip.botree

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.util.Log
import androidx.core.app.AlarmManagerCompat
import androidx.preference.PreferenceManager
import java.text.SimpleDateFormat
import java.util.*

class MeditationAlarm {
    fun startAlarmBroadcastReceiver(context: Context) {
        val _intent: Intent = Intent(context, AlarmBroadcastReceiver::class.java)
        val pendingIntent: PendingIntent = PendingIntent.getBroadcast(
            context,
            0,
            _intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        val alarmManager: AlarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val sharedPreferences: SharedPreferences = context.getSharedPreferences(
            context.getString(R.string.preference_file_key), Context.MODE_PRIVATE)
        val defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

        if (defaultSharedPreferences.getBoolean(context.getString(R.string.notification_enable_key), false)) {
            val calendar: Calendar = Calendar.getInstance()
            calendar.timeInMillis = System.currentTimeMillis()

            calendar.set(Calendar.HOUR_OF_DAY, sharedPreferences.getInt(context.getString(R.string.saved_notification_hour_key), 9))
            calendar.set(Calendar.MINUTE, sharedPreferences.getInt(context.getString(R.string.saved_notification_minutes_key), 0))
            calendar.set(Calendar.SECOND, 0)

            if (calendar.timeInMillis < System.currentTimeMillis()) {
                calendar.add(Calendar.DATE, 1)
            }

            AlarmManagerCompat.setExactAndAllowWhileIdle(
                alarmManager,
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                pendingIntent
            )

            Log.i("MeditationAlarm", "Alarm set for " + SimpleDateFormat("HH:mm").format(calendar.time))
        }
        else {
            Log.i("MeditationAlarm", "Alarm cancelled.")
            alarmManager.cancel(pendingIntent)
        }
    }
}
