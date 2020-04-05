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

    fun showNotification(context: Context?) {
        var meditationAlarm = MeditationAlarm()
        meditationAlarm.startAlarmBroadcastReceiver(context as Context)

        var channel_id: String = "test_channel"
        var name: CharSequence = context?.resources?.getString(R.string.app_name) as String
        var mBuilder: NotificationCompat.Builder
        var notificationIntent = Intent(context, MainActivity::class.java)
        var bundle = Bundle()

        notificationIntent.putExtras(bundle)
//        notificationIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_MULTIPLE_TASK
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

        var contentIntent: PendingIntent =
            PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        var mNotificationManager: NotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            var mChannel: NotificationChannel =
                NotificationChannel(channel_id, name, NotificationManager.IMPORTANCE_DEFAULT)
            mNotificationManager.createNotificationChannel(mChannel)
            mBuilder = NotificationCompat.Builder(context, channel_id)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLights(Color.RED, 300, 300)
                .setChannelId(channel_id)
                .setContentTitle("Title")
        }
        else {
            mBuilder = NotificationCompat.Builder(context, channel_id)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLights(Color.RED, 300, 300)
                .setPriority(NotificationManager.IMPORTANCE_DEFAULT)
                .setContentTitle("Title")
        }

        mBuilder.setContentIntent(contentIntent)
        mBuilder.setContentText("test")
        mBuilder.setAutoCancel(true)
        mNotificationManager.notify(1, mBuilder.build())


    }
}
