package com.example.mystorage.ui.info.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.mystorage.data.entity.InfoNameEdit
import com.example.mystorage.databinding.RecyclerInfoNameEditItemBinding
import com.example.mystorage.utils.diffcallbacks.InfoNameEditDiffCallbacks

class InfoNameEditAdapter(
) : ListAdapter<InfoNameEdit, InfoNameEditAdapter.ViewHolder>(InfoNameEditDiffCallbacks()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = RecyclerInfoNameEditItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }


    class ViewHolder(
        private val binding: RecyclerInfoNameEditItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(infoNameEdit: InfoNameEdit) = with(binding) {
            this.infoEdit = infoNameEdit
            executePendingBindings()
        }
    }
}