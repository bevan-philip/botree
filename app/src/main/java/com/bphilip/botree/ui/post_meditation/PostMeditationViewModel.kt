package com.bphilip.botree.ui.post_meditation

import androidx.lifecycle.*

class PostMeditationViewModel : ViewModel()  {

    private val _text = MutableLiveData<String>().apply {
        value = "Well done, you have meditated for X minutes!"
    }
    val text: MutableLiveData<String> = _text

    var meditatedFor : Long = 600000

    var pullValue = true

}