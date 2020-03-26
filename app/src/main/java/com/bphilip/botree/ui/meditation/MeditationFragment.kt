package com.bphilip.botree.ui.meditation

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.bphilip.botree.R
import java.util.concurrent.TimeUnit


class MeditationFragment : Fragment() {

    private lateinit var mTextViewTimer:TextView
    private lateinit var mButtonStart: ImageButton
    private lateinit var mButtonMinutesUp : ImageButton
    private lateinit var mButtonMinutesDown : ImageButton
    private lateinit var mButtonSecondsUp : ImageButton
    private lateinit var mButtonSecondsDown : ImageButton

    private var startTimeInMillis:Long = 600000

    lateinit var dataPasser: OnTimerStart

    override fun onAttach(context: Context) {
        super.onAttach(context)
        dataPasser = context as OnTimerStart
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_meditation, container, false)
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mButtonStart = getView()?.findViewById(R.id.timerStart) as ImageButton
        mButtonMinutesUp = getView()?.findViewById(R.id.minutesUp) as ImageButton
        mButtonMinutesDown = getView()?.findViewById(R.id.minutesDown) as ImageButton
        mButtonSecondsUp = getView()?.findViewById(R.id.secondsUp) as ImageButton
        mButtonSecondsDown = getView()?.findViewById(R.id.secondsDown) as ImageButton

        mButtonStart.setOnClickListener { v -> dataPasser.onTimerStart(startTimeInMillis, v) }
        mButtonMinutesUp.setOnClickListener { startTimeInMillis += 60000; timeUpdater() }
        mButtonMinutesDown.setOnClickListener { startTimeInMillis -= 60000; timeUpdater()  }
        mButtonMinutesDown.setOnLongClickListener { startTimeInMillis -= 60000; timeUpdater(); true  }
        mButtonSecondsUp.setOnClickListener { startTimeInMillis += 5000; timeUpdater()  }
        mButtonSecondsUp.setOnLongClickListener { startTimeInMillis += 5000; timeUpdater(); true  }
        mButtonSecondsDown.setOnClickListener { startTimeInMillis -= 5000; timeUpdater()  }
        mButtonSecondsDown.setOnLongClickListener { startTimeInMillis -= 5000; timeUpdater(); true  }

        timeUpdater()

    }

    private fun timeUpdater() {
        mTextViewTimer = view?.findViewById(R.id.time_meditation) as TextView

        if (startTimeInMillis < 0) {
            startTimeInMillis = 0
        }

        if (startTimeInMillis >= 6000000) {
            mTextViewTimer.setTextSize(TypedValue.COMPLEX_UNIT_SP, 72f)
        }
        else if (startTimeInMillis < 6000000) {
            mTextViewTimer.setTextSize(TypedValue.COMPLEX_UNIT_SP, 88f)
        }

        val timeFormatted = String.format("%02d:%02d",
            TimeUnit.MILLISECONDS.toMinutes(startTimeInMillis),
            TimeUnit.MILLISECONDS.toSeconds(startTimeInMillis) -
                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(startTimeInMillis))
        )

        mTextViewTimer.text = timeFormatted

    }

    interface OnTimerStart {
         fun onTimerStart(timer : Long, v: View)
    }


}