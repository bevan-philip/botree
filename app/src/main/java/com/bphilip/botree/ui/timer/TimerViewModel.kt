package com.bphilip.botree.ui.timer

import androidx.lifecycle.*

class TimerViewModel : ViewModel() {

    var pullValue = true

    var mCountDownStarted = true

    var startTimeInMillis:MutableLiveData<Long> = MutableLiveData<Long>().apply {
        value = 60000
    }

    var mTimeLeftInMillis:MutableLiveData<Long> = MutableLiveData<Long>().apply {
        value = startTimeInMillis.value
    }

}