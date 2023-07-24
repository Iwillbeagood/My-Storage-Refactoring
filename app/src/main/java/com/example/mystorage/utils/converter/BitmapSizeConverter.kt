package com.example.mystorage.utils.converter

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.ByteArrayOutputStream

object BitmapSizeConverter {
    fun convertBitmapSize(bitmap: Bitmap): Bitmap {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)

        var quality = 100
        val maxSize = 1024 * 64

        while (stream.size() > maxSize && quality > 0) {
            stream.reset()
            quality -= 5
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, stream)
        }

        val compressedBitmap = BitmapFactory.decodeByteArray(stream.toByteArray(), 0, stream.size())
        stream.close()

        return compressedBitmap
    }
}
