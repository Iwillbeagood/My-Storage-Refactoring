package com.example.mystorage.ui.item.structure.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.mystorage.data.entity.ItemGroup
import com.example.mystorage.databinding.RecyclerStrItemBinding
import com.example.mystorage.utils.diffcallbacks.ItemGroupDiffCallbacks

class ItemStrAdapter
: ListAdapter<ItemGroup, ItemStrAdapter.ViewHolder>(ItemGroupDiffCallbacks()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = RecyclerStrItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    inner class ViewHolder(
        private val binding: RecyclerStrItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        private val itemStrInnerAdapter = ItemStrInnerAdapter()

        fun bind(itemGroup: ItemGroup) = with(binding) {
            this.itemGroup = itemGroup

            this.strInnerRV.apply {
                this.layoutManager = GridLayoutManager(this.context, 4)
                adapter = itemStrInnerAdapter
            }
            itemStrInnerAdapter.submitList(itemGroup.itemList)
            executePendingBindings()
        }
    }
}