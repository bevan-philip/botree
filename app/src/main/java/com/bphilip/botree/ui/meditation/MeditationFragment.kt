package com.bphilip.botree.ui.meditation

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bphilip.botree.*
import com.bphilip.botree.ui.timer.Timer
import org.threeten.bp.Duration
import org.threeten.bp.LocalDateTime
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

        meditationViewModel =
            ViewModelProviders.of(this).get(MeditationViewModel::class.java)
        val textView: TextView = view.findViewById(R.id.time_meditation)

        meditationViewModel.text.observe(this, Observer {
            textView.text = it
        })

        sharedPref = activity!!.getSharedPreferences(
            getString(R.string.preference_file_key), Context.MODE_PRIVATE)

        if (meditationViewModel.loadStartTime) {
            meditationViewModel.startTimeInMillis = sharedPref.getInt(
                getString(R.string.saved_meditation_timer_key),
                resources.getInteger(R.integer.default_meditation_timer)
            )
            meditationViewModel.loadStartTime = false
        }

        // Hooks the timer and start buttons.
        mButtonStart = getView()?.findViewById(R.id.timerStart) as ImageButton
        mButtonMinutesUp = getView()?.findViewById(R.id.minutesUp) as ImageButton
        mButtonMinutesDown = getView()?.findViewById(R.id.minutesDown) as ImageButton
        mButtonSecondsUp = getView()?.findViewById(R.id.secondsUp) as ImageButton
        mButtonSecondsDown = getView()?.findViewById(R.id.secondsDown) as ImageButton

        // Starts the timer.
        mButtonStart.setOnClickListener { v-> startTimer(v, meditationViewModel.startTimeInMillis) }
        // Allows changing the timer
        mButtonMinutesUp.setOnClickListener { meditationViewModel.startTimeInMillis += 60000; timeUpdater() }
        mButtonMinutesDown.setOnClickListener { meditationViewModel.startTimeInMillis -= 60000; timeUpdater()  }
        mButtonSecondsUp.setOnClickListener { meditationViewModel.startTimeInMillis += 5000; timeUpdater()  }
        mButtonSecondsDown.setOnClickListener { meditationViewModel.startTimeInMillis -= 5000; timeUpdater()  }

        // Ensures the time displayed is accurate on view creation - time value from viewmodel.
        timeUpdater()

        // Find the recyclerView, and adapter.
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerview_meditation)
        val adapter = MeditationListAdapter(activity as Context)
        adapter.notifyDataSetChanged()
        // Attach recyclerView to the adapter.
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(view.context)

        // Update the adapter when the data changes.
        meditationViewModel.allMeditations.observe(this, Observer { words ->
            // Update the cached copy of the words in the adapter.
            words?.let { adapter.setWords(it) }
        })
    }

    private fun timeUpdater() {
        mTextViewTimer = view?.findViewById(R.id.time_meditation) as TextView

        // Ensure the time can't go below 0.
        if (meditationViewModel.startTimeInMillis < 0) {
            meditationViewModel.startTimeInMillis = 0
        }

        // If the passes a threshold, reduce the size of it.
        if (meditationViewModel.startTimeInMillis >= R.integer.max_default_font_size) {
            mTextViewTimer.setTextSize(TypedValue.COMPLEX_UNIT_SP, 72f)
        }
        else if (meditationViewModel.startTimeInMillis < R.integer.max_default_font_size) {
            mTextViewTimer.setTextSize(TypedValue.COMPLEX_UNIT_SP, 88f)
        }

        // And then update the visual time.
        meditationViewModel.text.value = Utility.timeFormatter(meditationViewModel.startTimeInMillis.toLong(), activity as Context)

    }

    /**
     * startTimer
     * Starts the Timer activity, with the duration specified in MeditationFragment.
     */
    private fun startTimer(view: View, duration : Int) {
        val intent = Intent(activity, Timer::class.java).apply {
            putExtra(EXTRA_TIMER, duration.toLong())
        }

        startActivity(intent)
    }


}