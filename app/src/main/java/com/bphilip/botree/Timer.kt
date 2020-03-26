package com.bphilip.botree

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.TextView
import org.w3c.dom.Text

class Timer : AppCompatActivity() {

    private var startTimeInMillis:Long = 600000
    private var mTimeLeftInMillis:Long = startTimeInMillis


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_timer)

        startTimeInMillis = intent.getLongExtra(EXTRA_TIMER, 600000)

        val textView = findViewById<TextView>(R.id.countdown_timer).apply {
            text = startTimeInMillis.toString()
        }


    }

//    fun startTimer() {
//        mCountDownTimer = object : CountDownTimer(30000, 1000) {
//            override fun onTick(millisUntilFinished: Long) {
//                mTextField.setText("seconds remaining: " + millisUntilFinished / 1000)
//            }
//
//            override fun onFinish() {
//                mTextField.setText("done!")
//            }
//        }.start()
//
//    }
}
