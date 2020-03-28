package com.bphilip.botree.ui.meditation

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MeditationViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "10:00"
    }
    val text: MutableLiveData<String> = _text

    var startTimeInMillis : Int = 600000

    var loadStartTime = true
}