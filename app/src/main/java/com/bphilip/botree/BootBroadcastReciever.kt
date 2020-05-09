package com.bphilip.botree

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class BootBroadcastReciever : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == "android.intent.action.BOOT_COMPLETED") {
            val meditationAlarm = MeditationAlarm()
            meditationAlarm.startAlarmBroadcastReceiver(context)
        }
    }
}
