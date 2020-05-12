package com.bphilip.botree.ui.reflections

import android.content.Context
import android.content.Intent
import android.os.Bundle
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
import org.threeten.bp.format.DateTimeFormatter
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
                val SWIPE_MIN_DISTANCE = 120
                val SWIPE_MAX_OFF_PATH = 250
                val SWIPE_THRESHOLD_VELOCITY = 200
                try {
                    if (abs(e1.y - e2.y) > SWIPE_MAX_OFF_PATH) return false
                    if (e1.x - e2.x > SWIPE_MIN_DISTANCE
                        && abs(velocityX) > SWIPE_THRESHOLD_VELOCITY
                    ) {

                        reflectionsViewModel.changeTime(-1)
                    } else if (e2.x - e1.x > SWIPE_MIN_DISTANCE
                        && abs(velocityX) > SWIPE_THRESHOLD_VELOCITY
                    ) {
                        reflectionsViewModel.changeTime(1)
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
        recyclerView = view.findViewById(R.id.recyclerview)
        val adapter =
            ReflectionListAdapter(activity as Context)
        adapter.notifyDataSetChanged()
        // Connects the RecyclerView Adapter to the RecyclerView.
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(activity as Context)

        reflectionsViewModel = ViewModelProviders.of(this.activity as FragmentActivity).get(ReflectionsViewModel::class.java)
        // When the LiveData updates, it updates the adapter.
        reflectionsViewModel.allReflections.observe(this, Observer { words ->
            // Update the cached copy of the words in the adapter.
            words?.let { adapter.setWords(it) }
        })

        val fab = view.findViewById<FloatingActionButton>(R.id.fab)
        fab?.setOnClickListener {
            Intent(activity, NewReflectionActivity::class.java).also { intent ->
                startActivityForResult(intent, newWordActivityRequestCode)
            }
        }

        val textDate: TextView = view.findViewById(R.id.textDate)

        // Change the weeks displayed when weeksBehind changes.
        reflectionsViewModel.weeksBehind.observe(this, Observer {
            textDate.text = String.format("%s - %s",
                Utility.startOfWeek().minusWeeks(it).format(DateTimeFormatter.ISO_DATE),
                Utility.endOfWeek().minusWeeks(it).format(DateTimeFormatter.ISO_DATE))

            reflectionsViewModel.changeDates(Utility.startOfWeek().minusWeeks(it), Utility.endOfWeek().minusWeeks(it))

        })

        // Change the weeksBehind value.
        val weeksMinusOne = view.findViewById<ImageButton>(R.id.button_monthsminusone)
        weeksMinusOne.setOnClickListener { reflectionsViewModel.changeTime(1) }

        val weeksPlusOne = view.findViewById<ImageButton>(R.id.button_monthsplusone)
        weeksPlusOne.setOnClickListener { reflectionsViewModel.changeTime(-1) }

        recyclerView.setOnTouchListener { v, event ->  gesture.onTouchEvent(event); v.performClick() }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        Utility.createReflectionFromIntent(requestCode, resultCode, data, activity as Context, reflectionsViewModel)

    }
}