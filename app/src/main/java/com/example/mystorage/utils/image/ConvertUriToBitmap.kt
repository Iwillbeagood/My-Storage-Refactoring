package com.example.mystorage.utils.image

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import java.io.IOException

object ConvertUriToBitmap {
    fun uriToBitmap(uri: Uri, context: Context): Bitmap? {
        val contentResolver = context.contentResolver
        try {
            val inputStream = contentResolver.openInputStream(uri)
            inputStream?.let {
                // 파일 경로로부터 Bitmap 생성
                return BitmapFactory.decodeStream(it)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return null
    }
}