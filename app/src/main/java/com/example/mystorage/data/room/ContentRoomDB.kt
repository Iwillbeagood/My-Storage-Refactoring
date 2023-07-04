package com.example.mystorage.data.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.mystorage.data.entity.InfoEntity
import com.example.mystorage.data.entity.ItemEntity
import com.example.mystorage.utils.converter.BitmapConverter
import com.example.mystorage.utils.converter.TimestampConverter
import kotlinx.coroutines.CoroutineScope

@Database(
    entities = [
        ItemEntity::class,
        InfoEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(
    value = [
        BitmapConverter::class,
        TimestampConverter::class
    ]
)
abstract class ContentRoomDB : RoomDatabase() {

    abstract fun contentDao(): ContentDAO

    companion object {
        @Volatile
        var INSTANCE: ContentRoomDB? = null

        fun getDatabase(context: Context): ContentRoomDB {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ContentRoomDB::class.java,
                    "content_database")
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}