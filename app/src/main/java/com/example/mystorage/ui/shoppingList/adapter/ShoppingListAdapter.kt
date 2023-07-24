package com.example.mystorage.ui.shoppingList.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.mystorage.data.entity.ItemEntity
import com.example.mystorage.databinding.RecyclerShoppingListItemBinding
import com.example.mystorage.utils.diffcallbacks.ItemDiffCallbacks
import com.example.mystorage.utils.listener.ShoppingListClickListener

class ShoppingListAdapter(
    private val shoppingListClickListener: ShoppingListClickListener
) : ListAdapter<ItemEntity, ShoppingListAdapter.ViewHolder>(ItemDiffCallbacks()) {

    operator fun get(position: Int): ItemEntity = currentList[position]

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = RecyclerShoppingListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding, shoppingListClickListener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = currentList[holder.absoluteAdapterPosition]
        holder.bind(item)
    }

    inner class ViewHolder(
        private val binding: RecyclerShoppingListItemBinding,
        private val shoppingListClickListener: ShoppingListClickListener
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ItemEntity) = with(binding) {
            this.item = item
            this.clickListener = shoppingListClickListener
            this.position = absoluteAdapterPosition

            executePendingBindings()
        }
    }
}