package com.example.mystorage.utils.converter

import androidx.room.TypeConverter
import java.sql.Timestamp

class TimestampConverter {
    @TypeConverter
    fun fromTimestamp(value: Timestamp?): Long? {
        return value?.time
    }

    @TypeConverter
    fun toTimestamp(value: Long?): Timestamp? {
        return value?.let { Timestamp(it) }
    }
}