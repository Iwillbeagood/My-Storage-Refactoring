package com.example.mystorage.ui.item.list.adapter


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.mystorage.data.entity.ItemEntity
import com.example.mystorage.databinding.RecyclerListItemBinding
import com.example.mystorage.utils.diffcallbacks.ItemDiffCallbacks
import com.example.mystorage.utils.listener.ItemClickListener

class ItemListAdapter(
    private val listener: ItemClickListener
): ListAdapter<ItemEntity, ItemListAdapter.ViewHolder>(ItemDiffCallbacks()) {

    var isLongClickMode: Boolean = false

    operator fun get(position: Int): ItemEntity = currentList[position]

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = RecyclerListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding, listener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = currentList[holder.absoluteAdapterPosition]
        holder.bind(item)
    }

    inner class ViewHolder(
        private val binding: RecyclerListItemBinding,
        private val listener: ItemClickListener
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ItemEntity) = with(binding) {
            this.item = item
            this.clickListener = listener
            this.position = absoluteAdapterPosition

            this.itemCheckbox.visibility =
                if (isLongClickMode) View.VISIBLE
                else View.GONE

            executePendingBindings()
        }
    }
}
