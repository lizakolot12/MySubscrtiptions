package com.mits.subscription.data.db;

import androidx.room.TypeConverter

import java.util.Date

class DateConverter {

    @TypeConverter
    fun fromLong(date: Long): Date? {
        if (date == 0L) {
            return null
        }
        return Date(date)
    }

    @TypeConverter
    fun toString(date: Date?): Long {
        if (date == null) return 0
        return date.time
    }

}
