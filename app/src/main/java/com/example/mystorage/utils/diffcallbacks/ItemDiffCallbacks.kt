package com.example.mystorage.utils.diffcallbacks

import androidx.recyclerview.widget.DiffUtil
import com.example.mystorage.data.entity.ItemEntity

class ItemDiffCallbacks : DiffUtil.ItemCallback<ItemEntity>() {
    override fun areItemsTheSame(oldItem: ItemEntity, newItem: ItemEntity): Boolean {
        return oldItem.item_ID == newItem.item_ID
    }

    override fun areContentsTheSame(oldItem: ItemEntity, newItem: ItemEntity): Boolean {
        return oldItem == newItem
    }
}