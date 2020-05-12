package com.bphilip.botree.ui.user_guide

import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import com.bphilip.botree.R

/**
 * Creates the user guide view. Not much to explain here.
 */

class UserGuideView : AppCompatActivity() {

    private lateinit var userGuideWebView: WebView

    override fun onCreate(savedInstanceState: Bundle?) {
        // Initialise userGuide with default user guide page.
        userGuideWebView = WebView(applicationContext)
        userGuideWebView.loadUrl("file:///android_asset/index.html")
        super.onCreate(savedInstanceState)
        setContentView(userGuideWebView)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onBackPressed() {
        // Support going back through pages support.
        if (userGuideWebView.canGoBack()) {
            userGuideWebView.goBack()
        }
        else {
            super.onBackPressed()
            finish()
        }
    }




}
