package com.bphilip.botree.ui.settings

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProviders
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat
import com.bphilip.botree.MeditationAlarm
import com.bphilip.botree.R
import com.bphilip.botree.ui.meditation.SettingsViewModel
import java.io.File
import java.io.FileWriter


class SettingsFragment : PreferenceFragmentCompat() {

    private lateinit var sharedPreferences: SharedPreferences
    private val REQUEST_GET_TIME = 1
    private val reflectionPermissionRequestCode = 1
    private val meditationPermissionRequestCode = 2
    private lateinit var settingsViewModel: SettingsViewModel
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {

        settingsViewModel =
            ViewModelProviders.of(this.activity as FragmentActivity).get(SettingsViewModel::class.java)

        setPreferencesFromResource(R.xml.preferences, rootKey)

        val timePicker = TimePickerFragment()
        // Ensures the timePicker returns onActivityResult to this fragment.
        timePicker.setTargetFragment(this, Activity.RESULT_CANCELED)

        sharedPreferences = context!!.getSharedPreferences(
            getString(R.string.preference_file_key), Context.MODE_PRIVATE)


        // When the user clicks on the time changer, show the time change prompt.
        findPreference<Preference>(getString(R.string.time_change_key))?.setOnPreferenceClickListener {
            TimePickerFragment()    .show(this.fragmentManager as FragmentManager, "timePicker")
            true
        }

        // When the user clicks on the notifcation enable, run the broadcast receiver, which'll
        // automatically disable the alarm if the user selects as such. If there is no time set,
        // request the time from the user.
        findPreference<Preference>(getString(R.string.preference_notification_enable_key))?.setOnPreferenceClickListener {
            if (!sharedPreferences.contains(getString(R.string.saved_notification_hour_key))) {
                timePicker.show(this.fragmentManager as FragmentManager, "timePicker")

            }
            else {
                MeditationAlarm().startAlarmBroadcastReceiver(context as Context)
            }
            true
        }

        findPreference<Preference>(getString(R.string.preference_export_reflections_key))?.setOnPreferenceClickListener {
            if (ContextCompat.checkSelfPermission(
                    context as Context,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    reflectionPermissionRequestCode
                )
            }
            else {
                writeCSV(isReflection = true, isMeditation = false, fileName = "reflections.csv")
            }
            true
        }

        findPreference<Preference>(getString(R.string.preference_export_meditations_key))?.setOnPreferenceClickListener {
            if (ContextCompat.checkSelfPermission(
                    context as Context,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                requestPermissions(
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    meditationPermissionRequestCode)

            }
            else {
                writeCSV(isReflection = false, isMeditation = true, fileName = "meditations.csv")

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
                    findPreference<SwitchPreferenceCompat>(getString(R.string.preference_notification_enable_key))?.isChecked =
                        false
                    Toast.makeText(context, getString(R.string.preference_notification_no_time_set), Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun shareCSV(path: String) {
        val uri = FileProvider.getUriForFile(
            context!!,
            "com.bphilip.botree.fileprovider",
            File(path)
        )

        val shareIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_STREAM, uri)
            type = "text/csv"
        }

        startActivity(Intent.createChooser(shareIntent, getString(R.string.share_csv)))
    }

    private fun writeCSV(isReflection: Boolean, isMeditation: Boolean, fileName: String) {
        var path = context?.getExternalFilesDir(null)?.absolutePath as String + "/csv"
        File(path).mkdirs()

        path += "/$fileName"
        val fileWriter = FileWriter(path)

        if (isReflection) {
            fileWriter.append("id, reflection, date\n")
            for (reflection in settingsViewModel.alLReflections) {
                fileWriter.append(reflection.asCSV())
            }
        }

        if (isMeditation) {
            fileWriter.append("id, duration, date\n")
            for (meditation in settingsViewModel.allMeditations) {
                fileWriter.append(meditation.asCSV())
            }
        }

        fileWriter.flush()
        fileWriter.close()

        Log.i("SettingsFragment", "Written CSV.")
        shareCSV(path)
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        Log.i("SettingsFragment", "onRequestPermissionsResult: $requestCode")
        when (requestCode) {
            reflectionPermissionRequestCode -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    writeCSV(isReflection = true, isMeditation = false, fileName = "reflections.csv")
                }
                return
            }
            meditationPermissionRequestCode -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    writeCSV(isReflection = false, isMeditation = true, fileName = "meditations.csv")
                }
                return
            }
        }
    }

}
