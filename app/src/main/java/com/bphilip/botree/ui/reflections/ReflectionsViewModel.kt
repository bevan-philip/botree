package com.bphilip.botree.ui.reflections

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDateTime
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.temporal.WeekFields
import java.util.*

class ReflectionsViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {

        value = String.format("%s - %s", LocalDate.now().with(WeekFields.of(Locale.getDefault()).dayOfWeek(), 1).format(
            DateTimeFormatter.ISO_DATE), LocalDate.now().with(WeekFields.of(Locale.getDefault()).dayOfWeek(), 1).plusDays(6).format(
            DateTimeFormatter.ISO_DATE))
    }
    val text: LiveData<String> = _text
}