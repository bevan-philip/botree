package com.bphilip.botree

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import org.threeten.bp.*
import java.util.*

@Entity(tableName = "reflections_table")
class Reflection(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id")  val id : Int = 0,
    @ColumnInfo(name = "word") val word: String,
    @ColumnInfo(name = "date") val date : LocalDateTime?
)

@Entity(tableName = "meditations_table")
class Meditation(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") val id : Int = 0,
    @ColumnInfo(name = "duration") val duration : Long,
    @ColumnInfo(name = "date") val date : LocalDate?
)