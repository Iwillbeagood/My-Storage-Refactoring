package com.example.mystorage.utils.listener

interface OptionsClickListener {
    fun onItemUsed(itemID: Int)
    fun onItemSingleUse(itemID: Int)
    fun onItemEdit(itemID: Int)
    fun onItemDelete(itemID: Int)
}