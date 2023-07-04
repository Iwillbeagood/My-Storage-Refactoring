package com.example.mystorage.utils.diffcallbacks

import androidx.recyclerview.widget.DiffUtil
import com.example.mystorage.data.entity.InfoName
import com.example.mystorage.data.entity.InfoNameEdit
import com.example.mystorage.data.entity.ItemEntity

class InfoNameEditDiffCallbacks : DiffUtil.ItemCallback<InfoNameEdit>() {
    override fun areItemsTheSame(oldItem: InfoNameEdit, newItem: InfoNameEdit): Boolean {
        return oldItem.origin_name == newItem.origin_name
    }

    override fun areContentsTheSame(oldItem: InfoNameEdit, newItem: InfoNameEdit): Boolean {
        return oldItem == newItem
    }
}