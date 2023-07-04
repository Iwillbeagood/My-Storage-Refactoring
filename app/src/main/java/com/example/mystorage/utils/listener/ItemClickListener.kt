package com.example.mystorage.utils.listener

interface ItemClickListener {
    fun onItemClick(position: Int, state: Boolean)
    fun onItemLongClick(position: Int, state: Boolean)
}