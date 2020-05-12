package com.bphilip.botree.ui.reflections

import android.app.Application
import androidx.lifecycle.*
import com.bphilip.botree.Reflection
import com.bphilip.botree.DataRepository
import com.bphilip.botree.ReflectionRoomDatabase
import com.bphilip.botree.Utility
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter

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
        val reflectionDao = ReflectionRoomDatabase.getDatabase(application).reflectionDao()
        val meditationDao = ReflectionRoomDatabase.getDatabase(application).meditationDao()
        repository = DataRepository(reflectionDao, meditationDao)
        allReflections = repository.allReflections
        changeDates(Utility.startOfWeek(), Utility.endOfWeek())
    }

    // Explanation can be found in MeditationViewModel.
    fun insert(reflection: Reflection) = viewModelScope.launch (Dispatchers.IO) {
        repository.insertReflection(reflection)
    }

    fun changeDates(start : LocalDate, end : LocalDate) {
        // Ensure the times are at the start and end of the respective days.
        repository.changeTimeReflections(start.atTime(0, 0), end.atTime(23, 59))
    }

    val text = MutableLiveData<String>().apply {
        // Sets the initial text, just in case.
        value = String.format("%s - %s", Utility.startOfWeek().format(
            DateTimeFormatter.ISO_DATE), Utility.endOfWeek().format(
            DateTimeFormatter.ISO_DATE))
    }

    fun changeTime(increment: Long) {
         // Increment the value with the weird method LiveData Ints seem to require it in.
        // And ensure that the value can never hit below 0.
        val currentValue = weeksBehind.value as Long
        if ((currentValue + increment) >= 0) {
            weeksBehind.postValue(
                weeksBehind.value?.plus(
                    increment
                )
            )
        }
    }
}