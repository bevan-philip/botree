package com.bphilip.botree.ui.meditation

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.bphilip.botree.MainActivity
import com.bphilip.botree.R

class MeditationFragment : Fragment() {

    private lateinit var meditationViewModel: MeditationViewModel
    private lateinit var mTextViewTimer:TextView
    private lateinit var mButtonStart: ImageButton
    private var startTimeInMillis:Long = 300000

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
            ViewModelProviders.of(this).get(MeditationViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_meditation, container, false)
        val textView: TextView = root.findViewById(R.id.time_meditation)

        meditationViewModel.text.observe(this, Observer {
            textView.text = it
        })
        return root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mTextViewTimer = getView()?.findViewById(R.id.time_meditation) as TextView
        mButtonStart = getView()?.findViewById(R.id.timerStart) as ImageButton

        mButtonStart.setOnClickListener { v -> dataPasser.onTimerStart(startTimeInMillis, v) }
    }

    interface OnTimerStart {
         fun onTimerStart(timer : Long, v: View)
    }


}