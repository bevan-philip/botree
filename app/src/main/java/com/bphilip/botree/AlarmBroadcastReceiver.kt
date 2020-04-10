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

class AlarmBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        // Receives the broadcast, and starts the showNotification functionality.
        Log.i("AlarmBroadcastReceiver", "Broadcast received.")
        showNotification(context)
    }

    private fun showNotification(context: Context?) {
        // Ensures the alarm is setup for next time (as it is a daily notification).
        val meditationAlarm = MeditationAlarm()
        meditationAlarm.startAlarmBroadcastReceiver(context as Context)

        // Pulls the relevant details from the strings file.
        val channelID: String = context.resources.getString(R.string.meditation_channel_id)
        val channelName: CharSequence = context.resources.getString(R.string.meditation_channel_title)
        val notificationTitle = context.resources.getString(R.string.meditation_notification_title)
        // Builds the notification.
        val mBuilder: NotificationCompat.Builder
        // Creates the intent (we want users to go to the MainActivity).
        val notificationIntent = Intent(context, MainActivity::class.java)
        val bundle = Bundle()

        notificationIntent.putExtras(bundle)
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        val contentIntent: PendingIntent =
            PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        // Gets the notification system service.
        val mNotificationManager: NotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Notifications behave differently across Android versions (mainly, older Android versions
        // do not have notification channels). We ensure the notification works regardless of
        // SDK version (other than versions below our minimum, of course).
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Creates the Notification Channel.
            val mChannel: NotificationChannel =
                NotificationChannel(channelID, channelName, NotificationManager.IMPORTANCE_DEFAULT)
            mNotificationManager.createNotificationChannel(mChannel)
            mBuilder = NotificationCompat.Builder(context, channelID)
                .setSmallIcon(R.drawable.ic_notification_icon)
                .setLights(Color.RED, 300, 300)
                .setChannelId(channelID)
                .setContentTitle(notificationTitle)
        }
        else {
            mBuilder = NotificationCompat.Builder(context, channelID)
                .setSmallIcon(R.drawable.ic_notification_icon)
                .setLights(Color.RED, 300, 300)
                // Priority is set differently in older notifications as it isn't channel based.
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentTitle(notificationTitle)
        }

        mBuilder.setContentIntent(contentIntent)
        // Sets the text.
        mBuilder.setContentText(context.resources.getString(R.string.meditation_notification_description))
        mBuilder.setAutoCancel(true)
        // Displays the notification.
        mNotificationManager.notify(1, mBuilder.build())


    }
}
