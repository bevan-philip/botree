package com.bphilip.botree

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.bphilip.botree.NewWordActivity.Companion.EXTRA_REPLY
import com.bphilip.botree.ui.meditation.MeditationViewModel
import com.bphilip.botree.ui.reflections.ReflectionsViewModel
import org.threeten.bp.Duration
import org.threeten.bp.LocalDateTime

class PostMeditation : AppCompatActivity() {

    private lateinit var postMeditationViewModel: PostMeditationViewModel
    private lateinit var reflectionsViewModel: ReflectionsViewModel
    private val newWordActivityRequestCode = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post_meditation)

        postMeditationViewModel =
            ViewModelProviders.of(this).get(PostMeditationViewModel::class.java)

        reflectionsViewModel = ViewModelProviders.of(this).get(ReflectionsViewModel::class.java)

        val textView: TextView = findViewById(R.id.time_meditated_for)

        postMeditationViewModel.text.observe(this, Observer {
            textView.text = it
        })

        if (postMeditationViewModel.pullValue) {
            postMeditationViewModel.meditatedFor = intent.getLongExtra(EXTRA_TIMER, 600000)
            postMeditationViewModel.pullValue = false
        }

        postMeditationViewModel.text.value = Utility.timeFormatter(postMeditationViewModel.meditatedFor)

        var endSessionButton : Button = findViewById(R.id.button_end_session)
        endSessionButton.setOnClickListener { onSupportNavigateUp() }

        var newReflectionButton : Button = findViewById(R.id.button_add_reflection)
        newReflectionButton.setOnClickListener {
            val intent = Intent(applicationContext, NewWordActivity::class.java)
            startActivityForResult(intent, newWordActivityRequestCode)
        }

    }

    override fun onBackPressed() {
        super.onBackPressed()
        onSupportNavigateUp()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        Utility.createReflectionFromIntent(requestCode, resultCode, data, applicationContext, reflectionsViewModel)
    }

}
