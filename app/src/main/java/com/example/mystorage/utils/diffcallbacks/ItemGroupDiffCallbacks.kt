package com.example.mystorage.utils.diffcallbacks

import androidx.recyclerview.widget.DiffUtil
import com.example.mystorage.data.entity.ItemEntity
import com.example.mystorage.data.entity.ItemGroup

class ItemGroupDiffCallbacks : DiffUtil.ItemCallback<ItemGroup>() {
    override fun areItemsTheSame(oldItem: ItemGroup, newItem: ItemGroup): Boolean {
        return oldItem.group_name == newItem.group_name
    }

    override fun areContentsTheSame(oldItem: ItemGroup, newItem: ItemGroup): Boolean {
        return oldItem == newItem
    }
}