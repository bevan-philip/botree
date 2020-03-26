package com.bphilip.botree

import android.app.PendingIntent.getActivity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.bphilip.botree.ui.meditation.MeditationFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

const val EXTRA_TIMER = "com.bphilip.botree.TIMER"

class MainActivity : AppCompatActivity(), MeditationFragment.OnTimerStart {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val navView: BottomNavigationView = findViewById(R.id.nav_view)

        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

    }

    fun startTimer(view: View, duration : Long) {
        val intent = Intent(this, Timer::class.java).apply {
            putExtra(EXTRA_TIMER, duration)
        }

        startActivity(intent)
    }

    override fun onTimerStart(timer : Long, v : View) {
        startTimer(v, timer)
    }


}
