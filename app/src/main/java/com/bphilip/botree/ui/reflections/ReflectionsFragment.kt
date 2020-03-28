package com.bphilip.botree.ui.reflections

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.bphilip.botree.R

class ReflectionsFragment : Fragment() {

    private lateinit var reflectionsViewModel: ReflectionsViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        reflectionsViewModel =
            ViewModelProviders.of(this).get(ReflectionsViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_reflections, container, false)
        val textView: TextView = root.findViewById(R.id.text_dashboard)
        reflectionsViewModel.text.observe(this, Observer {
            textView.text = it
        })
        return root
    }
}