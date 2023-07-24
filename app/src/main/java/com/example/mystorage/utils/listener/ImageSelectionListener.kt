package com.example.mystorage.utils.listener

import android.graphics.Bitmap

fun interface ImageSelectionListener {
    fun onImageSelected(imageBitmap: Bitmap)
}