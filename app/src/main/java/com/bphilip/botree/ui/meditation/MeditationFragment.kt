package com.bphilip.botree.ui.meditation

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ImageButton
import android.widget.TableRow
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bphilip.botree.*
import com.bphilip.botree.ui.timer.Timer
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter
import kotlin.math.abs


class MeditationFragment : Fragment() {

    private lateinit var meditationViewModel: MeditationViewModel
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

        // Creates the ViewModel.
        meditationViewModel =
            ViewModelProviders.of(this.activity as FragmentActivity).get(MeditationViewModel::class.java)

        val textView: TextView = view.findViewById(R.id.time_meditation)
        // Whenever the startTimeInMillis is updated, change the timer on the screen.
        meditationViewModel.startTimeInMillis.observe(this, Observer {
            textView.text = Utility.timeFormatter(it.toLong(), context as Context)
        })

        sharedPref = activity!!.getSharedPreferences(
            getString(R.string.preference_file_key), Context.MODE_PRIVATE)

        // If we haven't loaded a start time from SharedPreferences already, load it.
        if (meditationViewModel.loadStartTime) {
            meditationViewModel.startTimeInMillis.value = sharedPref.getInt(
                getString(R.string.saved_meditation_timer_key),
                resources.getInteger(R.integer.default_meditation_timer)
            ).toLong()
            Log.i("MeditationFragment", "Loading startTimeInMillis from SharedPrefs.")
            meditationViewModel.loadStartTime = false
        }

        // Hooks the timer and start buttons.
        mButtonStart = getView()?.findViewById(R.id.timerStart) as ImageButton
        mButtonMinutesUp = getView()?.findViewById(R.id.minutesUp) as ImageButton
        mButtonMinutesDown = getView()?.findViewById(R.id.minutesDown) as ImageButton
        mButtonSecondsUp = getView()?.findViewById(R.id.secondsUp) as ImageButton
        mButtonSecondsDown = getView()?.findViewById(R.id.secondsDown) as ImageButton

        // Starts the timer.
        mButtonStart.setOnClickListener {
            // Whenever we start the timer, we assume that they intend to use this time in the future.
            // Therefore we store it.
            with (sharedPref.edit()) {
                putInt(getString(R.string.saved_meditation_timer_key), meditationViewModel.startTimeInMillis.value?.toInt() as Int)
                apply()
            }
            val intent = Intent(activity, Timer::class.java).apply {
                putExtra(EXTRA_TIMER, meditationViewModel.startTimeInMillis.value)
            }
            Log.i("mVM.startTimeInMillis", meditationViewModel.startTimeInMillis.value.toString())
            startActivity(intent)
        }

        // Changes the timer on button press.
        mButtonMinutesUp.setOnClickListener { meditationViewModel.incrementStartTime(resources.getInteger(R.integer.minutes_increment).toLong()) }
        mButtonMinutesDown.setOnClickListener { meditationViewModel.incrementStartTime(-resources.getInteger(R.integer.minutes_increment).toLong())  }
        mButtonSecondsUp.setOnClickListener { meditationViewModel.incrementStartTime(resources.getInteger(R.integer.seconds_increment).toLong()) }
        mButtonSecondsDown.setOnClickListener { meditationViewModel.incrementStartTime(-resources.getInteger(R.integer.seconds_increment).toLong())  }

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

        var tableRow: TableRow = view.findViewById(R.id.tableRow)
        var tableElement = 0

        // Goes through each of the table columns, and hooks them up their relevant day in the
        // List of LiveData.
        for (day in meditationViewModel.meditatedOnDay) {
            val currentValue = (tableRow.virtualChildCount - 1) - tableElement
            val tableTextView: TextView = tableRow.getVirtualChildAt(currentValue) as TextView

            // If the count of meditations is greater-than-1, that means the user must have meditated
            // at least once on that day.
            day.observe(this, Observer {
                if (it < 1) {
                    tableTextView.setBackgroundResource(R.color.negative)
                }
                else {
                    tableTextView.setBackgroundResource(R.color.positive)
                }
            })

            // I would hook this up to the LiveData, as there is a possible inconsistent state when
            // the day ticks, and the LiveData updates but the actual day doesn't. However, if there's
            // no day data, we'll have a broken looking ticker, so there's no way to make it work otherwise.
            tableTextView.text = LocalDate.now().minusDays(tableElement.toLong()).format(DateTimeFormatter.ofPattern("EEE"))

            tableElement += 1
        }

        // Finds the average time.
        val averageTime = view.findViewById<TextView>(R.id.text_average)

        // Hooks it up to the averageMeditation text.
        meditationViewModel.averageMeditation.observe(this, Observer {
            // Stop crashes on initial startup when there is no value for this.
            if (it == null) {
                averageTime.text = "00:00"
            }
            else {
                averageTime.text = Utility.timeFormatter(it, context as Context)
            }
        })

        // Change the current month displayed whenever the weeksBehind value is changed.
        val currentWeek = view.findViewById<TextView>(R.id.text_week)
        meditationViewModel.weeksBehind.observe(this, Observer {
            currentWeek.text = String.format("%s - %s",
                Utility.startOfWeek().minusWeeks(it).format(DateTimeFormatter.ISO_DATE),
                Utility.endOfWeek().minusWeeks(it).format(DateTimeFormatter.ISO_DATE)
            )

            meditationViewModel.changeDates(
                Utility.startOfWeek().minusWeeks(it),
                Utility.endOfWeek().minusWeeks(it)
            )
        })
        // Change the months.
        val decrementButton: ImageButton = view.findViewById(R.id.button_monthsminusone)
        decrementButton.setOnClickListener { meditationViewModel.incrementWeeksBehind(1) }
        val incrementButton: ImageButton = view.findViewById(R.id.button_monthsplusone)
        incrementButton.setOnClickListener { meditationViewModel.incrementWeeksBehind(-1) }

    }



}