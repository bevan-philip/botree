package com.bphilip.botree.ui.reflections

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bphilip.botree.R
import com.bphilip.botree.Utility
import com.google.android.material.floatingactionbutton.FloatingActionButton
import org.threeten.bp.LocalDateTime
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.temporal.WeekFields
import java.util.*
import kotlin.math.abs


class ReflectionsFragment : Fragment() {

    private lateinit var reflectionsViewModel: ReflectionsViewModel
    private val newWordActivityRequestCode = 1
    private lateinit var recyclerView: RecyclerView

    val gesture: GestureDetector = GestureDetector(
        activity,
        object : GestureDetector.SimpleOnGestureListener() {
            override fun onDown(e: MotionEvent?): Boolean {
                return true
            }

            override fun onFling(
                e1: MotionEvent, e2: MotionEvent, velocityX: Float,
                velocityY: Float
            ): Boolean {
                Log.i("ReflectionsFragment", "onFling has been called!")
                val SWIPE_MIN_DISTANCE = 120
                val SWIPE_MAX_OFF_PATH = 250
                val SWIPE_THRESHOLD_VELOCITY = 200
                try {
                    if (abs(e1.y - e2.y) > SWIPE_MAX_OFF_PATH) return false
                    if (e1.x - e2.x > SWIPE_MIN_DISTANCE
                        && abs(velocityX) > SWIPE_THRESHOLD_VELOCITY
                    ) {

                        changeTime(-1)
                    } else if (e2.x - e1.x > SWIPE_MIN_DISTANCE
                        && abs(velocityX) > SWIPE_THRESHOLD_VELOCITY
                    ) {
                        changeTime(1)
                    }
                } catch (e: Exception) {
                    // nothing
                }
                return super.onFling(e1, e2, velocityX, velocityY)
            }
        })


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_reflections, container, false)
        root.setOnTouchListener { _, event -> gesture.onTouchEvent(event) }
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Finds the RecyclerView inside the view.
        recyclerView = view.findViewById<RecyclerView>(R.id.recyclerview)
        val adapter =
            ReflectionListAdapter(activity as Context)
        adapter.notifyDataSetChanged()
        // Connects the RecyclerView Adapter to the RecyclerView.
        recyclerView?.adapter = adapter
        recyclerView?.layoutManager = LinearLayoutManager(activity as Context)

        reflectionsViewModel = ViewModelProviders.of(this.activity as FragmentActivity).get(ReflectionsViewModel::class.java)
        // When the LiveData updates, it updates the adapter.
        reflectionsViewModel.allReflections.observe(this, Observer { words ->
            // Update the cached copy of the words in the adapter.
            words?.let { adapter.setWords(it) }
        })

        val fab = view.findViewById<FloatingActionButton>(R.id.fab)
        fab?.setOnClickListener {
            val intent = Intent(activity, NewReflectionActivity::class.java)
            startActivityForResult(intent, newWordActivityRequestCode)
        }

        val textView: TextView = view.findViewById(R.id.textDate)
        reflectionsViewModel.text.observe(this, Observer {
            textView.text = it
        })

        val weeksMinusOne = view.findViewById<ImageButton>(R.id.button_weeksminusone)
        weeksMinusOne.setOnClickListener { changeTime(1) }

        val weeksPlusOne = view.findViewById<ImageButton>(R.id.button_weeksplusone)
        weeksPlusOne.setOnClickListener { changeTime(-1) }

        recyclerView.setOnTouchListener { v, event ->  gesture.onTouchEvent(event); v.performClick() }


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        Utility.createReflectionFromIntent(requestCode, resultCode, data, activity as Context, reflectionsViewModel)

    }

    private fun changeTime(weekChange : Long) {
        reflectionsViewModel.weeksBehind += weekChange

        // Can't store data in the future (from the current date), so no point allowing it.
        if (reflectionsViewModel.weeksBehind < 0) {
            reflectionsViewModel.weeksBehind = 0
        }

        // Finds the start and end dates of the week.
        val startDate = LocalDateTime.now().minusWeeks(reflectionsViewModel.weeksBehind).with(WeekFields.of(Locale.getDefault()).dayOfWeek(), 1)
        val endDate = LocalDateTime.now().minusWeeks(reflectionsViewModel.weeksBehind).with(WeekFields.of(Locale.getDefault()).dayOfWeek(), 1).plusDays(6)

        // Updates the text at the top of the screen.
        reflectionsViewModel.text.value = String.format("%s - %s", startDate.format(DateTimeFormatter.ISO_DATE), endDate.format(DateTimeFormatter.ISO_DATE))

        // Changes the LiveData query.
        reflectionsViewModel.changeDates(startDate, endDate)
        

    }



}