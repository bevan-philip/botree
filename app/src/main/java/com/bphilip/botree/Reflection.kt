package com.bphilip.botree

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import org.threeten.bp.*
import java.util.*

// The fields for reflections.
@Entity(tableName = "reflections_table")
class Reflection(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id")  val id : Int = 0,
    @ColumnInfo(name = "word") val word: String,
    @ColumnInfo(name = "date") val date : LocalDateTime?
)

// The fields of meditations.
@Entity(tableName = "meditations_table")
class Meditation(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") val id : Int = 0,
    @ColumnInfo(name = "duration") val duration : Long,
    // We don't store the time for meditations so it is easier to determine whether the user
    // meditated on a certain day (as otherwise, we'd have to search from the start to the end).
    @ColumnInfo(name = "date") val date : LocalDate?
)