package com.bphilip.botree

import android.content.Context
import androidx.room.*
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.threeten.bp.*

// Annotates to create a database with the two classes, Reflection and Meditation.
// Don't currently export the schema for migrations.
@Database(entities = [Reflection::class, Meditation::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class ReflectionRoomDatabase : RoomDatabase() {

    abstract fun reflectionDao(): ReflectionDao

    companion object {
        // Singleton prevents multiple instances of database opening at the
        // same time.
        @Volatile
        private var INSTANCE: ReflectionRoomDatabase? = null

        fun getDatabase(
            context: Context
        ): ReflectionRoomDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            // Keeps it thread-safe (i.e. don't use the database until we've gotten the instance).
            synchronized(this) {
                // Creates a Room instance of the database.
                // If the database isn't created, it'll create it for us.
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ReflectionRoomDatabase::class.java,
                    "meditation_database"
                )
                    .build()
                INSTANCE = instance
                return instance
            }
        }
    }
}

/**
 * Ensures timestamps can be converted to and from a data format SQLite can store.
 */

class Converters {
    @TypeConverter
    fun fromDate(value: Long?): LocalDate? {
        return value?.let {
            LocalDate.ofEpochDay(value)
        }

    }

    @TypeConverter
    fun LocalDateToTimestamp(date: LocalDate?): Long? {
        return date?.toEpochDay()
    }

    @TypeConverter
    fun fromTimestamp(value: Long?): LocalDateTime? {
        return value?.let {
            LocalDateTime.ofInstant(
                Instant.ofEpochMilli(it), ZoneId.systemDefault()
            )
        }
    }

    @TypeConverter
    fun LocalDateTimeToTimestamp(date: LocalDateTime?): Long? {
        return date?.atZone(ZoneId.systemDefault())?.toInstant()?.toEpochMilli()
    }
}