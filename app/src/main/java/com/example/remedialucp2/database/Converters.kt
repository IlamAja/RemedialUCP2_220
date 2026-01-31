package com.example.remedialucp2.database

import androidx.room.TypeConverter
import com.example.remedialucp2.models.BookStatus
import java.util.Date

class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }

    @TypeConverter
    fun fromBookStatus(status: BookStatus): String {
        return status.name
    }

    @TypeConverter
    fun toBookStatus(value: String): BookStatus {
        return BookStatus.valueOf(value)
    }
}
