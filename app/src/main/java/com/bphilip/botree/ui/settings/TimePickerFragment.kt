package com.bphilip.botree.ui.settings

import android.app.Activity
import android.app.Dialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.format.DateFormat
import android.widget.TimePicker
import androidx.fragment.app.DialogFragment
import com.bphilip.botree.MeditationAlarm
import com.bphilip.botree.R
import java.util.*


class TimePickerFragment : DialogFragment(), TimePickerDialog.OnTimeSetListener {

    private lateinit var sharedPref: SharedPreferences

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        sharedPref = activity!!.getSharedPreferences(
            getString(R.string.preference_file_key), Context.MODE_PRIVATE)

        // Get the saved values for the time from SharedPreferences, otherwise have some defaults
        // from the calendar.
        val c = Calendar.getInstance()
        val hour = sharedPref.getInt(getString(R.string.saved_notification_hour_key), c.get(Calendar.HOUR_OF_DAY))
        val minute = sharedPref.getInt(getString(R.string.saved_notification_minutes_key), c.get(Calendar.MINUTE))

        // Create a new instance of TimePickerDialog and return it
        return TimePickerDialog(activity, this, hour, minute, DateFormat.is24HourFormat(activity))
    }

    override fun onTimeSet(view: TimePicker, hourOfDay: Int, minute: Int) {
        // If time selected, change the stored time.
        with (sharedPref.edit()) {
            putInt(getString(R.string.saved_notification_hour_key), hourOfDay)
            putInt(getString(R.string.saved_notification_minutes_key), minute)
            apply()
        }

        // Ensure the alarm is set.
        MeditationAlarm().startAlarmBroadcastReceiver(context as Context)

    }

    override fun onCancel(dialog: DialogInterface) {
        // If cancelled, send back the request cancelled code.
        super.onCancel(dialog)
        val replyIntent = Intent()
        targetFragment?.onActivityResult(1, Activity.RESULT_CANCELED, replyIntent)
    }


}
