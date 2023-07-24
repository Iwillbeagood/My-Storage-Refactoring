package com.example.mystorage.utils.etc

import android.graphics.Bitmap
import android.view.View
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.example.mystorage.R

object BindingUtils {

    @JvmStatic
    @BindingAdapter("android:onLongClick")
    fun setOnLongClickListener(
        view: View,
        func : () -> Unit
    ) {
        view.setOnLongClickListener {
            func()
            return@setOnLongClickListener true
        }
    }

    @JvmStatic
    @BindingAdapter("imageBitmap")
    fun setImageBitmap(imageView: ImageView, bitmap: Bitmap?) {
        if (bitmap != null) {
            imageView.setImageBitmap(bitmap)
        } else {
            imageView.setImageResource(R.drawable.box)
        }
    }

    @JvmStatic
    @BindingAdapter("isVisible")
    fun setIsVisible(view: View, isVisible: Boolean) {
        view.visibility = if (isVisible) View.VISIBLE else View.GONE
    }
}