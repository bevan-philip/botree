package com.bphilip.botree.ui.timer

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.bphilip.botree.EXTRA_TIMER
import com.bphilip.botree.MusicService
import com.bphilip.botree.MusicService.Companion.IS_LOOP
import com.bphilip.botree.MusicService.Companion.RESOURCE_NAME
import com.bphilip.botree.database.Meditation
import com.bphilip.botree.R
import com.bphilip.botree.Utility
import com.bphilip.botree.ui.meditation.MeditationViewModel
import com.bphilip.botree.ui.post_meditation.PostMeditation
import org.threeten.bp.LocalDate
import kotlin.math.roundToInt


class Timer : AppCompatActivity() {

    // CountDownTimer instance.
    private lateinit var mCountDownTimer: CountDownTimer
    // Start/Pause button. Needs to be changeable by different functions.
    private lateinit var mPauseButton : ImageButton
    private lateinit var timerViewModel: TimerViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_timer)

        timerViewModel = ViewModelProviders.of(this).get(TimerViewModel::class.java)

        // If we haven't already pulled the value from the intent, pull it.
        if (timerViewModel.pullValue) {
            timerViewModel.pullValue = false
            timerViewModel.startTimeInMillis.value = intent.getLongExtra(EXTRA_TIMER, 600000)
            timerViewModel.mTimeLeftInMillis.value = timerViewModel.startTimeInMillis.value
        }

        val countDownTimer: TextView = findViewById(R.id.countdown_timer)
        val mProgressCountDown: ProgressBar = findViewById(R.id.progress_countdown)
        // Whenever the time left changes, change the progress and the text.
        timerViewModel.mTimeLeftInMillis.observe(this, Observer {
            countDownTimer.text = Utility.timeFormatter(it + 1000, applicationContext )
            mProgressCountDown.progress =
                ((it.toFloat() / timerViewModel.startTimeInMillis.value?.toFloat() as Float)*100).roundToInt()
        })

        // Start or Pause the timer, depending on the current state.
        mPauseButton = findViewById(R.id.pause_button)
        mPauseButton.setOnClickListener {
            if (timerViewModel.mCountDownStarted) pauseTimer() else startTimer()
        }

        // Stop the timer, but still keep the duration meditated.
        val mStopButton: ImageButton = findViewById(R.id.stop_button)
        mStopButton.setOnClickListener { v -> finishMeditation(v,
            timerViewModel.startTimeInMillis.value as Long - timerViewModel.mTimeLeftInMillis.value as Long)
        }

        // Start the timer.
        startTimer()

        val mCheckBox: CheckBox = findViewById(R.id.music_checkbox)

        mCheckBox.setOnClickListener {
            if (mCheckBox.isChecked) {
                Intent(this, MusicService::class.java).apply {
                    putExtra(RESOURCE_NAME, R.raw.optional_music)
                    putExtra(IS_LOOP, true)
                }.also { intent -> startService(intent) }
            }
            else {
                Intent(this, MusicService::class.java).also { intent ->
                    stopService(intent)
                }
            }
        }
    }

    fun startTimer() {
        // Change the button to indicate what the button will do at that moment in time.
        mPauseButton.setImageResource(R.drawable.ic_pause)

        mCountDownTimer = object : CountDownTimer(
                timerViewModel.mTimeLeftInMillis.value as Long,
                1000)
        {
            override fun onTick(millisUntilFinished: Long) {
                Log.v("CountDown", millisUntilFinished.toString())
                timerViewModel.mTimeLeftInMillis.postValue(millisUntilFinished)
            }

            override fun onFinish() {
                // We add one second to the time left to make it match with the progress bar, so we
                // subtract one to ensure it displays 00:00 at the end.
                timerViewModel.mTimeLeftInMillis.postValue(-1000)
                finishMeditation(findViewById(R.id.progress_countdown), timerViewModel.startTimeInMillis.value as Long)
            }
        }.start()

        timerViewModel.mCountDownStarted = true

    }

    private fun pauseTimer() {
        // Cancel the timer. We can simply restart the timer as we've stored the timeLeft.
        mCountDownTimer.cancel()
        timerViewModel.mCountDownStarted = false
        mPauseButton.setImageResource(R.drawable.ic_play_arrow)
    }

    override fun onDestroy() {
        super.onDestroy()

        // Ensure CountDownTimer does not remain active in background.
        mCountDownTimer.cancel()
    }

    fun finishMeditation(view: View, duration : Long) {
        // Create the intent that starts the PostMeditation screen.
        val intent = Intent(this, PostMeditation::class.java).apply {
            putExtra(EXTRA_TIMER, duration)
        }

        // Store the meditation.
        val meditationViewModel =
            ViewModelProviders.of(this).get(MeditationViewModel::class.java)

        meditationViewModel.insert(
            Meditation(
                0,
                duration,
                LocalDate.now()
            )
        )

        // Ensure looping music is stopped
        Intent(this, MusicService::class.java).also { intent2 ->
            stopService(intent2)
        }

        // Play some audio.
        Intent(this, MusicService::class.java).apply {
            putExtra(RESOURCE_NAME, R.raw.bell)
            putExtra(IS_LOOP, false)
        }.also { bellIntent -> startService(bellIntent) }


        // Start the activity with the intent.
        startActivity(intent)
        // And ensure completion.
        finish()
    }
}
