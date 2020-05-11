package com.bphilip.botree

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.jakewharton.threetenabp.AndroidThreeTen

// Used for timer responses by ViewModels.
const val EXTRA_TIMER = "com.bphilip.botree.TIMER"

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        // Boilerplate code from Android Studio. Creates the Bottom three navigation fragments.
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val navView: BottomNavigationView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment)

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_reflection, R.id.navigation_settings
            )
        )

        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        // Initialise the ThreeTen Backport library.
        // Backports Java8+ time library to JRE 6 (our current JRE).
        // Will be deprecated in the future with a future Android Studio update.
        // https://developer.android.com/studio/preview/features#j8-desugar
        AndroidThreeTen.init(this)

    }



}
