package com.bphilip.botree.ui.meditation

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.bphilip.botree.R
import com.bphilip.botree.Utility
import java.util.concurrent.TimeUnit


class MeditationFragment : Fragment() {

    private lateinit var meditationViewModel: MeditationViewModel
    private lateinit var mTextViewTimer:TextView
    private lateinit var mButtonStart: ImageButton
    private lateinit var mButtonMinutesUp : ImageButton
    private lateinit var mButtonMinutesDown : ImageButton
    private lateinit var mButtonSecondsUp : ImageButton
    private lateinit var mButtonSecondsDown : ImageButton
    private lateinit var sharedPref : SharedPreferences

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
        meditationViewModel =
            ViewModelProviders.of(activity as FragmentActivity).get(MeditationViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_meditation, container, false)
        val textView: TextView = root.findViewById(R.id.time_meditation)

        meditationViewModel.text.observe(this, Observer {
            textView.text = it
        })

        sharedPref = activity!!.getSharedPreferences(
            getString(R.string.preference_file_key), Context.MODE_PRIVATE)

        if (meditationViewModel.loadStartTime) {
            meditationViewModel.startTimeInMillis = sharedPref?.getInt(
                getString(R.string.saved_meditation_timer_key),
                resources.getInteger(R.integer.default_meditation_timer)
            )
            meditationViewModel.loadStartTime = false
        }
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mButtonStart = getView()?.findViewById(R.id.timerStart) as ImageButton
        mButtonMinutesUp = getView()?.findViewById(R.id.minutesUp) as ImageButton
        mButtonMinutesDown = getView()?.findViewById(R.id.minutesDown) as ImageButton
        mButtonSecondsUp = getView()?.findViewById(R.id.secondsUp) as ImageButton
        mButtonSecondsDown = getView()?.findViewById(R.id.secondsDown) as ImageButton

        mButtonStart.setOnClickListener { v -> dataPasser.onTimerStart(meditationViewModel.startTimeInMillis, v) }
        mButtonMinutesUp.setOnClickListener { meditationViewModel.startTimeInMillis += 60000; timeUpdater() }
        mButtonMinutesDown.setOnClickListener { meditationViewModel.startTimeInMillis -= 60000; timeUpdater()  }
        mButtonMinutesDown.setOnLongClickListener { meditationViewModel.startTimeInMillis -= 60000; timeUpdater(); true  }
        mButtonSecondsUp.setOnClickListener { meditationViewModel.startTimeInMillis += 5000; timeUpdater()  }
        mButtonSecondsUp.setOnLongClickListener { meditationViewModel.startTimeInMillis += 5000; timeUpdater(); true  }
        mButtonSecondsDown.setOnClickListener { meditationViewModel.startTimeInMillis -= 5000; timeUpdater()  }
        mButtonSecondsDown.setOnLongClickListener { meditationViewModel.startTimeInMillis -= 5000; timeUpdater(); true  }

        timeUpdater()
    }

    private fun timeUpdater() {
        mTextViewTimer = view?.findViewById(R.id.time_meditation) as TextView

        if (meditationViewModel.startTimeInMillis < 0) {
            meditationViewModel.startTimeInMillis = 0
        }

        if (meditationViewModel.startTimeInMillis >= R.integer.max_default_font_size) {
            mTextViewTimer.setTextSize(TypedValue.COMPLEX_UNIT_SP, 72f)
        }
        else if (meditationViewModel.startTimeInMillis < R.integer.max_default_font_size) {
            mTextViewTimer.setTextSize(TypedValue.COMPLEX_UNIT_SP, 88f)
        }

        meditationViewModel.text.value = Utility.timeFormatter(meditationViewModel.startTimeInMillis.toLong())

    }

    interface OnTimerStart {
         fun onTimerStart(timer : Int, v: View)
    }

}