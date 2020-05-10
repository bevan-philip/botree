package com.bphilip.botree

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.AlarmManagerCompat
import androidx.preference.PreferenceManager
import java.text.SimpleDateFormat
import java.util.*

class MeditationAlarm {
    fun startAlarmBroadcastReceiver(context: Context) {
        // Creates the intent to send to AlarmBroadcastReceiver.
        val intent: Intent = Intent(context, AlarmBroadcastReceiver::class.java)
        // Creates the PendingIntent, and ensures it updates any existing PendingIntent.
        val pendingIntent: PendingIntent = PendingIntent.getBroadcast(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        // Gets the alarm manager
        val alarmManager: AlarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        // Gets all the SharedPreferences instances (the one used by the preference fragment, and
        // our own store.
        val sharedPreferences: SharedPreferences = context.getSharedPreferences(
            context.getString(R.string.preference_file_key), Context.MODE_PRIVATE)
        val defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

        // If notifications are enabled, we'll configure it.
        if (defaultSharedPreferences.getBoolean(context.getString(R.string.preference_notification_enable_key), false)) {
            val calendar: Calendar = Calendar.getInstance()
            // Ensures the Calendar has most of the date elements already created.
            calendar.timeInMillis = System.currentTimeMillis()

            // Pulls the set hour and minutes from the shared preferences.
            calendar.set(Calendar.HOUR_OF_DAY, sharedPreferences.getInt(context.getString(R.string.saved_notification_hour_key), 9))
            calendar.set(Calendar.MINUTE, sharedPreferences.getInt(context.getString(R.string.saved_notification_minutes_key), 0))
            calendar.set(Calendar.SECOND, 0)

            // If it is trying to set the alarm to the past, clearly we're a day out.
            if (calendar.timeInMillis < System.currentTimeMillis()) {
                calendar.add(Calendar.DATE, 1)
            }

            // AlarmManagerCompat gracefully degrades across Android versions. Have to use
            // setExactAndAllowWhileIdle to have reliable notifications, due to increasing
            // restrictions on power use in Android versions.
            AlarmManagerCompat.setExactAndAllowWhileIdle(
                alarmManager,
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                pendingIntent
            )

            // Enable the boot receiver, so alarms can persist across reboots.
            val receiver = ComponentName(context, BootBroadcastReciever::class.java)

            context.packageManager.setComponentEnabledSetting(
                receiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP
            )


            Log.i("MeditationAlarm", "Alarm set for " + SimpleDateFormat("HH:mm").format(calendar.time) + " " + SimpleDateFormat("d/M").format(calendar.time))
        }
        else {
            // Else cancel the alarm.
            Log.i("MeditationAlarm", "Alarm cancelled.")
            alarmManager.cancel(pendingIntent)

            // Disable boot receiver if alarm is not enabled. Don't waste resources :)
            val receiver = ComponentName(context, BootBroadcastReciever::class.java)

            context.packageManager.setComponentEnabledSetting(
                receiver,
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP
            )

        }
    }
}
