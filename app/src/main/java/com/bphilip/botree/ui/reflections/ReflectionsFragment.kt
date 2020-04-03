package com.bphilip.botree.ui.reflections

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
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
import com.google.android.material.floatingactionbutton.FloatingActionButton
import org.threeten.bp.LocalDateTime
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.temporal.WeekFields
import java.util.*

class ReflectionsFragment : Fragment() {

    private lateinit var reflectionsViewModel: ReflectionsViewModel
    private val newWordActivityRequestCode = 1
    private var mContext: Context? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_reflections, container, false)
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerview)
        val adapter = WordListAdapter(activity as Context)
        adapter.notifyDataSetChanged()
        recyclerView?.adapter = adapter
        recyclerView?.layoutManager = LinearLayoutManager(mContext as Context)

        reflectionsViewModel = ViewModelProviders.of(this).get(ReflectionsViewModel::class.java)
        reflectionsViewModel.allReflections.observe(this, Observer { words ->
            // Update the cached copy of the words in the adapter.
            words?.let { adapter.setWords(it) }
        })

        val fab = view.findViewById<FloatingActionButton>(R.id.fab)
        fab?.setOnClickListener {
            val intent = Intent(mContext, NewWordActivity::class.java)
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

        changeTime(0)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == newWordActivityRequestCode && resultCode == Activity.RESULT_OK) {
            data?.getStringExtra(NewWordActivity.EXTRA_REPLY)?.let {
                val word = Reflection(0, it, LocalDateTime.now())
                reflectionsViewModel.insert(word)
            }
        } else {
            Toast.makeText(
                mContext,
                R.string.empty_not_saved,
                Toast.LENGTH_LONG).show()
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onDetach() {
        super.onDetach()
        mContext = null
    }

    fun changeTime(weekChange : Long) {
        reflectionsViewModel.weeksBehind += weekChange

        if (reflectionsViewModel.weeksBehind < 0) {
            reflectionsViewModel.weeksBehind = 0
        }

        val startDate = LocalDateTime.now().minusWeeks(reflectionsViewModel.weeksBehind).with(WeekFields.of(Locale.getDefault()).dayOfWeek(), 1)
        val endDate = LocalDateTime.now().minusWeeks(reflectionsViewModel.weeksBehind).with(WeekFields.of(Locale.getDefault()).dayOfWeek(), 1).plusDays(6)

        reflectionsViewModel.text.value = String.format("%s - %s", startDate.format(DateTimeFormatter.ISO_DATE), endDate.format(DateTimeFormatter.ISO_DATE))

        reflectionsViewModel.changeDates(startDate, endDate)
    }
}