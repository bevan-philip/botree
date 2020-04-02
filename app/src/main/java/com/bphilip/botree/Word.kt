package com.bphilip.botree

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import org.threeten.bp.Instant
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneId
import java.util.*

@Entity(tableName = "word_table")
class Word(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id")  val id : Int = 0,
    @ColumnInfo(name = "word") val word: String,
    @ColumnInfo(name = "date") val date : LocalDateTime?
)