package com.bphilip.botree.ui.settings

import android.app.Application
import androidx.lifecycle.*
import com.bphilip.botree.*
import kotlinx.coroutines.launch

class SettingsViewModel (application: Application) : AndroidViewModel(application) {

    lateinit var allMeditations : List<Exportable>
    lateinit var alLReflections : List<Exportable>

    // Because ReflectionsDatabase is a singleton, doesn't really matter we're loading it again.
    // In an ideal system, we'd use Dependency Injection, but not quite there yet.
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