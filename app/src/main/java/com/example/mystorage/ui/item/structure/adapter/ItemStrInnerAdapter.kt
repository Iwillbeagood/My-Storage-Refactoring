package com.example.mystorage.ui.item.structure.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mystorage.R
import com.example.mystorage.data.entity.ItemEntity
import com.example.mystorage.databinding.RecyclerStrInnerItemBinding
import com.example.mystorage.databinding.RecyclerStrItemBinding
import com.example.mystorage.utils.diffcallbacks.ItemDiffCallbacks

class ItemStrInnerAdapter
: ListAdapter<ItemEntity, ItemStrInnerAdapter.ViewHolder>(ItemDiffCallbacks())  {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = RecyclerStrInnerItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    inner class ViewHolder(
        private val binding: RecyclerStrInnerItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ItemEntity) = with(binding) {
            this.item = item
            executePendingBindings()
        }
    }
}