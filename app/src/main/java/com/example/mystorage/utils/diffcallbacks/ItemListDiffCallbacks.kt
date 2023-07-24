package com.example.mystorage.utils.diffcallbacks

import androidx.recyclerview.widget.DiffUtil
import com.example.mystorage.data.entity.ListItem

class ItemListDiffCallbacks : DiffUtil.ItemCallback<ListItem>() {
    override fun areItemsTheSame(oldItem: ListItem, newItem: ListItem): Boolean {
        return oldItem.itemEntity == newItem.itemEntity
    }

    override fun areContentsTheSame(oldItem: ListItem, newItem: ListItem): Boolean {
        return oldItem == newItem
    }
}