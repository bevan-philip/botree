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
        Log.i("AlarmBroadcastReceiver", "Broadcast received.")
        showNotification(context)
    }

    private fun showNotification(context: Context?) {
        val meditationAlarm = MeditationAlarm()
        meditationAlarm.startAlarmBroadcastReceiver(context as Context)

        val channelID: String = context.resources.getString(R.string.meditation_channel_id)
        val channelName: CharSequence = context.resources.getString(R.string.meditation_channel_title)
        val notificationTitle = context.resources.getString(R.string.meditation_notification_title)
        val mBuilder: NotificationCompat.Builder
        val notificationIntent = Intent(context, MainActivity::class.java)
        val bundle = Bundle()

        notificationIntent.putExtras(bundle)
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

        val contentIntent: PendingIntent =
            PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        val mNotificationManager: NotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val mChannel: NotificationChannel =
                NotificationChannel(channelID, channelName, NotificationManager.IMPORTANCE_DEFAULT)
            mNotificationManager.createNotificationChannel(mChannel)
            mBuilder = NotificationCompat.Builder(context, channelID)
                .setSmallIcon(R.mipmap.ic_botree_icon)
                .setLights(Color.RED, 300, 300)
                .setChannelId(channelID)
                .setContentTitle(notificationTitle)
        }
        else {
            mBuilder = NotificationCompat.Builder(context, channelID)
                .setSmallIcon(R.mipmap.ic_botree_icon)
                .setLights(Color.RED, 300, 300)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentTitle(notificationTitle)
        }

        mBuilder.setContentIntent(contentIntent)
        mBuilder.setContentText(context.resources.getString(R.string.meditation_notification_description))
        mBuilder.setAutoCancel(true)
        mNotificationManager.notify(1, mBuilder.build())


    }
}
