package com.example.mystorage.utils.converter

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import androidx.room.TypeConverter
import java.io.ByteArrayOutputStream

class BitmapConverter {
    @TypeConverter
    fun toByteArray(bitmap : Bitmap?) : ByteArray? {
        val outputStream = ByteArrayOutputStream()
        bitmap?.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        return outputStream.toByteArray()
    }

    // ByteArray -> Bitmap 변환
    @TypeConverter
    fun toBitmap(bytes : ByteArray?) : Bitmap? {
        return BitmapFactory.decodeByteArray(bytes, 0, bytes?.size ?: 0)
    }
}