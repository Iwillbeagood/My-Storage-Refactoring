package com.example.mystorage.ui.item.used_item.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.mystorage.data.entity.ItemEntity
import com.example.mystorage.databinding.RecyclerUsedItemBinding
import com.example.mystorage.utils.diffcallbacks.ItemDiffCallbacks
import com.example.mystorage.utils.listener.UsedItemClickListener

class UsedItemAdapter(
    private val listener: UsedItemClickListener
) : ListAdapter<ItemEntity, UsedItemAdapter.ViewHolder>(ItemDiffCallbacks()) {

    operator fun get(position: Int): ItemEntity = currentList[position]

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = RecyclerUsedItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding, listener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = currentList[holder.absoluteAdapterPosition]
        holder.bind(item)
    }

    inner class ViewHolder(
        private val binding: RecyclerUsedItemBinding,
        private val listener: UsedItemClickListener
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ItemEntity) = with(binding) {
            this.item = item
            this.clickListener = listener
            this.position = absoluteAdapterPosition
            executePendingBindings()
        }
    }
}
