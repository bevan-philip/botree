package com.bphilip.botree

import android.content.Context
import androidx.room.*
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.threeten.bp.*

// Annotates class to be a Room Database with a table (entity) of the Word class
@Database(entities = [Reflection::class, Meditation::class], version = 2, exportSchema = false)
@TypeConverters(Converters::class)
abstract class ReflectionRoomDatabase : RoomDatabase() {

    abstract fun reflectionDao(): ReflectionDao

    companion object {
        // Singleton prevents multiple instances of database opening at the
        // same time.
        @Volatile
        private var INSTANCE: ReflectionRoomDatabase? = null

        fun getDatabase(
            context: Context,
            scope: CoroutineScope
        ): ReflectionRoomDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ReflectionRoomDatabase::class.java,
                    "word_database"
                )
                    .build()
                INSTANCE = instance
                return instance
            }
        }
    }
}

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