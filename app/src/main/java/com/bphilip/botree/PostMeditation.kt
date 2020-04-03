package com.bphilip.botree

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.bphilip.botree.ui.meditation.MeditationViewModel
import org.threeten.bp.Duration
import org.threeten.bp.LocalDateTime

class PostMeditation : AppCompatActivity() {

    private lateinit var postMeditationViewModel: PostMeditationViewModel
    private lateinit var meditationViewModel : MeditationViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post_meditation)

        postMeditationViewModel =
            ViewModelProviders.of(this).get(PostMeditationViewModel::class.java)

        meditationViewModel =
            ViewModelProviders.of(this).get(MeditationViewModel::class.java)

        val textView: TextView = findViewById(R.id.time_meditated_for)

        postMeditationViewModel.text.observe(this, Observer {
            textView.text = it
        })

        if (postMeditationViewModel.pullValue) {
            postMeditationViewModel.meditatedFor = intent.getLongExtra(EXTRA_TIMER, 600000)
            postMeditationViewModel.pullValue = false
        }

        postMeditationViewModel.text.value = Utility.timeFormatter(postMeditationViewModel.meditatedFor)

        meditationViewModel.insert(Meditation(0, Duration.ofMillis(postMeditationViewModel.meditatedFor), LocalDateTime.now()))



    }

}
