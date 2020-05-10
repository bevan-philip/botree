package com.bphilip.botree

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class AlarmBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        // Receives the broadcast, and starts the showNotification functionality.
        Log.i("AlarmBroadcastReceiver", "Broadcast received.")
        showNotification(context as Context)
    }

    private fun showNotification(context: Context) {
        // Ensures the alarm is setup for next time (as it is a daily notification).
        val meditationAlarm = MeditationAlarm()
        meditationAlarm.startAlarmBroadcastReceiver(context)

        // Pulls the relevant details from the strings file.
        val channelID: String = context.resources.getString(R.string.meditation_channel_id)
        val channelName: CharSequence = context.resources.getString(R.string.meditation_channel_title)
        val notificationTitle = context.resources.getString(R.string.meditation_notification_title)

        // Builds the notification.
        val mBuilder: NotificationCompat.Builder
        // Creates the intent (we want users to go to the MainActivity).
        val notificationIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val contentIntent: PendingIntent =
            PendingIntent.getActivity(context, 0, notificationIntent, 0)

        // Notifications behave differently across Android versions (mainly, older Android versions
        // do not have notification channels). We ensure the notification works regardless of
        // SDK version (other than versions below our minimum, of course).
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Gets the notification system service.
            val mNotificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            // Creates the Notification Channel.
            val mChannel: NotificationChannel =
                NotificationChannel(channelID, channelName, NotificationManager.IMPORTANCE_DEFAULT).apply {
                    description = context.resources.getString(R.string.meditation_channel_description)
                }
            mNotificationManager.createNotificationChannel(mChannel)
        }

        mBuilder = NotificationCompat.Builder(context, channelID)
            .setSmallIcon(R.drawable.ic_notification_icon)
            .setLights(Color.RED, 300, 300)
            .setContentText(context.resources.getString(R.string.meditation_notification_description))
            .setContentTitle(notificationTitle)
            // Priority is set differently in older notifications as it isn't channel based.
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(contentIntent)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(context)) {
            notify(1, mBuilder.build())
        }
    }
}
