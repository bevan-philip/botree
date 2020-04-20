package com.bphilip.botree.ui.user_guide

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebView
import com.bphilip.botree.R

/**
 * Creates the user guide view. Not much to explain here.
 */

class UserGuideView : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        val myWebView = WebView(applicationContext)
        // Ensures it has animations consistent with the ones used throughout the app.
        overridePendingTransition(
            R.anim.nav_default_pop_enter_anim,
            R.anim.nav_default_pop_exit_anim
        )
        myWebView.loadUrl("file:///android_asset/index.html")
        super.onCreate(savedInstanceState)
        setContentView(myWebView)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }


    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(
            R.anim.nav_default_pop_enter_anim,
            R.anim.nav_default_pop_exit_anim
        )
        finish()
    }


}
