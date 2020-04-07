package com.bphilip.botree

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebView

class UserGuideView : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        val myWebView = WebView(applicationContext)
        overridePendingTransition(R.anim.nav_default_pop_enter_anim, R.anim.nav_default_pop_exit_anim)
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
        overridePendingTransition(R.anim.nav_default_pop_enter_anim, R.anim.nav_default_pop_exit_anim)
    }


}
