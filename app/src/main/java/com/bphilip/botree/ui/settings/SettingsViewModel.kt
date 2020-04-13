package com.bphilip.botree.ui.meditation

import android.app.Application
import androidx.lifecycle.*
import com.bphilip.botree.*
import kotlinx.coroutines.launch

class SettingsViewModel (application: Application) : AndroidViewModel(application) {

    lateinit var allMeditations : List<Meditation>
    lateinit var alLReflections : List<Reflection>

    var reflectionDao: ReflectionDao = ReflectionRoomDatabase.getDatabase(application).reflectionDao()

    init {
        loadMeditations()
        loadReflections()
    }


    private fun loadMeditations() = viewModelScope.launch {
        allMeditations = reflectionDao.getAllMeditations()
    }

    private fun loadReflections() = viewModelScope.launch {
        alLReflections = reflectionDao.getAllReflections()
    }

}