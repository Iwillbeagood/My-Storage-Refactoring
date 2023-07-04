package com.example.mystorage.utils.diffcallbacks

import androidx.recyclerview.widget.DiffUtil
import com.example.mystorage.data.entity.InfoName
import com.example.mystorage.data.entity.ItemEntity

class InfoNameDiffCallbacks : DiffUtil.ItemCallback<InfoName>() {
    override fun areItemsTheSame(oldItem: InfoName, newItem: InfoName): Boolean {
        return oldItem.name == newItem.name
    }

    override fun areContentsTheSame(oldItem: InfoName, newItem: InfoName): Boolean {
        return oldItem == newItem
    }
}