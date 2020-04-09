package com.bphilip.botree.ui.timer

import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import com.bphilip.botree.EXTRA_TIMER
import com.bphilip.botree.Meditation
import com.bphilip.botree.R
import com.bphilip.botree.Utility
import com.bphilip.botree.ui.meditation.MeditationViewModel
import com.bphilip.botree.ui.post_meditation.PostMeditation
import org.threeten.bp.LocalDate
import kotlin.math.roundToInt


class Timer : AppCompatActivity() {

    private var startTimeInMillis:Long = 600000
    private var mTimeLeftInMillis:Long = startTimeInMillis
    private lateinit var mProgressCountDown : ProgressBar
    private lateinit var mTextViewTimer:TextView

    private var mCountDownStarted = true

    private lateinit var mCountDownTimer: CountDownTimer
    private lateinit var mPauseButton : ImageButton
    private lateinit var mStopButton : ImageButton

    private lateinit var meditationViewModel : MeditationViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_timer)

        startTimeInMillis = intent.getLongExtra(EXTRA_TIMER, 600000)
        mTimeLeftInMillis = startTimeInMillis

        findViewById<TextView>(R.id.countdown_timer).apply {
            text = Utility.timeFormatter(startTimeInMillis, context)
        }

        mProgressCountDown = findViewById(R.id.progress_countdown)
        mPauseButton = findViewById(R.id.pause_button)
        mPauseButton.setOnClickListener {
            if (mCountDownStarted) pauseTimer() else startTimer()
        }

        mStopButton = findViewById(R.id.stop_button)
        mStopButton.setOnClickListener { v -> finishMeditation(v, startTimeInMillis - mTimeLeftInMillis) }

        mProgressCountDown.progress = 100

        meditationViewModel =
            ViewModelProviders.of(this).get(MeditationViewModel::class.java)
        startTimer()

    }

    fun startTimer() {
        mTextViewTimer = findViewById(R.id.countdown_timer)
        mPauseButton.setImageResource(R.drawable.ic_pause)

        mCountDownTimer = object : CountDownTimer(mTimeLeftInMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                mTextViewTimer.text =
                    Utility.timeFormatter(millisUntilFinished + 1000, applicationContext)
                Log.v("CountDown", millisUntilFinished.toString())
                Log.v("Progress", ((millisUntilFinished.toFloat() / startTimeInMillis.toFloat())*100).toString())
                mProgressCountDown.progress =
                    ((millisUntilFinished.toFloat() / startTimeInMillis.toFloat())*100).roundToInt()
                mTimeLeftInMillis = millisUntilFinished
            }

            override fun onFinish() {
                mTextViewTimer.text = "00:00"
                mProgressCountDown.progress = 0
                finishMeditation(findViewById(R.id.progress_countdown), startTimeInMillis)
            }
        }.start()

        mCountDownStarted = true

    }

    private fun pauseTimer() {
        mCountDownTimer.cancel()
        mCountDownStarted = false
        mPauseButton.setImageResource(R.drawable.ic_play_arrow)
    }



    private fun destroyTimer() {
        if (mCountDownTimer != null) {
            mCountDownTimer.cancel()
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        destroyTimer()
    }

    fun finishMeditation(view: View, duration : Long) {
        val intent = Intent(this, PostMeditation::class.java).apply {
            putExtra(EXTRA_TIMER, duration)
        }

        meditationViewModel.insert(
            Meditation(
                0,
                duration,
                LocalDate.now()
            )
        )
        
        val mp: MediaPlayer? = MediaPlayer.create(view.context, R.raw.bell)
        mp?.start()

        startActivity(intent)
        finish()
    }
}
