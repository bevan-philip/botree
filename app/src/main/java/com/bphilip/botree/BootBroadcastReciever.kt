package com.bphilip.botree

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class BootBroadcastReciever : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == "android.intent.action.BOOT_COMPLETED" || intent.action == "android.intent.action.QUICKBOOT_POWERON") {
            val meditationAlarm = MeditationAlarm()
            meditationAlarm.startAlarmBroadcastReceiver(context)
            Log.i("BootBroadcastReceiver", "Restarting alarm")
        }
    }
}
