package com.example.mystorage.utils.image

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import java.io.File

object DecodeFileUtil {
    fun decodeFileWithOrientation(file: File): Bitmap? {
        val exif = ExifInterface(file.absolutePath)
        val orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED)
        val options = BitmapFactory.Options()

        // 이미지 회전 각도에 따라 옵션 설정
        when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> {
                options.inSampleSize = 2
                return BitmapFactory.decodeFile(file.absolutePath, options)?.let { rotateBitmap(it, 90) }
            }
            ExifInterface.ORIENTATION_ROTATE_180 -> {
                options.inSampleSize = 2
                return BitmapFactory.decodeFile(file.absolutePath, options)?.let { rotateBitmap(it, 180) }
            }
            ExifInterface.ORIENTATION_ROTATE_270 -> {
                options.inSampleSize = 2
                return BitmapFactory.decodeFile(file.absolutePath, options)?.let { rotateBitmap(it, 270) }
            }
            else -> {
                options.inSampleSize = 1
                return BitmapFactory.decodeFile(file.absolutePath, options)
            }
        }
    }

    fun rotateBitmap(bitmap: Bitmap, degrees: Int): Bitmap {
        val matrix = Matrix().apply { postRotate(degrees.toFloat()) }
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }
}