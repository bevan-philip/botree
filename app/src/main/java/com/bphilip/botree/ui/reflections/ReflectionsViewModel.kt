package com.bphilip.botree.ui.reflections

import android.app.Application
import androidx.lifecycle.*
import com.bphilip.botree.Reflection
import com.bphilip.botree.DataRepository
import com.bphilip.botree.ReflectionRoomDatabase
import kotlinx.coroutines.launch
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDateTime
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.temporal.WeekFields
import java.util.*

class ReflectionsViewModel (application: Application) : AndroidViewModel(application) {

    // The ViewModel maintains a reference to the repository to get data.
    private val repository: DataRepository
    // LiveData gives us updated words when they change.
    val allReflections: LiveData<List<Reflection>>
    var weeksBehind : Long  = 0

    init {
        // Gets reference to WordDao from WordRoomDatabase to construct
        // the correct WordRepository.
        val wordsDao = ReflectionRoomDatabase.getDatabase(application).reflectionDao()
        repository = DataRepository(wordsDao)
        allReflections = repository.allReflections
        changeDates(LocalDate.now().with(WeekFields.of(Locale.getDefault()).dayOfWeek(), 1), LocalDate.now().with(WeekFields.of(Locale.getDefault()).dayOfWeek(), 1).plusDays(6))
    }

    /**
     * The implementation of insert() in the database is completely hidden from the UI.
     * Room ensures that you're not doing any long running operations on
     * the main thread, blocking the UI, so we don't need to handle changing Dispatchers.
     * ViewModels have a coroutine scope based on their lifecycle called
     * viewModelScope which we can use here.
     */
    fun insert(reflection: Reflection) = viewModelScope.launch {
        repository.insertReflection(reflection)
    }

    fun changeDates(start : LocalDate, end : LocalDate) {
        repository.changeTime(start.atTime(0, 0), end.atTime(23, 59))
    }

    private val _text = MutableLiveData<String>().apply {

        value = String.format("%s - %s", LocalDate.now().with(WeekFields.of(Locale.getDefault()).dayOfWeek(), 1).format(
            DateTimeFormatter.ISO_DATE), LocalDate.now().with(WeekFields.of(Locale.getDefault()).dayOfWeek(), 1).plusDays(6).format(
            DateTimeFormatter.ISO_DATE))
    }
    val text: MutableLiveData<String> = _text
}