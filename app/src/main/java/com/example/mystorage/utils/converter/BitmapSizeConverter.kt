package com.example.mystorage.utils.converter

import android.graphics.Bitmap
import java.io.ByteArrayOutputStream

object BitmapSizeConverter {
    fun convertBitmapSize(bitmap: Bitmap): Bitmap {
        val stream = ByteArrayOutputStream()
        var quality = 100

        while (stream.toByteArray().size / 1024 > 2048) {
            quality -= 10
            stream.reset()
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, stream)
        }

        return bitmap
    }
}