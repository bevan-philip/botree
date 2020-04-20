package com.bphilip.botree.ui.reflections

import android.app.Application
import androidx.lifecycle.*
import com.bphilip.botree.Reflection
import com.bphilip.botree.DataRepository
import com.bphilip.botree.ReflectionRoomDatabase
import com.bphilip.botree.Utility
import kotlinx.coroutines.launch
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.temporal.WeekFields
import java.util.*

class ReflectionsViewModel (application: Application) : AndroidViewModel(application) {

    // The ViewModel maintains a reference to the repository to get data.
    private val repository: DataRepository
    // LiveData gives us updated words when they change.
    val allReflections: LiveData<List<Reflection>>
    var weeksBehind : MutableLiveData<Long>  = MutableLiveData<Long>().apply {
        value = 0
    }

    init {
        // Gets reference to WordDao from WordRoomDatabase to construct
        // the correct WordRepository.
        val wordsDao = ReflectionRoomDatabase.getDatabase(application).reflectionDao()
        repository = DataRepository(wordsDao)
        allReflections = repository.allReflections
        changeDates(Utility.startOfWeek(), Utility.endOfWeek())
    }

    // Explanatin can be found in MeditationViewModel.
    fun insert(reflection: Reflection) = viewModelScope.launch {
        repository.insertReflection(reflection)
    }

    fun changeDates(start : LocalDate, end : LocalDate) {
        // Ensure the times are at the start and end of the respective days.
        repository.changeTimeReflections(start.atTime(0, 0), end.atTime(23, 59))
    }

    private val _text = MutableLiveData<String>().apply {
        // Sets the initial text, just in case.
        value = String.format("%s - %s", Utility.startOfWeek().format(
            DateTimeFormatter.ISO_DATE), Utility.endOfWeek().format(
            DateTimeFormatter.ISO_DATE))
    }
    val text: MutableLiveData<String> = _text
}