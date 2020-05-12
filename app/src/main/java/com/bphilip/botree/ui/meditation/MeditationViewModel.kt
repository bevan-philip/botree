package com.bphilip.botree.ui.meditation

import android.app.Application
import androidx.lifecycle.*
import com.bphilip.botree.database.DataRepository
import com.bphilip.botree.database.Meditation
import com.bphilip.botree.Utility
import com.bphilip.botree.database.ReflectionRoomDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.threeten.bp.LocalDate

class MeditationViewModel (application: Application) : AndroidViewModel(application)  {

    // The ViewModel maintains a reference to the repository to get data.
    private val repository: DataRepository
    // LiveData gives us updated words when they change.
    val allMeditations: LiveData<List<Meditation>>
    val averageMeditation: LiveData<Long>
    val meditatedOnDay: List<LiveData<Long>>

    val weeksBehind: MutableLiveData<Long> = MutableLiveData<Long>().apply {
        value = 0
    }

     val startTimeInMillis : MutableLiveData<Long> = MutableLiveData<Long>().apply {
        value = 600000
    }

    init {
        // Gets the reflectionsDao, so the repository talks to the right Dao.
        val reflectionDao = ReflectionRoomDatabase.getDatabase(application).reflectionDao()
        val meditationDao = ReflectionRoomDatabase.getDatabase(application).meditationDao()
        repository = DataRepository(
            reflectionDao,
            meditationDao
        )

        // Looks up all the LiveData values to the LiveData values in the repository.
        allMeditations = repository.allMeditations
        averageMeditation = repository.averageMeditation
        meditatedOnDay = repository.meditatedOnDay
        changeDates(Utility.startOfWeek(), Utility.endOfWeek())
    }

    // Whether to load the startTime from SharedPreferences: we only want to load the value on
    // app launch, otherwise we want to save any modifications the user has made.
    var loadStartTime = true

    // Change the dates for what meditations are displayed.
    fun changeDates(start : LocalDate, end : LocalDate) {
        repository.changeTimeMeditations(start, end)
    }

    /**
     * Uses Kotlin's coroutineScopes to ensure insert is not being run on the main thread.
     * While one could use GlobalScope to also achieve this outcome, it is advised against this.
     * (see: https://medium.com/@elizarov/the-reason-to-avoid-globalscope-835337445abc,
     * https://stackoverflow.com/questions/52581809/how-to-use-coroutines-globalscope-on-the-main-thread),
     * so we use viewModelScope. viewModelScope also any operations operate only on the lifecycle of
     * the viewmodel (i.e. end of viewModel = no more background operations).
     * https://medium.com/androiddevelopers/easy-coroutines-in-android-viewmodelscope-25bffb605471
     */
    fun insert(meditation: Meditation) = viewModelScope.launch(Dispatchers.IO) {
        repository.insertMeditation(meditation)
    }

    fun incrementStartTime(increment: Long) {
        // Increment the value with the weird method LiveData Ints seem to require it in.
        // And ensure that the value can never hit below 0.
        val currentValue = startTimeInMillis.value as Long
        if ((currentValue + increment) >= 0) {
            startTimeInMillis.postValue(
                startTimeInMillis.value?.plus(
                    increment
                )
            )
        }
    }
    fun incrementWeeksBehind(increment: Long) {
        // Same as incrementStartTime, but changes the weeksBehind variable.
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