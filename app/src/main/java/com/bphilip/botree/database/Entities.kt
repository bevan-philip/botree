package com.bphilip.botree.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import org.threeten.bp.*

/**
 * Uses Kotlin's sealed classes to allow the data classes to have common properties.
 * We use this to have a common export class for the data classes.
 * See: https://kotlinlang.org/docs/reference/whatsnew11.html#sealed-and-data-classes
 */
sealed class Exportable {
    // Exports a record as a CSV.
    fun csvBody(exportable: Exportable) : String {
        return when (exportable) {
            is Reflection -> String.format("%s, %s, %s\n",
                exportable.id.toString(),
                exportable.reflection,
                exportable.date.toString()
            )
            is Meditation -> String.format("%s, %s, %s\n",
                exportable.id.toString(),
                exportable.duration.toString(),
                exportable.date.toString()
            )
        }
    }

    // Exports the header for a record type.
    fun csvHeader(exportable: Exportable): String {
        return when (exportable) {
            is Reflection -> "id, reflection, date\n"
            is Meditation -> "id, duration, date\n"
        }
    }
}
// The fields for reflections.
@Entity(tableName = "reflections_table")
data class Reflection(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id")  val id : Int = 0,
    @ColumnInfo(name = "reflection") val reflection: String,
    @ColumnInfo(name = "date") val date : LocalDateTime?
) : Exportable()

// The fields of meditations.
@Entity(tableName = "meditations_table")
data class Meditation(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") val id : Int = 0,
    @ColumnInfo(name = "duration") val duration : Long,
    // We don't store the time for meditations so it is easier to determine whether the user
    // meditated on a certain day (as otherwise, we'd have to search from the start to the end).
    @ColumnInfo(name = "date") val date : LocalDate?
) : Exportable()
