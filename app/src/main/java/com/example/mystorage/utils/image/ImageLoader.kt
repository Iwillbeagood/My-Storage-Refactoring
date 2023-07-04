package com.example.mystorage.utils.image

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import android.widget.ImageView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide

object ImageLoader {
    fun ImageView.loadUrl(url: String) {
        Glide.with(this)
            .load(url)
            .into(this)
    }

    fun ImageView.loadBitmap(bitmap: Bitmap) {
        Glide.with(this)
            .load(bitmap)
            .into(this)
    }

    fun ImageView.loadImage(contentUriString: String) {
        val context = context
        val contentResolver = context.contentResolver
        val contentUri = Uri.parse(contentUriString)
        val hasReadPermission = ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
        if (hasReadPermission) {
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, contentUri)
            setImageBitmap(bitmap)
        } else {
            ActivityCompat.requestPermissions(context as Activity, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 1)
        }
    }
}