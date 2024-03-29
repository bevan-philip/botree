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
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProviders
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat
import com.bphilip.botree.database.Exportable
import com.bphilip.botree.MeditationAlarm
import com.bphilip.botree.R
import java.io.File
import java.io.FileWriter


class SettingsFragment : PreferenceFragmentCompat() {

    private lateinit var sharedPreferences: SharedPreferences
    private val requestGetTime = 1
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
            TimePickerFragment().show(this.fragmentManager as FragmentManager, "timePicker")
            true
        }

        // When the user clicks on the notification enable, run the broadcast receiver, which'll
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


        /**
         * Export functions.
         * They first check that the permissions exist, and if so, export the CSV of said category
         * to a CSV, and creates a share prompt.
         */
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
                shareCSV(writeCSV(settingsViewModel.alLReflections, getString(R.string.export_reflections_file_name)))
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
                shareCSV(writeCSV(settingsViewModel.allMeditations, getString(R.string.export_meditation_file_name)))

            }
            true
        }

    }

    /**
     * onActivityResult()
     * We use this to get invalidate the turning on of the notification if no time has been
     * set in the time picker.
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // If the user hasn't setup a time, then we have no indication of when to deliver the
        // the notification, so we disable it.
        if (resultCode == Activity.RESULT_CANCELED) {
            if (requestCode == requestGetTime) {
                if (!sharedPreferences.contains(getString(R.string.saved_notification_hour_key))) {
                    findPreference<SwitchPreferenceCompat>(getString(R.string.preference_notification_enable_key))?.isChecked =
                        false
                    Toast.makeText(context, getString(R.string.preference_notification_no_time_set), Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    /**
     * shareCSV()
     * Creates the share prompt, and shares the file.
     */
    private fun shareCSV(path: String) {
        // Creates the URI from the File Provider, so that other applications can access the file.
        val uri = FileProvider.getUriForFile(
            context!!,
            "com.bphilip.botree.fileprovider",
            File(path)
        )

        // Creates the share prompt.
        Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_STREAM, uri)
            type = "text/csv"
        }.also {intent ->
            startActivity(Intent.createChooser(intent, getString(R.string.share_csv)))
        }

        // Starts the default Android share prompt.
    }

    /**
     * writeCSV(exportableRecords: List<Exportable>, fileName: String)
     * Writes a list of exportable records to a CSV.
     */
    private fun writeCSV(exportableRecords: List<Exportable>, fileName: String) : String {
        // Finds the internal path.
        var path = context?.getExternalFilesDir(null)?.absolutePath as String + getString(R.string.export_folder)
        File(path).mkdirs()

        path += "/$fileName"
        val fileWriter = FileWriter(path)

        // Writes all the records to file, ensuring that the file has a header.
        var header = true
        for (record in exportableRecords) {
            if (header) {
                fileWriter.append(record.csvHeader(record))
                header = false
            }
            fileWriter.append(record.csvBody(record))
        }

        // Ensure the file has been properly closed.
        fileWriter.flush()
        fileWriter.close()

        Log.i("SettingsFragment", "Written CSV.")

        return path
    }

    /**
     * onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray)
     * Whenever we get the result of the permission request, try to save the CSV (if it is a success).
     */
    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            reflectionPermissionRequestCode -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                     shareCSV(writeCSV(settingsViewModel.alLReflections, getString(R.string.export_reflections_file_name)))
                }
                return
            }
            meditationPermissionRequestCode -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    shareCSV(writeCSV(settingsViewModel.allMeditations, getString(R.string.export_meditation_file_name)))
                }
                return
            }
        }
    }

}
