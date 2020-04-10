package com.bphilip.botree.ui.settings

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import androidx.preference.*
import com.bphilip.botree.MeditationAlarm
import com.bphilip.botree.R

class SettingsFragment : PreferenceFragmentCompat() {

    private lateinit var sharedPreferences: SharedPreferences
    private val REQUEST_GET_TIME = 1
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {

        setPreferencesFromResource(R.xml.preferences, rootKey)

        val timePicker : TimePickerFragment =
            TimePickerFragment()
        // Ensures the timePicker returns onActivityResult to this fragment.
        timePicker.setTargetFragment(this, Activity.RESULT_CANCELED)

        sharedPreferences = context!!.getSharedPreferences(
            getString(R.string.preference_file_key), Context.MODE_PRIVATE)


        // When the user clicks on the time changer, show the time change prompt.
        findPreference<Preference>(getString(R.string.time_change_key))?.setOnPreferenceClickListener {
            TimePickerFragment()
                .show(this.fragmentManager as FragmentManager, "timePicker")
            true
        }

        // When the user clicks on the notifcation enable, run the broadcast receiver, which'll
        // automatically disable the alarm if the user selects as such. If there is no time set,
        // request the time from the user.
        findPreference<Preference>(getString(R.string.notification_enable_key))?.setOnPreferenceClickListener {
            if (!sharedPreferences.contains(getString(R.string.saved_notification_hour_key))) {
                timePicker.show(this.fragmentManager as FragmentManager, "timePicker")

            }
            else {
                MeditationAlarm().startAlarmBroadcastReceiver(context as Context)
            }
            true
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // If the user hasn't setup a time, then we have no indication of when to deliver the
        // the notification, so we disable it.
        if (resultCode == Activity.RESULT_CANCELED) {
            if (requestCode == REQUEST_GET_TIME) {
                if (!sharedPreferences.contains(getString(R.string.saved_notification_hour_key))) {
                    findPreference<SwitchPreferenceCompat>(getString(R.string.notification_enable_key))?.isChecked =
                        false
                    Toast.makeText(context, getString(R.string.preference_notification_no_time_set), Toast.LENGTH_LONG).show()
                }
            }
        }
    }



}
