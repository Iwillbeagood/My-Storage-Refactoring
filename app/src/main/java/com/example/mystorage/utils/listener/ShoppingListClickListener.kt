package com.example.mystorage.utils.listener

import android.graphics.Bitmap

fun interface ShoppingListClickListener {
    fun onShoppingListClicked(position: Int, selected: Boolean)
}