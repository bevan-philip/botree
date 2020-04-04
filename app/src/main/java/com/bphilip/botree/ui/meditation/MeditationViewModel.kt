package com.bphilip.botree.ui.meditation

import android.app.Application
import androidx.lifecycle.*
import com.bphilip.botree.DataRepository
import com.bphilip.botree.Meditation
import com.bphilip.botree.Utility
import com.bphilip.botree.ReflectionRoomDatabase
import kotlinx.coroutines.launch

class MeditationViewModel (application: Application) : AndroidViewModel(application)  {

    // The ViewModel maintains a reference to the repository to get data.
    private val repository: DataRepository
    // LiveData gives us updated words when they change.
    val allMeditations: LiveData<List<Meditation>>
    var startTimeInMillis : Int = 600000

    init {
        // Gets reference to WordDao from WordRoomDatabase to construct
        // the correct WordRepository.
        val wordsDao = ReflectionRoomDatabase.getDatabase(application, viewModelScope).reflectionDao()
        repository = DataRepository(wordsDao)
        allMeditations = repository.allMeditations
    }

    private val _text = MutableLiveData<String>().apply {
        value = Utility.timeFormatter(startTimeInMillis.toLong(), application.applicationContext)
    }
    val text: MutableLiveData<String> = _text

    var loadStartTime = true

    /**
     * The implementation of insert() in the database is completely hidden from the UI.
     * Room ensures that you're not doing any long running operations on
     * the main thread, blocking the UI, so we don't need to handle changing Dispatchers.
     * ViewModels have a coroutine scope based on their lifecycle called
     * viewModelScope which we can use here.
     */
    fun insert(meditation: Meditation) = viewModelScope.launch {
        repository.insertMeditation(meditation)
    }


}