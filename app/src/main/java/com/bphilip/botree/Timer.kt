package com.bphilip.botree

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProviders
import com.bphilip.botree.ui.meditation.MeditationViewModel
import org.threeten.bp.Duration
import org.threeten.bp.LocalDateTime
import java.lang.Math.round
import java.util.concurrent.TimeUnit
import kotlin.math.roundToInt

class Timer : AppCompatActivity() {

    private var startTimeInMillis:Long = 600000
    private var mTimeLeftInMillis:Long = startTimeInMillis
    private lateinit var mProgressCountDown : ProgressBar
    private lateinit var mTextViewTimer:TextView

    private var mCountDownStarted = true

    private lateinit var mCountDownTimer: CountDownTimer
    private lateinit var mPauseButton : ImageButton

    private lateinit var meditationViewModel : MeditationViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_timer)

        startTimeInMillis = intent.getLongExtra(EXTRA_TIMER, 600000)
        mTimeLeftInMillis = startTimeInMillis

        val textView = findViewById<TextView>(R.id.countdown_timer).apply {
            text = Utility.timeFormatter(startTimeInMillis)
        }

        mProgressCountDown = findViewById(R.id.progress_countdown)
        mPauseButton = findViewById(R.id.pauseButton)
        mPauseButton.setOnClickListener {
            if (mCountDownStarted) pauseTimer() else startTimer()
        }

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
                mTextViewTimer.text = Utility.timeFormatter(millisUntilFinished)
                Log.v("CountDown", millisUntilFinished.toString())
                Log.v("Progress", ((millisUntilFinished.toFloat() / startTimeInMillis.toFloat())*100).toString())
                mProgressCountDown.progress =
                    ((millisUntilFinished.toFloat() / startTimeInMillis.toFloat())*100).roundToInt()
                mTimeLeftInMillis = millisUntilFinished
            }

            override fun onFinish() {
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

        meditationViewModel.insert(Meditation(0, Duration.ofMillis(duration), LocalDateTime.now()))

        startActivity(intent)
        finish()
    }
}
