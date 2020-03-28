package com.bphilip.botree

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders

class PostMeditation : AppCompatActivity() {

    private lateinit var postMeditationViewModel: PostMeditationViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post_meditation)

        postMeditationViewModel =
            ViewModelProviders.of(this).get(PostMeditationViewModel::class.java)

        val textView: TextView = findViewById(R.id.time_meditated_for)

        postMeditationViewModel.text.observe(this, Observer {
            textView.text = it
        })

        if (postMeditationViewModel.pullValue) {
            postMeditationViewModel.meditatedFor = intent.getLongExtra(EXTRA_TIMER, 600000)
            postMeditationViewModel.pullValue = false
        }

        postMeditationViewModel.text.value = Utility.timeFormatter(postMeditationViewModel.meditatedFor)



    }

}
